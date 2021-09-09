package com.hyd.utils;

import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;

public class RedisHelper {
	@Autowired
	private JedisCluster jedis;
}
