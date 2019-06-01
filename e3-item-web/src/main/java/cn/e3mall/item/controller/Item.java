package cn.e3mall.item.controller;

import org.apache.commons.lang3.StringUtils;

import cn.e3mall.pojo.TbItem;

public class Item extends TbItem {

	public String[] getImages() {
		String image2 = this.getImage();
		if (StringUtils.isNotBlank(image2)) {
			String[] images = image2.split(",");
			return images;
		}
		return null;
	}

	public Item() {
	}

	public Item(TbItem tbItem) {
		this.setBarcode(tbItem.getBarcode());
		this.setCid(tbItem.getCid());;
		this.setCreated(tbItem.getCreated());;
		this.setId(tbItem.getId());;
		this.setImage(tbItem.getImage());;
		this.setNum(tbItem.getNum());
		this.setPrice(tbItem.getPrice());
		this.setSellPoint(tbItem.getSellPoint());
		this.setStatus(tbItem.getStatus());
		this.setTitle(tbItem.getTitle());
		this.setUpdated(tbItem.getUpdated());
	}
}
