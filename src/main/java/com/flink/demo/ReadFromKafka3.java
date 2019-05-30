package com.flink.demo;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReadFromKafka3 {

    public static void main(String[] args) throws Exception {
// create execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Map<String, String> properties = new HashMap<>();

        properties.put("zookeeper.connect", "182.119.91.183:2181");
        properties.put("bootstrap.servers", "182.119.91.183:9092");
        properties.put("group.id", "group_test_stephan");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "300000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("topic", "app_log");
        // parse user parameters

        ParameterTool parameterTool = ParameterTool.fromMap(properties);

        String topic = parameterTool.getRequired("topic");
        Properties properties1 = parameterTool.getProperties();
        SimpleStringSchema simpleStringSchema = new SimpleStringSchema();
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(topic, simpleStringSchema, properties1);
        DataStream<String> messageStream = env.addSource(consumer);
        hello(env, messageStream);
    }

    static void hello(StreamExecutionEnvironment env, DataStream<String> messageStream) throws Exception {
        messageStream.rebalance().map(new MapFunction<String, String>() {
            //序列化设置
            private static final long serialVersionUID = 1L;

            @Override
            public String map(String value) throws Exception {
                return value;
            }
        });

        messageStream.print().setParallelism(1);

        env.execute();
    }
}