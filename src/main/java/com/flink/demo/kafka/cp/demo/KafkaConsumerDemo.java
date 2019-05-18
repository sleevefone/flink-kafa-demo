package com.flink.demo.kafka.cp.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class KafkaConsumerDemo {
    private final KafkaConsumer<String, String> consumer;
    private KafkaConsumerDemo(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "172.16.143.147:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<String, String>(props);
    }
    void consume() throws InterruptedException {
        consumer.subscribe(Arrays.asList(KafkaProducerDemo.TOPIC));
        while (true) {
            Thread.sleep(1000L);
            ConsumerRecords<String, String> records = consumer.poll(100);
            if (records.isEmpty())
                System.out.println("topic test2 is empty!");

            for (ConsumerRecord<String, String> record : records){
                System.out.println("I'm coming");
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        KafkaConsumerDemo kafkaConsumerDemo = new KafkaConsumerDemo();
        kafkaConsumerDemo.consume();
    }
}