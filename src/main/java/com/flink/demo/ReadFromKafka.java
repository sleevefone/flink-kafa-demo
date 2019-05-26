package com.flink.demo;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReadFromKafka {

    public static void main(String[] args) throws Exception {
// create execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Map<String, String> properties = new HashMap<>();
//        properties.put("bootstrap.servers", "192.168.191.130:9092");
        properties.put("bootstrap.servers", "172.16.143.147:9092");
        properties.put("group.id", "group_test_stephan");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "300000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("topic", "test2");
        // parse user parameters

        ParameterTool parameterTool = ParameterTool.fromMap(properties);

        String topic = parameterTool.getRequired("topic");
        Properties properties1 = parameterTool.getProperties();
        SimpleStringSchema simpleStringSchema = new SimpleStringSchema();
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(topic, simpleStringSchema, properties1);
        DataStream<String> messageStream = env.addSource(consumer);
        messageStream.print();
        env.execute();


        StateTtlConfig ttlConfig = StateTtlConfig
                .newBuilder(Time.days(7))
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();

        ValueStateDescriptor<Long> lastUserLogin =
                new ValueStateDescriptor<>("lastUserLogin", Long.class);

        lastUserLogin.enableTimeToLive(ttlConfig);
    }

    //指定Redis key并将flink数据类型映射到Redis数据类型
    public static final class RedisExampleMapper implements RedisMapper<Tuple5<Long, Long, Long, String, Long>> {
        //设置数据使用的数据结构 HashSet 并设置key的名称
        public RedisCommandDescription getCommandDescription() {
            return new RedisCommandDescription(RedisCommand.HSET, "flink");
        }
        /**
         * 获取 value值 value的数据是键值对
         * @param data
         * @return
         */
        //指定key
        public String getKeyFromData(Tuple5<Long, Long, Long, String, Long> data) {
            return data.f0.toString();
        }
        //指定value
        public String getValueFromData(Tuple5<Long, Long, Long, String, Long> data) {
            return data.f1.toString()+data.f3;
        }
    }


}