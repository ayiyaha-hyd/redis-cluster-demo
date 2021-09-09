package com.hyd.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 获取配置文件 Redis 配置
 */
@Component
@ConfigurationProperties(prefix = "spring.redis.cluster")
@Data
@NoArgsConstructor
public class RedisClusterProperties {
	private List<String> nodes;
	private Integer maxAttempts;
	private Integer connectionTimeout;
	private Integer soTimeout;
	private String password;
}