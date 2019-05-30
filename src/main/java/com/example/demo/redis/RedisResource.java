//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.demo.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * todo 泛型{@code OUT} 取出redis?
 * @param <OUT>
 */
public class RedisResource<OUT> extends RichParallelSourceFunction<OUT> {



    private JedisPool jedisPool;


    @Override
    public void run(SourceContext<OUT> sourceContext) throws Exception {

    }

    @Override
    public void cancel() {
        jedisPool.getResource().close();
    }

    @Override
    public void open(Configuration parameters) throws Exception {

        String host="xxxxx";
        int port = Protocol.DEFAULT_PORT;
        int timeout = Protocol.DEFAULT_TIMEOUT;
        int database = Protocol.DEFAULT_DATABASE;
        String password;
        int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;
        int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
        int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMaxTotal(maxTotal);
        genericObjectPoolConfig.setMinIdle(minIdle);
        jedisPool = new JedisPool(genericObjectPoolConfig, host,
                6379, timeout, null,
                database);

    }
}
