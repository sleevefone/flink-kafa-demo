//package com.flink.demo.kafka.cp.demo;
//
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.serialization.SimpleStringSchema;
//import org.apache.flink.api.java.tuple.Tuple5;
//import org.apache.flink.streaming.api.TimeCharacteristic;
//import org.apache.flink.streaming.api.datastream.DataStreamSource;
//import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
//import org.apache.flink.streaming.connectors.redis.RedisSink;
//import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
//import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
//import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
//import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
//
//import java.util.Properties;
//
//public class Data2Redis {
//    public static void main(String[] args) throws Exception {
//
//        System.out.println("===============》 flink任务开始  ==============》");
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        //设置kafka连接参数
//        Properties properties = new Properties();
//        properties.put("bootstrap.servers", "172.16.143.147:9092");
//        properties.put("group.id", "group_test_stephan");
//        properties.put("enable.auto.commit", "true");
//        properties.put("auto.commit.interval.ms", "1000");
//        properties.put("auto.offset.reset", "earliest");
//        properties.put("session.timeout.ms", "300000");
//        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        properties.put("topic", "test2");
//        //设置时间类型
//        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
//        //设置检查点时间间隔
//        env.enableCheckpointing(5000);
//        //设置检查点模式        //env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//        System.out.println("===============》 开始读取kafka中的数据  ==============》");
//        //创建kafak消费者，获取kafak中的数据
//        FlinkKafkaConsumer<String> kafkaConsumer010 = new FlinkKafkaConsumer<>("test2", new SimpleStringSchema(), properties);
//        kafkaConsumer010.setStartFromEarliest();
//        DataStreamSource<String> kafkaData = env.addSource(kafkaConsumer010);
//        kafkaData.print();
//        //解析kafka数据流 转化成固定格式数据流
//        SingleOutputStreamOperator<String> userData = kafkaData.map((MapFunction<String, String >) s -> s);
//        //实例化Flink和Redis关联类FlinkJedisPoolConfig，设置Redis端口
//        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("172.16.143.147").build();
//        //实例化RedisSink，并通过flink的addSink的方式将flink计算的结果插入到redis
//        userData.addSink(new RedisSink<>(conf, new RedisExampleMapper()));
//        System.out.println("===============》 flink任务结束  ==============》");
//        //设置程序名称
//        env.execute("data_to_redis_wangzh");
//    }
//
//    //指定Redis key并将flink数据类型映射到Redis数据类型
//    public static final class RedisExampleMapper implements RedisMapper<String> {
//        //设置数据使用的数据结构 HashSet 并设置key的名称
//        public RedisCommandDescription getCommandDescription() {
//            return new RedisCommandDescription(RedisCommand., "flink");
//        }
//
//        @Override
//        public String getKeyFromData(String s) {
//            return s;
//        }
//
//        @Override
//        public String getValueFromData(String s) {
//            return s;
//        }
//
//
//    }
//}