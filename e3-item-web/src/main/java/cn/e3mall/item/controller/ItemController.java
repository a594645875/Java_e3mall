package cn.e3mall.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemCatService;
import cn.e3mall.service.ItemService;

@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{id}")
	public String getItemById(@PathVariable Long id,Model model) {
		//根据id查询TbItem
		TbItem tbItem = itemService.getItemById(id);
		//根据id查询TbItemDesc
		E3Result e3Result = itemService.selectTbItemDesc(id);
		TbItemDesc itemDesc = (TbItemDesc) e3Result.getData();
		//封装Item对象
		Item item = new Item(tbItem);
		//传递对象给页面
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", itemDesc);
		//返回逻辑视图
		return "item";
	}
}
