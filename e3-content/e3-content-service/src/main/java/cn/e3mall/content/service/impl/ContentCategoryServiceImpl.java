package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService{
	
	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;

	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		// 1、取查询参数id，parentId
		// 2、根据parentId查询tb_content_category，查询子节点列表。
		TbContentCategoryExample example = new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		// 3、得到List<TbContentCategory>
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		// 4、把列表转换成List<EasyUITreeNode>ub
		List<EasyUITreeNode> resultList = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			//添加到列表
			resultList.add(node);
		}
		return resultList;
	}

	@Override
	public E3Result addContentCategory(long parentId, String name) {
		// 1、接收两个参数：parentId、name
		// 2、向tb_content_category表中插入数据。
		// a)创建一个TbContentCategory对象
		TbContentCategory tbContentCategory = new TbContentCategory();
		// b)补全TbContentCategory对象的属性
		tbContentCategory.setCreated(new Date());
		tbContentCategory.setIsParent(false);
		tbContentCategory.setName(name);
		tbContentCategory.setParentId(parentId);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		tbContentCategory.setSortOrder(1);
		//状态。可选值:1(正常),2(删除)
		tbContentCategory.setStatus(1);
		tbContentCategory.setUpdated(new Date());
		// c)向tb_content_category表中插入数据,插入的同时获得主键id
		contentCategoryMapper.insert(tbContentCategory);
		// 3、判断父节点的isparent是否为true，不是true需要改为true。
		TbContentCategory parentNode = contentCategoryMapper.selectByPrimaryKey(parentId);
		//更新父节点
		if (!parentNode.getIsParent()) {
			parentNode.setIsParent(true);
			contentCategoryMapper.updateByPrimaryKey(parentNode);
		}
		// 4、需要主键返回。在c)已获得
		// 5、返回E3Result，其中包装TbContentCategory对象
		return E3Result.ok(tbContentCategory);
	}

	@Override
	public E3Result updateContentCategory(long id,String name) {
		TbContentCategory tbContentCategory = new TbContentCategory();
		tbContentCategory.setId(Long.valueOf(id));
		tbContentCategory.setName(name);
		contentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
		return E3Result.ok();
	}

	@Override
	public E3Result deleteContentCategory(long id) {
		//方法一:根据id删除节点,并递归删除其子节点
		//deleCategoryAndChrildNode1(id);
		//return E3Result.ok();
		//方法二:根据id删除节点,若有子节点提示先删除子节点
		E3Result result = deleCategoryAndChrildNode2(id);
		return result;
	}
	//方法二:根据id删除节点,若有子节点提示"先删除子节点"
	private E3Result deleCategoryAndChrildNode2(long id) {
		//获得该节点
		TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		//判断该节点下是为父节点
		if (tbContentCategory.getIsParent()) {
			//是父节点则提示"请先删除子节点"
			E3Result result = new E3Result();
			result.setMsg("请先删除子节点");
			result.setStatus(100);
			return result;
		}
		//2.判断该节点的父节点下是否还有其他的子节点,只有该节点时size==1
		if (getChirldNodeList(tbContentCategory.getParentId()).size()==1) {
			//没有其他子节点,将父节点该为叶子节点
			TbContentCategory parentNode = contentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
			parentNode.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKey(parentNode);
		}
		//3.删除本节点
		contentCategoryMapper.deleteByPrimaryKey(id);
		return E3Result.ok();
	}

	//方法一根据id删除节点,并递归删除其子节点
	private void deleCategoryAndChrildNode1(long id) {
		//获得要删除的节点
		TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(id);
		//1.判断是否为父节点
		if (tbContentCategory.getIsParent()) {
			//是父节点则获得其所有子节点
			List<TbContentCategory> chirldNodeList = getChirldNodeList(id);
			//删除所有的子节点
			for (TbContentCategory chirldNode : chirldNodeList) {
				deleCategoryAndChrildNode1(chirldNode.getId());
			}
		}
		//2.判断该节点的父节点下是否还有其他的子节点,只有该节点时size==1
		if (getChirldNodeList(tbContentCategory.getParentId()).size()==1) {
			//没有其他子节点,将父节点该为叶子节点
			TbContentCategory parentNode = contentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
			parentNode.setIsParent(false);
			contentCategoryMapper.updateByPrimaryKey(parentNode);
		}
		//3.删除本节点
		contentCategoryMapper.deleteByPrimaryKey(id);
	}
	
	//获得该节点下所有的子节点
	private List<TbContentCategory> getChirldNodeList(long id) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(id);
		List<TbContentCategory> chirldNodeList = contentCategoryMapper.selectByExample(example);
		return chirldNodeList;
	}
}
