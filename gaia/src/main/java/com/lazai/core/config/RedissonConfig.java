package com.lazai.core.config;


import lombok.extern.log4j.Log4j2;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class RedissonConfig extends CachingConfigurerSupport {

    @Value("${spring.redisson.profiles}")
    private String profiles;

    /**
     * 功能描述: 哨兵模式
     *
     * @return org.redisson.api.RedissonClient
     * @see <a href="https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95">https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95</a>
     */
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws Exception {
        log.info("loading redisson config: {}", profiles);
        Config config = Config.fromYAML(RedissonConfig.class.getClassLoader().getResource(profiles));
        config.setThreads(0);
        config.setTransportMode(TransportMode.NIO);
        // String序列化
        config.setCodec(StringCodec.INSTANCE);
        return Redisson.create(config);
    }

}
