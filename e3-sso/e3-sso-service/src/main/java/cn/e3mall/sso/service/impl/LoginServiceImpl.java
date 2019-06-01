package cn.e3mall.sso.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.LoginService;
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private TbUserMapper tBUserMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${SESSION}")
	private String SESSION;
	
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;
	
	@Override
	public E3Result login(String username, String password) {
		//检查用户名
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = tBUserMapper.selectByExample(example);
		if (list == null || list.size()== 0) {
			return E3Result.build(400, "用户名或密码错误!");
		}
		TbUser user = list.get(0);
		//检查密码
		if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
			return E3Result.build(400, "用户名或密码错误!");
		}
		//登陆成功后,创建token
		String token = UUID.randomUUID().toString();
		//把token写进redis,key是SESSION:token,value是user的json,密码为null
		user.setPassword(null);
		jedisClient.set(SESSION+":"+token, JsonUtils.objectToJson(user));
		//设置token过期时间
		jedisClient.expire(SESSION+":"+token, SESSION_EXPIRE);
		//返回登陆成功页面
		return E3Result.ok(token);
	}

}
