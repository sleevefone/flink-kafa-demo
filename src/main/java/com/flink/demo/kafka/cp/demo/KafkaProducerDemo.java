package com.flink.demo.kafka.cp.demo;


import org.apache.kafka.clients.producer.*;

import java.util.Properties;


/**
 *
 * 1：启动zookeeper
 *
 * ./bin/zookeeper-server-start.sh ./config/zookeeper.properties
 * 2：启动kafka
 * ./bin/kafka-server-start.sh config/server.properties
 * 3： 查看kafka--zookeeper连接情况
 * <p>
 * [root@nodevan2 kafka]# lsof -i:2181
 * COMMAND   PID USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
 * java    16067 root  100u  IPv6 244256      0t0  TCP *:eforward (LISTEN)
 * java    16067 root  101u  IPv6 244851      0t0  TCP localhost:eforward->localhost:44654 (ESTABLISHED)
 * java    16378 root  100u  IPv6 244405      0t0  TCP localhost:44654->localhost:eforward (ESTABLISHED)
 * [root@nodevan2 kafka]# lsof -i:9092
 * COMMAND   PID USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
 * java    16378 root  105u  IPv6 244407      0t0  TCP *:XmlIpcRegSvc (LISTEN)
 * java    16378 root  121u  IPv6 244852      0t0  TCP 172.16.143.147:54630->172.16.143.147:XmlIpcRegSvc (ESTABLISHED)
 * java    16378 root  122u  IPv6 244413      0t0  TCP 172.16.143.147:XmlIpcRegSvc->172.16.143.147:54630 (ESTABLISHED)
 *
 * 4：创建topic
 * ./bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic test2 --partitions 3 --replication-factor 1
 * <p>
 * <p>
 * ./bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic test
 * <p>
 * 5：启动生产者消费者
 * ==================
 * 需要手动创建topic
 * ==================
 *
 * 问题一：生产者消费者为何都是直连kafka
 *

 */
public class KafkaProducerDemo {
    private final Producer<String, String> kafkaProdcer;
    public final static String TOPIC = "test2";

    private KafkaProducerDemo() {
        kafkaProdcer = createKafkaProducer();
    }

    private Producer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "172.16.143.147:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);
        return kafkaProducer;
    }

    void produce() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final String key = "key" + i;
            String data = "hello kafka message:" + key;
            kafkaProdcer.send(new ProducerRecord<>(TOPIC, key, data), new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println("发送key" + key + "成功");
                }
            });
        }
    }

    public static void main(String[] args) {
        KafkaProducerDemo kafkaProducerDemo = new KafkaProducerDemo();
        kafkaProducerDemo.produce();
    }

}