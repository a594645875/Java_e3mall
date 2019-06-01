package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Service
 * <p>Title: ItemServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService{
	@Autowired
	private TbItemMapper tbItemMapper;
	
	@Autowired
	private TbItemDescMapper tbItemDescMapper;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private Destination topicDestination;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${ITEM_INFO_PRE}")
	private String ITEM_INFO_PRE;
	@Value("${ITEM_INFO_EXPIRE}")
	private Integer ITEM_INFO_EXPIRE;
	
	@Override
	public TbItem getItemById(long itemId) {
		try {
			//查询缓存,如果为空则捕捉异常继续运行
			String json = jedisClient.get(ITEM_INFO_PRE + ":" + itemId + ":BASE");
			if (StringUtils.isNotBlank(json)) {
				TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
				return tbItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//方法一根据主键查询
		//TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
		//方法二设置查询条件
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andIdEqualTo(itemId);
		//执行查询
		List<TbItem> list = tbItemMapper.selectByExample(example);
		if (null != list && list.size() > 0) {
			TbItem item = list.get(0);
			try {
				//把数据保存到缓存中
				jedisClient.set(ITEM_INFO_PRE+":"+itemId+":BASE", JsonUtils.objectToJson(item));
				//设置过期时间
				jedisClient.expire(ITEM_INFO_PRE+":"+itemId+":BASE", ITEM_INFO_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return item;
		}
		
		return null;
	}

	@Override
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = tbItemMapper.selectByExample(example);
		//取分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		//创建返回结果对象
		EasyUIDataGridResult result = new EasyUIDataGridResult(pageInfo.getTotal(),list);
		return result;
	}



	@Override
	public E3Result addItem(TbItem item, String desc) {
		// 1、生成商品id
		final long genItemId = IDUtils.genItemId();
		// 2、补全TbItem对象的属性
		item.setId(genItemId);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		// 3、向商品表插入数据
		tbItemMapper.insert(item);
		// 4、创建一个TbItemDesc对象
		TbItemDesc tbItemDesc = new TbItemDesc();
		// 5、补全TbItemDesc的属性
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setCreated(date);
		tbItemDesc.setUpdated(date);
		tbItemDesc.setItemId(genItemId);
		// 6、向商品描述表插入数据
		tbItemDescMapper.insert(tbItemDesc);
		//发送一个商品添加信息
		jmsTemplate.send(topicDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(genItemId+"");
				return textMessage;
			}
		});
		// 7、E3Result.ok()
		return E3Result.ok();
	}

	@Override
	public E3Result deleItem(String ids) {
		//判断ids不为空
		if (StringUtils.isNoneBlank(ids)) {
			//分割ids
			String[] split = ids.split(",");
			for (String id : split) {
				tbItemMapper.deleteByPrimaryKey(Long.valueOf(id));
				tbItemDescMapper.deleteByPrimaryKey(Long.valueOf(id));
			}
			return E3Result.ok();
		}
		return null;
	}

	@Override
	public E3Result selectTbItemDesc(long id) {
		try {
			//查询缓存,如果为空则捕捉异常继续运行
			String json = jedisClient.get(ITEM_INFO_PRE + ":" + id + ":DESC");
			if (StringUtils.isNotBlank(json)) {
				TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return E3Result.ok(tbItemDesc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行查询
		TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(id);
		try {
			//把数据保存到缓存中
			jedisClient.set(ITEM_INFO_PRE+":"+id+":DESC", JsonUtils.objectToJson(tbItemDesc));
			//设置过期时间
			jedisClient.expire(ITEM_INFO_PRE+":"+id+":DESC", ITEM_INFO_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return E3Result.ok(tbItemDesc);
	}

	@Override
	public E3Result selectTbItem(long id) {
		TbItem tbItem = tbItemMapper.selectByPrimaryKey(id);
		return E3Result.ok(tbItem);
	}

	@Override
	public E3Result tbItemInstock(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] splits = ids.split(",");
			for (String id : splits) {
				TbItem tbItem = new TbItem();
				tbItem.setId(Long.valueOf(id));
				//TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(id));
				//商品状态，1-正常，2-下架，3-删除
				tbItem.setStatus((byte) 2);
				//Selective只更新status,没有Selective全部更新
				tbItemMapper.updateByPrimaryKeySelective(tbItem);
			}
			return E3Result.ok();
		}
		return null;
	}

	@Override
	public E3Result tbItemReshelf(String ids) {
		if (StringUtils.isNoneBlank(ids)) {
			String[] splits = ids.split(",");
			for (String id : splits) {
				TbItem tbItem = new TbItem();
				tbItem.setId(Long.valueOf(id));
				//TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(id));
				//商品状态，1-正常，2-下架，3-删除
				tbItem.setStatus((byte) 1);
				//Selective只更新status,没有Selective全部更新
				tbItemMapper.updateByPrimaryKeySelective(tbItem);
			}
			return E3Result.ok();
		}
		return null;
	}

}
