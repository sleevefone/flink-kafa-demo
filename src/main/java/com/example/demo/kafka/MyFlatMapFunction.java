package com.example.demo.kafka;

import com.ext.redis.config.FlinkJedisPoolConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import java.util.Arrays;

public class MyFlatMapFunction implements FlatMapFunction<String, Tuple2<String, Integer>> {

    FlinkJedisPoolConfig redis = new FlinkJedisPoolConfig.Builder().setHost("192.168.191.130").build();


    @Override
    public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
//        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
//
//        genericObjectPoolConfig.setMaxIdle(redis.getMaxIdle());
//        genericObjectPoolConfig.setMaxTotal(redis.getMaxTotal());
//        genericObjectPoolConfig.setMinIdle(redis.getMinIdle());
//
//        JedisPool jedisPool = new JedisPool(genericObjectPoolConfig, redis.getHost(),
//                redis.getPort(), redis.getConnectionTimeout(), redis.getPassword(),
//                redis.getDatabase());
//
//        RedisContainer redisContainer = new RedisContainer(jedisPool);
//
//        Map<String, String> mass_condition = redisContainer.hgetAll("mass_condition");
//        System.out.println(mass_condition);

        if (StringUtils.isEmpty(s))
            return;
        String[] split = s.split("\\W+");
        Arrays.asList(split).forEach(o-> collector.collect(new Tuple2<>(o, 1)));

    }
}