package com.fds.flex.core.portal.connection;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

public class SpringLectureRedisStandaloneConnection extends SpringLectureRedisConnectionBase {

    public SpringLectureRedisStandaloneConnection(String host, int port) {
        super(host, port);
    }

    @Override
    public LettuceConnectionFactory getConnectionFactory() {
        if (host == null || host.equals("")) {
            return new LettuceConnectionFactory();
        }
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(getHost(), getPort());
        connFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);

        return connFactory;
    }

}
