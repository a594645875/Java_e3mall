package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.apache.bcel.generic.ReturnaddressType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper tbUserMapper;
	
	@Override
	public E3Result checkData(String param, int type) {
		// 1、从tb_user表中查询数据
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		// 2、查询条件根据参数动态生成。
		//1、2、3分别代表username、phone、email
		if (type == 1) {
			criteria.andUsernameEqualTo(param);
		} else if (type == 2) {
			criteria.andPhoneEqualTo(param);
		} else if (type == 3) {
			criteria.andEmailEqualTo(param);
		} else {
			return E3Result.build(400, "非法的参数");
		}
		//执行查询
		List<TbUser> list = tbUserMapper.selectByExample(example);
		// 3、判断查询结果，如果查询到数据返回false。
		if (list==null || list.size() == 0) {
			// 4、如果没有返回true。
			return E3Result.ok(true);
		}
		//使用E3result包装,返回结果
		return E3Result.ok(false);
	}

	@Override
	public E3Result register(TbUser user) {
		//数据有效性检验
		//完整性
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())
				||StringUtils.isBlank(user.getPhone())) {
			return E3Result.build(400, "用户数据不完整,注册失败");
		}
		//有效性1用户名2手机号3邮箱
		if (!(boolean) checkData(user.getUsername(), 1).getData()) {
			return E3Result.build(400, "用户名已被注册");
		}
		if (!(boolean) checkData(user.getPhone(), 2).getData()) {
			return E3Result.build(400, "手机号码已被注册");
		}
		/*if (!(boolean) checkData(user.getEmail(), 3).getData()) {
			return E3Result.build(400, "邮箱已被注册");
		}*/
		//补全pojo的属性
		user.setCreated(new Date());
		user.setUpdated(new Date());
		//对密码进行MD5加密
		String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5Password);
		//向数据库插入user
		tbUserMapper.insert(user);
		//返回添加成功
		return E3Result.ok();
	}

}
