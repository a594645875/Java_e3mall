package cn.e3mall.service;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;

public interface ItemService {
	//根据id查询商品信息
	TbItem getItemById(long id);
	//根据页码和行数查询商品信息
	EasyUIDataGridResult getItemList(Integer page, Integer rows);
	//添加商品
	E3Result addItem(TbItem item, String desc);
	//删除商品
	E3Result deleItem(String ids);
	//异步重新加载回显商品描述
	E3Result selectTbItemDesc(long id);
	//异步重新加载回显商品规格
	E3Result selectTbItem(long id);
	//商品下架
	E3Result tbItemInstock(String ids);
	//商品上架
	E3Result tbItemReshelf(String ids);
}

