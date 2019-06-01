package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_CART_PRE}")
	private String REDIS_CART_PRE;
	@Autowired
	private TbItemMapper tbItemMapper;
	/*
	 * 添加商品至购物车
	 * (non-Javadoc)
	 * @see cn.e3mall.cart.service.CartService#addCart(long, long, int)
	 */
	@Override
	public E3Result addCart(long userId, long itemId, int num) {
		//向redis中添加购物车。
		//数据类型是hash key：用户id field：商品id value：商品信息
		//判断商品是否存在
		Boolean hexists =jedisClient.hexists(REDIS_CART_PRE+":"+userId,itemId+"");
		//如果存在数量相加
		if (hexists) {
			String json = jedisClient.hget(REDIS_CART_PRE+":"+userId,itemId+"");
			//把json转换成TbItem
			TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
			tbItem.setNum(tbItem.getNum()+num);
			//写回redis
			jedisClient.hset(REDIS_CART_PRE+":"+userId,itemId+"", JsonUtils.objectToJson(tbItem));
		}
		//如果不存在，根据商品id取商品信息
		TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
		//设置购物车数据量
		tbItem.setNum(num);
		//取一张图片
		String image = tbItem.getImage();
		if (StringUtils.isNotBlank(image)) {
			tbItem.setImage(image.split(",")[0]);
		}
		//添加到购物车列表
		jedisClient.hset(REDIS_CART_PRE+":"+userId,itemId+"", JsonUtils.objectToJson(tbItem));
		return null;
	}
	@Override
	public E3Result mergeCart(long userId, List<TbItem> itemList) {
		//遍历商品列表
		//把列表添加到购物车。
		//判断购物车中是否有此商品(在addCart方法里)
		//如果有，数量相加(在addCart方法里)
		//如果没有添加新的商品(在addCart方法里)
		for (TbItem tbItem : itemList) {
			addCart(userId, tbItem.getId(), tbItem.getNum());
		}
		//返回成功
		return E3Result.ok();
	}
	@Override
	public List<TbItem> getCartListItem(long userId) {
		//根据用户id查询购车列表
		List<String> jsonList = jedisClient.hvals(REDIS_CART_PRE+":"+userId);
		List<TbItem> itemList = new ArrayList<>();
		for (String json : jsonList) {
			//创建一个TbItem对象
			TbItem tbitem = JsonUtils.jsonToPojo(json, TbItem.class);
			//添加到列表
			itemList.add(tbitem);
		}
		return itemList;
	}
	
	@Override
	public E3Result updateCartNum(long userId, long itemId, int num) {
		//从redis中取商品信息
		String json = jedisClient.hget(REDIS_CART_PRE+":"+userId, itemId+"");
		TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
		//更新商品数量
		item.setNum(num);
		//写入redis
		jedisClient.hset(REDIS_CART_PRE+":"+userId, itemId+"", JsonUtils.objectToJson(item));
		//返回成功
		return E3Result.ok();
	}
	@Override
	public E3Result deleteCartItem(long userId, long itemId) {
		//从redis删除对象商品
		jedisClient.hdel(REDIS_CART_PRE+":"+userId, itemId+"");
		return E3Result.ok();
	}
	@Override
	public E3Result cleanCart(long userId) {
		//从redis删除对象商品
		jedisClient.del(REDIS_CART_PRE+":"+userId);
		return E3Result.ok();
	}
	
	
}
