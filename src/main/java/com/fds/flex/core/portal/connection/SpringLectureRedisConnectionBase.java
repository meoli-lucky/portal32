package com.fds.flex.core.portal.connection;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

public class SpringLectureRedisConnectionBase {

    public String host;
    public int port;

    public LettuceConnectionFactory connFactory;

    public SpringLectureRedisConnectionBase(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public LettuceConnectionFactory getConnectionFactory() {
        connFactory = new LettuceConnectionFactory();

        return connFactory;
    }

    public void distroy() {
        if (connFactory != null) {
            connFactory.destroy();
        }

    }

    public void reset() {
        if (connFactory != null) {
            connFactory.resetConnection();
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
