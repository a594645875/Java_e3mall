package cn.e3mall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;


@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;
	
	@Value("${PAGE_ROWS}")
	private Integer PAGE_ROWS;
	
	@RequestMapping("/search")
	public String search(String keyword,@RequestParam(defaultValue="1")Integer page,
			Model model) throws Exception {
		//keyword转码
		keyword = new String(keyword.getBytes("iso8859-1"), "UTF-8");
		//测试用bug
		//int i = 1/0;
		//查询商品信息
		SearchResult result = searchService.search(keyword, page, PAGE_ROWS);
		//传递结果给jsp页面
		model.addAttribute("query", keyword);
		model.addAttribute("totalPages", result.getTotalPages());
		model.addAttribute("itemList", result.getItemList());
		model.addAttribute("recourdCount", result.getRecourdCount());
		model.addAttribute("page", page);
		//返回逻辑视图
		return "search";
	}
}
