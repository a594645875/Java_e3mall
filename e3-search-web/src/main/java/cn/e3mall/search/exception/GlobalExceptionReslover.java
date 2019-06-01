package cn.e3mall.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

public class GlobalExceptionReslover implements HandlerExceptionResolver {
	
	Logger logger = LoggerFactory.getLogger(GlobalExceptionReslover.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, 
			Object handler,Exception ex) {
		//打印控制台
		ex.printStackTrace();
		//写日志文件
		logger.debug("测试输出的日志。。。。。。。");
		logger.info("系统发生异常了。。。。。。。");
		logger.error("系统发生异常",ex);
		//发邮件、发短信
		//Jmail：可以查找相关的资料
		//需要在购买短信。调用第三方接口即可。
		//展示错误页面
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", "系统发生异常,请稍后重试");
		modelAndView.setViewName("error/exception");
		return modelAndView;
	}

}
