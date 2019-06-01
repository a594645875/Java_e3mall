package cn.e3mall.common.pojo;

import java.io.Serializable;
import java.util.List;

import cn.e3mall.common.pojo.SearchItem;

public class SearchResult implements Serializable{
	
	private List<SearchItem> itemList;
	private int totalPages;
	private int recourdCount;
	
	public SearchResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SearchResult(List<SearchItem> itemList, int totalPages, int recourdCount) {
		super();
		this.itemList = itemList;
		this.totalPages = totalPages;
		this.recourdCount = recourdCount;
	}
	public List<SearchItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<SearchItem> itemList) {
		this.itemList = itemList;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public int getRecourdCount() {
		return recourdCount;
	}
	public void setRecourdCount(int recourdCount) {
		this.recourdCount = recourdCount;
	}

}
