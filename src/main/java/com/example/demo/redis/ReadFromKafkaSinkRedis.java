package com.example.demo.redis;

import com.example.demo.kafka.MyFlatMapFunction;
import com.example.demo.kafka.RedisExampleMapper;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

public class ReadFromKafkaSinkRedis {

    public static void main(String[] args) throws Exception {
// create execution environment
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
        // parse user parameters
        ParameterTool parameterTool = ParameterTool.fromMap(properties);

        FlinkKafkaConsumer<String> consumer010 = new FlinkKafkaConsumer<>(
                parameterTool.getRequired("topic"), new SimpleStringSchema(), parameterTool.getProperties());


        DataStream<String> messageStream = env.addSource(consumer010);

        // print() will write the contents of the stream to the TaskManager's standard out stream
        // the rebelance call is causing a repartitioning of the data so that all machines
        // see the messages (for example in cases when "num kafka partitions" < "num flink operators"



        FlinkJedisPoolConfig redis = new FlinkJedisPoolConfig.Builder().setHost("192.168.191.130").build();
//        InetSocketAddress node1 = new InetSocketAddress("", 6379);
//        HashSet<InetSocketAddress> set = new HashSet<>();
//        set.add(node1);
//        FlinkJedisClusterConfig build = new FlinkJedisClusterConfig.Builder().setNodes(set).build();
        RedisSink<Tuple2<String, String>> listRedisSink = new RedisSink<>(redis, new RedisExampleMapper());
        messageStream
                .flatMap(new MyFlatMapFunction())

                .addSink(listRedisSink);


        env.execute();
    }
}




