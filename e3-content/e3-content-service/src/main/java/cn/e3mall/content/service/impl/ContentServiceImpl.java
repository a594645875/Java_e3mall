package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;

@Service
public class ContentServiceImpl implements ContentService{
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;
	
	@Autowired
	private TbContentMapper tbContentMapper;
	
	@Override
	public EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		//创建查询条件
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		//执行查询
		List<TbContent> list = tbContentMapper.selectByExample(example);
		//取分页信息
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		//封装并返回结果
		EasyUIDataGridResult result = new EasyUIDataGridResult(pageInfo.getTotal(), list);
		return result;
	}

	@Override
	public E3Result saveContent(TbContent content) {
		content.setCreated(new Date());
		content.setUpdated(new Date());
		//缓存同步
		jedisClient.hdel(CONTENT_KEY, content.getCategoryId().toString());
		tbContentMapper.insert(content);
		return E3Result.ok();
	}
	//内容描述回显
	@Override
	public TbContent selectByIdContent(Long id) {
		return tbContentMapper.selectByPrimaryKey(id);
	}

	@Override
	public E3Result updateContent(TbContent content) {
		tbContentMapper.updateByPrimaryKeyWithBLOBs(content);
		return E3Result.ok();
	}

	@Override
	public E3Result deleteContent(String[] ids) {
		for (String id : ids) {
			tbContentMapper.deleteByPrimaryKey(Long.valueOf(id));
		}
		return E3Result.ok();
	}

	@Override
	public List<TbContent> selectByCategoryId(Long AD1_LIST_ID) {
		//查询缓存
		try {
			String json = jedisClient.hget(CONTENT_KEY, AD1_LIST_ID + "");
			//判断json是否为空
			if (StringUtils.isNotBlank(json)) {
				//把json转换成list
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//设置查询条件
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(AD1_LIST_ID);
		//查询数据库
		List<TbContent> list = tbContentMapper.selectByExample(example);
		//向缓存中添加数据
		try {
			jedisClient.hset(CONTENT_KEY, AD1_LIST_ID + "", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
		
		
	}

}
