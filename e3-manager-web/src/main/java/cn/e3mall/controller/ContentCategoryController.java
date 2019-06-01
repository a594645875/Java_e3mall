package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;

@Controller
@RequestMapping("/content/category")
public class ContentCategoryController {
	
	@Autowired
	private ContentCategoryService contentCategoryService;
	
	@RequestMapping("/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentList(
			@RequestParam(value="id",defaultValue="0") Long parentId) {
		
		List<EasyUITreeNode> list = contentCategoryService.getContentCategoryList(parentId);
		return list;
	} 
	
	@RequestMapping("/create")
	@ResponseBody
	public E3Result addContentCategory(Long parentId, String name) {
		E3Result result = contentCategoryService.addContentCategory(parentId, name);
		return result;
	}
	
	@RequestMapping("/update")
	@ResponseBody
	public E3Result updateContentCategory(Long id, String name) {
		E3Result result = contentCategoryService.updateContentCategory(id, name);
		return result;
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public E3Result deleteCategory(Long id) {
		E3Result result = contentCategoryService.deleteContentCategory(id);
		return result;
	}
}
