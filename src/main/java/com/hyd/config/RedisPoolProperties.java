package com.hyd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis.jedis.pool")
@Data
public class RedisPoolProperties {
	private long maxActive;
	private long maxWait;
	private int maxIdle;
	private int minIdle;
}
