package com.flink.demo;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.HashMap;
import java.util.Map;

public class WriteIntoKafka {
    public static void main(String[] args) throws Exception {
        // create execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Map<String,String> properties = new HashMap<>();
        properties.put("bootstrap.servers", "172.16.143.147:9092");
        properties.put("topic", "test2");

        // parse user parameters
        ParameterTool parameterTool = ParameterTool.fromMap(properties);

        // add a simple source which is writing some strings
        DataStream<String> messageStream = env.addSource(new SimpleStringGenerator());

        // write stream to Kafka
        messageStream.addSink(new FlinkKafkaProducer<>(parameterTool.getRequired("bootstrap.servers"),
                parameterTool.getRequired("topic"),
                new SimpleStringSchema()));

        messageStream.rebalance().map(new MapFunction<String, String>() {
            //序列化设置
            private static final long serialVersionUID = 1L;

            @Override
            public String map(String value) throws Exception {
                return value;
            }
        });

        messageStream.print();

        env.execute();
    }

    public static class SimpleStringGenerator implements SourceFunction<String> {
        //序列化设置
        private static final long serialVersionUID = 1L;
        boolean running = true;

        @Override
        public void run(SourceContext<String> ctx) throws Exception {

            int write = 1;
            while (running) {
                Thread.sleep(3000);
                String hello = "hello write=" + write++;
                ctx.collect(hello);
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }
}