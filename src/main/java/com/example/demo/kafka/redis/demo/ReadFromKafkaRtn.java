package com.example.demo.kafka.redis.demo;

import com.example.demo.kafka.MyFlatMapFunction;
import com.example.demo.kafka.RedisExampleMapper;
import com.ext.redis.RedisSink;
import com.ext.redis.config.FlinkJedisPoolConfig;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * http://dyingbleed.com/flink-6/
 *
 * ① 设置状态存储后端，支持内存、文件系统和 RocksDB；
 *
 * ② 启用检查点并每秒保存一次，单位：毫秒；
 *
 * ③ 设置保存点模式为恰好一次；
 *
 * ④ 取消时保留检查点。
 *
 * 保存点
 * 保存点通过 Flink 检查点机制保存了任务运行过程中状态的镜像。通常用于停止并恢复、分发和任务更新。
 *
 * 触发保存点：
 *
 * bin/flink savepoint <任务 ID> [<保存点目录>] -yid <YARN 应用 ID>
 * 取消时触发保存点：
 *
 * bin/flink cancel -s [<保存点目录>] <任务 ID> -yid <YARN 应用 ID>
 * 从保存点恢复运行：
 *
 * bin/flink run -s <保存点目录> -c <主类> -m <JM 地址> -p <并发数> app.jar
 * 从保留的检查点恢复与从保存点恢复是一样的。
 *
 * 如果不指定保存点目录，默认为 state.savepoints.dir 配置，可以通过编辑 conf/flink-conf.yml 文件修改：
 *
 * state.savepoints.dir: hdfs://RM/user/flink/savepoints
 * 检查点 VS 保存点
 * 检查点是自动完成的，保存点是手动完成的。
 *
 * 检查点是轻量级的，保存点是重量级的。
 *
 * 检查点支持 RocksDB，保存点仅支持文件系统。
 *
 * 参考
 * Apache Flink Documentation: Checkpointing  https://ci.apache.org/projects/flink/flink-docs-release-1.8/dev/stream/state/checkpointing.html
 * Apache Flink Documentation: Checkpoints  https://ci.apache.org/projects/flink/flink-docs-release-1.8/ops/state/checkpoints.html
 * Apache Flink Documentation: Savepoints   https://ci.apache.org/projects/flink/flink-docs-release-1.8/ops/state/savepoints.html
 *
 * todo 保存点, 待验证
 */
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

        // env.setStateBackend(new FsStateBackend("")); // ①//默认为 state.savepoints.dir 配置
        env.enableCheckpointing(1000); // ②
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE); // ③

        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION); // ④


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




