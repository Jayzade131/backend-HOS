
package com.org.hosply360.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
public class AppRedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppRedisConfig.class);


    @Value("${redis.host}")
    private String REDIS_HOST;

    @Value("${redis.port}")
    private String REDIS_PORT;

    @Value("${redis.password}")
    private String REDIS_PASSWORD;

    @Value("${redis.timeout}")
    private String REDIS_TIMEOUT;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {

        var redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(REDIS_HOST);
        redisStandaloneConfiguration.setPort(Integer.valueOf(REDIS_PORT));
        redisStandaloneConfiguration.setPassword(REDIS_PASSWORD);

        var jedisClientConfiguration = JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofSeconds(Integer.valueOf(REDIS_TIMEOUT)));// connection timeout

        JedisConnectionFactory factory = new JedisConnectionFactory(redisStandaloneConfiguration,
                jedisClientConfiguration.build());

        try {
            factory.afterPropertiesSet();
            factory.getConnection().ping();
            logger.info("✅ Successfully connected to Redis!");
        } catch (Exception e) {
            logger.error("❌ Failed to connect to Redis!", e);
        }

        return factory;
    }

    @Bean
    public CacheManager appCacheManager(JedisConnectionFactory jedisConnectionFactory) {
        var redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofDays(10))
                .enableTimeToIdle()
                .disableCachingNullValues();

        return RedisCacheManager.builder(
                RedisCacheWriter.nonLockingRedisCacheWriter(jedisConnectionFactory)
        ).cacheDefaults(redisCacheConfiguration).build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
