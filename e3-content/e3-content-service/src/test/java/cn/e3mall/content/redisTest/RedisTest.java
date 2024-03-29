package cn.e3mall.content.redisTest;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.common.jedis.JedisClient;

public class RedisTest {
	@Test
	public void testJedisClient() throws Exception {
		//初始化Spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		//从容器中获得JedisClient对象
		JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
		jedisClient.set("first", "100");
		//String result = jedisClient.get("first");
		//System.out.println(result);
		String hget = jedisClient.hget("CONTENT_KEY", "89");
		System.out.println(hget);
		
	}
}
