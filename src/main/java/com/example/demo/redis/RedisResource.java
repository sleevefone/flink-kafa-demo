////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package com.example.demo.redis;
//
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
//import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
//import org.apache.flink.streaming.connectors.redis.common.container.RedisCommandsContainer;
//import org.apache.flink.streaming.connectors.redis.common.container.RedisCommandsContainerBuilder;
//import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
//import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
//import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
//import org.apache.flink.util.Preconditions;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//
///**
// * todo 泛型{@code OUT} 取出redis?
// * @param <OUT>
// */
//public class RedisResource<OUT> extends RichSinkFunction<OUT> {
//    private static final long serialVersionUID = 1L;
//    private static final Logger LOG = LoggerFactory.getLogger(RedisResource.class);
//    private FlinkJedisConfigBase flinkJedisConfigBase;
//    private RedisCommandsContainer redisCommandsContainer;
//
//    public RedisResource(FlinkJedisConfigBase flinkJedisConfigBase) {
////        Preconditions.checkNotNull(flinkJedisConfigBase, "Redis connection pool config should not be null");
//        Preconditions.checkNotNull(redisSinkMapper, "Redis Mapper can not be null");
////        Preconditions.checkNotNull(redisSinkMapper.getCommandDescription(), "Redis Mapper data type description can not be null");
//        this.flinkJedisConfigBase = flinkJedisConfigBase;
//    }
//
//
//    /**
//     * todo 泛型{@code OUT} 取出redis?
//     */
//    public void invoke(OUT output) throws Exception {
////        String key = this.redisSinkMapper.getKeyFromData(output);
////        String value = this.redisSinkMapper.getValueFromData(output);
////        redisCommandsContainer.
//
//
//
//    }
//
//    public void open(Configuration parameters) throws Exception {
//        this.redisCommandsContainer = RedisCommandsContainerBuilder.build(this.flinkJedisConfigBase);
//    }
//
//    public void close() throws IOException {
//        if (this.redisCommandsContainer != null) {
//            this.redisCommandsContainer.close();
//        }
//
//    }
//}
