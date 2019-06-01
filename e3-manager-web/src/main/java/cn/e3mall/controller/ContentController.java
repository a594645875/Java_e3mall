package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

@Controller
public class ContentController {
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows) {
		EasyUIDataGridResult result = contentService.getContentList(categoryId, page, rows);
		return result;
	}
	
	@RequestMapping("/content/save")
	@ResponseBody
	public E3Result saveContent(TbContent content) {
		E3Result result = contentService.saveContent(content);
		return result;
	}
	
	@RequestMapping("/content/query/{id}")
	@ResponseBody
	public TbContent selectByIdContent(@PathVariable Long id){
		return contentService.selectByIdContent(id);
	}
	
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public E3Result updateContent(TbContent content) {
		return contentService.updateContent(content);
	}
	
	@RequestMapping("/content/delete")
	@ResponseBody
	public E3Result deleteContent(String[] ids) {
		return contentService.deleteContent(ids);
	}
}
