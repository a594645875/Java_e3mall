package cn.e3mall.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	private CartService cartService;
	@Autowired
	private TokenService tokenService;
	
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		//从cookie中取token
		String token = CookieUtils.getCookieValue(request, "token");
		//1.判断token是否存在
		if (!StringUtils.isNotBlank(token)) {
			//如果token不存在，未登录状态，跳转到sso系统的登录页面。用户登录成功后，跳转到当前请求的url
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL());
			//拦截
			return false;
		}
		//如果token存在，需要调用sso系统的服务，根据token取用户信息
		E3Result e3Result = tokenService.getUserByToken(token);
		//2.如果取不到，用户登录已经过期，需要登录。
		if (e3Result.getStatus() != 200) {
			//如果token不存在，未登录状态，跳转到sso系统的登录页面。用户登录成功后，跳转到当前请求的url
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL());
			//拦截
			return false;
		}
		//如果取到用户信息，是登录状态，需要把用户信息写入request。
		TbUser user = (TbUser) e3Result.getData();
		request.setAttribute("user", user);
		//3.判断cookie中是否有购物车数据，如果有就合并到服务端。
		String cartJson = CookieUtils.getCookieValue(request, "cart",true);
		if (StringUtils.isNotBlank(cartJson)) {
			//合并购物车
			cartService.mergeCart(user.getId(), JsonUtils.jsonToList(cartJson, TbItem.class));
		}
		//放行
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}
	
}
