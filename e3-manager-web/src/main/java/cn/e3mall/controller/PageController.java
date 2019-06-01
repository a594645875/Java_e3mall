package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.service.ItemService;

@Controller
public class PageController {
	
	// 显示首页
	@RequestMapping("/")
	public String showIndex() {
		return "index";
	}

	// 到达指定页面
	@RequestMapping("/{page}")
	public String showPage(@PathVariable String page) {
		return page;
	}
}
