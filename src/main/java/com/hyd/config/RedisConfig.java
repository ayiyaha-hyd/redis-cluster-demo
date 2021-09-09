package com.hyd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class    RedisConfig {
	@Resource
	private RedisClusterProperties clusterProperties;
	@Resource
	private RedisPoolProperties poolProperties;

	/**
	 * redis 集群配置类
	 */
	@Bean
	public RedisClusterConfiguration getClusterConfig() {
		RedisClusterConfiguration rcc = new RedisClusterConfiguration(clusterProperties.getNodes());
		rcc.setMaxRedirects(clusterProperties.getMaxAttempts());
		rcc.setPassword(RedisPassword.of(clusterProperties.getPassword()));
		return rcc;
	}

	@Order(value = 1)
	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxWaitMillis(poolProperties.getMaxWait());
		jedisPoolConfig.setMaxIdle(poolProperties.getMaxIdle());
		jedisPoolConfig.setMinIdle(poolProperties.getMinIdle());
		return jedisPoolConfig;
	}

	/**
	 * redis 集群客户端
	 */
	@Bean
	public JedisCluster getJedisCluster() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		// 截取集群节点
		String[] cluster = clusterProperties.getNodes().toArray(new String[0]);
		// 创建set集合
						Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		// 循环数组把集群节点添加到set集合中
		for (String node : cluster) {
			String[] host = node.split(":");
			//添加集群节点
			nodes.add(new HostAndPort(host[0], Integer.parseInt(host[1])));
		}
		JedisCluster jedisCluster = new JedisCluster(nodes,
				clusterProperties.getConnectionTimeout(),
				clusterProperties.getSoTimeout(),
				clusterProperties.getMaxAttempts(),
				clusterProperties.getPassword(),
				poolConfig);
		return jedisCluster;
	}

	/**
	 * redis 连接工厂
	 */
	@Bean
	public JedisConnectionFactory redisConnectionFactory(RedisClusterConfiguration cluster, JedisPoolConfig jedisPoolConfig) {

		return new JedisConnectionFactory(cluster, jedisPoolConfig);
	}

	/**
	 * redisTemplate (for cluster)
	 */
	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
		//使用 redis集群连接方式
		template.setConnectionFactory(factory);
		//设置key,value序列化器
		template.setKeySerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);
		template.setHashValueSerializer(stringSerializer);
		template.setValueSerializer(jdkSerializationRedisSerializer);
		// template 初始化
		template.afterPropertiesSet();
		return template;
	}

	// Jackson2JsonRedisSerializer
/*	@Bean(name = "redisTemplate2")
	public RedisTemplate<String, Object> redisTemplate2(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper om = new ObjectMapper();
		template.setConnectionFactory(factory);
		template.afterPropertiesSet();
		template.setKeySerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);
		template.setHashValueSerializer(stringSerializer);
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}*/

	@Bean
	public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
		return redisTemplate.opsForHash();
	}

	@Bean
	public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
		return redisTemplate.opsForValue();
	}

	@Bean
	public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
		return redisTemplate.opsForList();
	}

	@Bean
	public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
		return redisTemplate.opsForSet();
	}

	@Bean
	public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
		return redisTemplate.opsForZSet();
	}
}
