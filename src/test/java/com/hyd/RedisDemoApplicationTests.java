package com.hyd;

import com.alibaba.fastjson.JSON;
import com.hyd.config.RedisClusterProperties;
import com.hyd.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.core.ClusterOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class RedisDemoApplicationTests {
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private RedisUtil redisUtil;
	@Resource
	private JedisCluster jedisCluster;

	@Resource
	private RedisClusterProperties clusterProperties;

	@Test
	void test() {
		System.out.println(redisTemplate.keys("*"));
	}

	@Test
	void test2() {
		System.out.println(redisTemplate.hasKey("CCYSIGN"));
		System.out.println(redisTemplate.hasKey("CCYCODE"));
		System.out.println(redisTemplate.opsForValue().get("CCYSIGN"));
		RedisOperations operations = redisTemplate.opsForValue().getOperations();
	}

	@Test
	void test3() {
		String[] cluster = clusterProperties.getNodes().toArray(new String[0]);
		Set<RedisClusterNode> nodes = new HashSet<>();
		for (String node : cluster) {
			String[] host = node.split(":");
			nodes.add(new RedisClusterNode(host[0], Integer.parseInt(host[1])));
		}
		ClusterOperations clusterOperations = redisTemplate.opsForCluster();
		int keysCount = 0;
		for (RedisClusterNode node : nodes) {
			String ping = clusterOperations.ping(node);
			System.out.print(node.getPort()+": ");
			System.out.print(ping+",");
			Set keys = clusterOperations.keys(node, "*");
			keysCount +=keys.size();
			System.out.println(keys.size());
			Iterator iterator = keys.iterator();
			List nodeList = new ArrayList();
			while (iterator.hasNext()) {
				nodeList.add(iterator.next());
			}
			System.out.println(nodeList.toString());
		}
		System.out.println("keysCount: "+keysCount);
	}
	@Test
	void test4(){
//		String key = "CCYSIGN";
		String key = "hello";//Ccy,CCYCODE
		boolean flag = redisUtil.cacheExist(key);
		System.out.println(flag);
		if(flag){
			Object select = redisUtil.select(key);
			System.out.println(select);
		}
	}
	@Test
	void test5(){
		redisUtil.insert("hello", JSON.toJSONString("world") , 3600L);
	}

	@Test
	void test6(){
		String[] cluster = clusterProperties.getNodes().toArray(new String[0]);
		Set<RedisClusterNode> nodes = new HashSet<>();
		for (String node : cluster) {
			String[] host = node.split(":");
			nodes.add(new RedisClusterNode(host[0], Integer.parseInt(host[1])));
		}
		ClusterOperations clusterOperations = redisTemplate.opsForCluster();
		int keysCount = 0;
		for (RedisClusterNode node : nodes) {
			String ping = clusterOperations.ping(node);
			System.out.print(node.getPort()+": ");
			System.out.println(ping+",");
			Set keys = clusterOperations.keys(node, "*");
			keysCount +=keys.size();
//			System.out.println(keys.size());
//			Iterator iterator = keys.iterator();
//			List nodeList = new ArrayList();
//			while (iterator.hasNext()) {
//				nodeList.add(iterator.next());
//			}
//			System.out.println(nodeList.toString());
		}
		System.out.println("keysCount: "+keysCount);
	}

	@Test
	public void test7(){
//		Set<String> keys = jedisCluster.keys("*");
		String hello = jedisCluster.get("hello");
		System.out.println(hello);
	}

	@Test
	public void test8(){

	}

}


