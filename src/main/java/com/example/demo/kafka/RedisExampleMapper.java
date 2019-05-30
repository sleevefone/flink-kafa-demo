package com.example.demo.kafka;

import com.ext.redis.mapper.RedisCommand;
import com.ext.redis.mapper.RedisCommandDescription;
import com.ext.redis.mapper.RedisMapper;
import org.apache.flink.api.java.tuple.Tuple2;

public class RedisExampleMapper implements RedisMapper<Tuple2<String, String>> {


    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.SET, null);
    }

    @Override
    public String getKeyFromData(Tuple2<String, String> s) {
        return s.f0;
    }

    @Override
    public String getValueFromData(Tuple2<String, String> s) {
        return s.f1;
    }
}