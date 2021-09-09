package com.hyd.utils;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	@Resource
	private ValueOperations<String,Object> valueOperations;

	public RedisUtil() {
	}

	public boolean cacheExist(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	public void insert(String key, Object obj, long expire) {
		valueOperations.set(key, obj, expire, TimeUnit.DAYS);
	}

	public Object select(String key) {
		return cacheExist(key) ? valueOperations.get(key) : new Object();
	}

	public String get(String key) {
		return (String) valueOperations.get(key);
	}

	public void update(String key) {
		if (cacheExist(key)) {
			redisTemplate.delete(key);
		}
	}

	public void delete(String key) {
		if (cacheExist(key)) {
			redisTemplate.delete(key);
		}

	}

	public static void switchDB(RedisTemplate redisTemplate, int index) {
		LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
		if (lettuceConnectionFactory != null) {
			lettuceConnectionFactory.setDatabase(index);
			redisTemplate.setConnectionFactory(lettuceConnectionFactory);
			lettuceConnectionFactory.resetConnection();
		}

	}
}

