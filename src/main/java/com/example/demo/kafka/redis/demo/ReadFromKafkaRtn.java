package com.example.demo.kafka.redis.demo;

import com.example.demo.kafka.MyFlatMapFunction;
import com.example.demo.kafka.RedisExampleMapper;
import com.ext.redis.RedisSink;
import com.ext.redis.config.FlinkJedisPoolConfig;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.HashMap;
import java.util.Map;

public class ReadFromKafkaRtn {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Map<String, String> properties = new HashMap<>();
        properties.put("bootstrap.servers", "192.168.191.130:9092");
        properties.put("group.id", "test");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("topic", "test2");

        ParameterTool parameterTool = ParameterTool.fromMap(properties);

        FlinkKafkaConsumer<String> consumer010 = new FlinkKafkaConsumer<>(
                parameterTool.getRequired("topic"), new SimpleStringSchema(), parameterTool.getProperties());


        DataStream<String> messageStream = env.addSource(consumer010);
        FlinkJedisPoolConfig build = new FlinkJedisPoolConfig.Builder().setHost("192.168.191.130").build();

        messageStream.flatMap(new MyFlatMapFunction())
                .keyBy(0)
                .sum(1)
                .addSink(new RedisSink<>(build, new RedisExampleMapper()))
                .setParallelism(4);
        env.execute();
    }
}




