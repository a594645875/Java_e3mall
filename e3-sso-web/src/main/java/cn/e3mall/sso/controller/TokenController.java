package cn.e3mall.sso.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.sso.service.TokenService;

@Controller
public class TokenController {

	@Autowired
	private TokenService tokenService;
	
	/*@RequestMapping("/user/token/{token}")
	@ResponseBody
	public E3Result getUserByToken(@PathVariable String token) {
		return tokenService.getUserByToken(token);
	}*/
	
	
	/*
	//jsop方法一
	@RequestMapping("/user/token/{token}")
		produces=MediaType.APPLICATION_JSON_UTF8_VALUE+"application/json;charset=utf-8");
	@ResponseBody
	public String getUserByToken(@PathVariable String token, String callback) {
		E3Result result = tokenService.getUserByToken(token);
		//返回前判断是否为jsonp请求,组装一个jsonp语句
		if (StringUtils.isNotBlank(callback)) {
			return callback + "(" + JsonUtils.objectToJson(result) + ")";
		}
	}*/
	
	//jsonp方法二
	@RequestMapping(value="/user/token/{token}")
	@ResponseBody
	public Object getUserByToken(@PathVariable String token, String callback) {
		E3Result result = tokenService.getUserByToken(token);
		//返回前判断是否json请求
		if (StringUtils.isNotBlank(callback)) {
			//把结果封装成一个js语句响应
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
		return result;
	}
	
	@RequestMapping("/user/logout/{token}")
	public void deleteToken(@PathVariable String token,HttpServletResponse response) {
		try {
			//安全退出,删除token中的key
			tokenService.deleteToken(token);
			//返回主页
			response.sendRedirect("http://localhost:8082");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
