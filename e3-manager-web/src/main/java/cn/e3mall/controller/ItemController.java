package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
/**
 * 商品管理Controller
 * <p>Title: ItemController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Controller
public class ItemController {
	@Autowired
	public ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	private TbItem getItemById(@PathVariable Long itemId){
		TbItem tbItem = itemService.getItemById(itemId);
		return tbItem;
	}
	
	// 商品列表
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		EasyUIDataGridResult result = itemService.getItemList(page, rows);
		return result;
	}
	
	//添加商品
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public E3Result saveItem(TbItem item,String desc) {
		E3Result result = itemService.addItem(item, desc);
		return result;
	}
	
	//删除商品
	@RequestMapping(value="/rest/item/delete",method=RequestMethod.POST)
	@ResponseBody
	public E3Result deleItem(String ids) {
		E3Result result = itemService.deleItem(ids);
		return result;
		
	}
	
	/**
	 * 异步重新加载回显商品描述
	 * @param id
	 * @return
	 */
	@RequestMapping("/rest/item/query/item/desc/{id}")
	@ResponseBody
	public E3Result selectTbItemDesc(@PathVariable long id) {
		E3Result result = itemService.selectTbItemDesc(id);
		return result;
	}
	
	/**
	 * 异步重新加载回显商品规格
	 * @param id
	 * @return
	 */
	@RequestMapping("/rest/item/param/item/query/{id}")
	@ResponseBody
	public E3Result selectTbItem(@PathVariable long id) {
		E3Result result = itemService.selectTbItem(id);
		return result;
	}
	
	/**
	 * 商品下架
	 * @param ids
	 * @return
	 */
	@RequestMapping("/rest/item/instock")
	@ResponseBody
	public E3Result tbItemInstock(String ids) {
		E3Result result = itemService.tbItemInstock(ids);
		return result;
	}
	
	@RequestMapping("/rest/item/reshelf")
	@ResponseBody
	public E3Result tbItemReshelf(String ids) {
		E3Result result = itemService.tbItemReshelf(ids);
		return result;
	}
}


