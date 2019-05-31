package com.example.demo.kafka;

import com.example.demo.redis.RedisResource;
import com.ext.redis.RedisSink;
import com.ext.redis.config.FlinkJedisPoolConfig;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

public class ReadFromKafka {

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
//
        // print() will write the contents of the stream to the TaskManager's standard out stream
        // the rebelance call is causing a repartitioning of the data so that all machines
        // see the messages (for example in cases when "num kafka partitions" < "num flink operators"


//        FlinkJedisPoolConfig redis = new FlinkJedisPoolConfig.Builder().setHost("192.168.191.130").build();
//        InetSocketAddress node1 = new InetSocketAddress("", 6379);
//        HashSet<InetSocketAddress> set = new HashSet<>();
//        set.add(node1);
//        FlinkJedisClusterConfig build = new FlinkJedisClusterConfig.Builder().setNodes(set).build();

        FlinkJedisPoolConfig build = new FlinkJedisPoolConfig.Builder().setHost("192.168.191.130").build();
//        DataStreamSource<Map<String, String>> redisSource = env.addSource(new RedisResource(redis));
//        redisSource.setParallelism(1);
//         redisSource.connect(messageStream)
//                .flatMap(new CoFlatMapFunction<Map<String,String>, String, Object>() {
//                    @Override
//                    public void flatMap1(Map<String, String> value, Collector<Object> out) throws Exception {
//                        System.out.println("ReadFromKafka.flatMap1");
//                        System.out.println(value+"==== f1");
//                    }
//
//                    @Override
//                    public void flatMap2(String value, Collector<Object> out) throws Exception {
//                        System.out.println("ReadFromKafka.flatMap2");
//                        System.out.println(value+"f2");
//
//                    }
//                });

//        connectData.print();
//
//

        messageStream.flatMap(new MyFlatMapFunction())
                .keyBy(0)
                .sum(1)
                .addSink(new RedisSink<>(build, new RedisExampleMapper()))
                .setParallelism(1);
        env.execute();
    }
}




