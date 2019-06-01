package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;

@Controller
public class CartController {

	@Autowired
	private ItemService itemService;

	@Autowired
	private CartService cartService;
	
	@Value("${COOKIE_CART_EXPIRE}")
	private Integer COOKIE_CART_EXPIRE;

	/*
	 * 将商品添加到购物车
	 * 登陆就写进redis
	 * 没登陆写进cookie,过期时间1800秒
	 */
	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId,@RequestParam(defaultValue="1")Integer num,
			HttpServletRequest request,HttpServletResponse response) {
		//判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("user");
		//如果是登录状态，把购物车写入redis
		if (user != null) {
			//保存到服务端
			cartService.addCart(user.getId(), itemId, num);
			//返回逻辑视图
			return "cartSuccess";
		}
		//如果未登录使用cookie
		//从cookie中取购物车列表
		List<TbItem> list = getCartListFromCookie(request);
		//判断商品在商品列表中是否存在
		boolean flag = false;
		for (TbItem tbItem : list) {
			//如果存在数量相加
			if (tbItem.getId() == itemId.longValue()) {
				flag=true;
				//找到商品，数量相加
				tbItem.setNum(tbItem.getNum()+num);
				//跳出循环
				break;
			}
		}
		//如果不存在
		if (!flag) {
			//根据商品id查询商品信息。得到一个TbItem对象
			E3Result itemResult = itemService.selectTbItem(itemId);
			TbItem item = (TbItem) itemResult.getData();
			//设置商品数量
			item.setNum(num);
			//取一张图片
			item.setImage(item.getImage().split(",")[0]);
			//把商品添加到商品列表
			list.add(item);
		}
		//写入cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list), COOKIE_CART_EXPIRE, true);
		//返回添加成功页面
		return "cartSuccess";
	}
	/*
	 * 从Cookie中取购物车列表的方法
	 */
	private List<TbItem> getCartListFromCookie(HttpServletRequest request) {
		String json = CookieUtils.getCookieValue(request, "cart", true);
		//判断json是否为空
		if (StringUtils.isBlank(json)) {
			return new ArrayList<>();
		}
		//把json转换为商品列表
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	
	/*
	 * 展示购物车列表
	 */
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request,HttpServletResponse response) {
		//判断用户是否为登录状态
		TbUser user = (TbUser) request.getAttribute("user");
		//从cookie中取购物车列表
		List<TbItem> cartList = getCartListFromCookie(request);
		//如果是登录状态
		if (user != null) {
			//如果cookie中购物车列表不为空，把cookie中的购物车商品和服务端的购物车商品合并。
			cartService.mergeCart(user.getId(), cartList);
			//把cookie中的购物车删除
			CookieUtils.deleteCookie(request, response, "cart");
			//从服务端取购物车列表
			cartList = cartService.getCartListItem(user.getId());
		}
		//把列表传递给页面
		request.setAttribute("cartList", cartList);
		//返回逻辑视图
		return "cart";
	}
	
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateCartNum(@PathVariable Long itemId,@PathVariable Integer num,
			HttpServletRequest request,HttpServletResponse response) {
		//检查用户是否登陆,如登陆修改redis
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			cartService.updateCartNum(user.getId(), itemId, num);
			return E3Result.ok();
		}
		//如没登陆修改cookie
		//从Cookie中取购物车商品列表
		List<TbItem> cartList = getCartListFromCookie(request);
		for (TbItem tbItem : cartList) {
			//遍历,修改对应的商品的数量
			if (tbItem.getId().longValue() == itemId) {
				tbItem.setNum(num);
				//完成数量修改,跳出遍历
				break;
			}
		}
		//写入cookie,更新过期时间
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList),COOKIE_CART_EXPIRE,true);
		//返回成功
		return E3Result.ok();
	}
	
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,
			HttpServletRequest request,HttpServletResponse response) {
		//判断用户是否登陆,若登陆,从redis中删除对应商品
		TbUser user = (TbUser) request.getAttribute("user");
		if (user != null) {
			cartService.deleteCartItem(user.getId(), itemId);
			//逻辑视图,展示购物车列表的方法
			return "redirect:/cart/cart.html";
		}
		//若没登陆,取cookie中的购物车,遍历,删除对应的商品
		List<TbItem> cartList = getCartListFromCookie(request);
		for (TbItem tbItem : cartList) {
			if (itemId == tbItem.getId().longValue()) {
				cartList.remove(tbItem);
				break;
			}
		}
		//把购物车写回cookie中
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
		//逻辑视图,展示购物车列表的方法
		return "redirect:/cart/cart.html";
	}
}
