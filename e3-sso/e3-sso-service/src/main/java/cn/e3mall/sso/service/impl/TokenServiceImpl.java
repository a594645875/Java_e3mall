package cn.e3mall.sso.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;
@Service
public class TokenServiceImpl implements TokenService {

	@Autowired
	private JedisClient jedisClient;
	
	@Value("${SESSION}")
	private String SESSION;
	
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	
	@Override
	public E3Result getUserByToken(String token) {
		//根据tonken在redis中查找用户
		String json = jedisClient.get(SESSION+":"+token);
		//查找不到数据,返回登陆过期
		if (StringUtils.isBlank(json)) {
			return E3Result.build(400, "登陆已过期,请重新登陆.");
		}
		//查询有 数据,重新设置过期时间
		jedisClient.expire(SESSION+":"+token, SESSION_EXPIRE);
		//json转TbUser
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return E3Result.ok(user);
	}

	@Override
	public E3Result deleteToken(String token) {
		jedisClient.del(SESSION+":"+token);
		return E3Result.ok();
	}
	

}
