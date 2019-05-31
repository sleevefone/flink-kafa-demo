//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.demo.redis;

import com.ext.redis.RedisSink;
import com.ext.redis.config.FlinkJedisConfigBase;
import com.ext.redis.container.RedisCommandsContainer;
import com.ext.redis.container.RedisCommandsContainerBuilder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * todo 泛型{@code OUT} 取出redis?
 */
public class RedisResource extends RichParallelSourceFunction<Map<String, String>> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RedisSink.class);

    /**
     * This additional key needed for {@link --RedisDataType#HASH} and {@link --RedisDataType#SORTED_SET}.
     * Other {@link --RedisDataType} works only with two variable i.e. name of the list and value to be added.
     * But for {@link -RedisDataType#HASH} and {@link --RedisDataType#SORTED_SET} we need three variables.
     * <p>For {@link -RedisDataType#HASH} we need hash name, hash key and element.
     * {@code additionalKey} used as hash name for {@link -RedisDataType#HASH}
     * <p>For {@link -RedisDataType#SORTED_SET} we need set name, the element and it's score.
     * {@code additionalKey} used as set name for {@link -RedisDataType#SORTED_SET}
     */

    private FlinkJedisConfigBase flinkJedisConfigBase;
    private RedisCommandsContainer redisCommandsContainer;

    /**
     * Creates a new {@link RedisSink} that connects to the Redis server.
     *
     * @param flinkJedisConfigBase The configuration of {@link FlinkJedisConfigBase}
     */
    public RedisResource(FlinkJedisConfigBase flinkJedisConfigBase) {
        Preconditions.checkNotNull(flinkJedisConfigBase, "Redis connection pool config should not be null");

        this.flinkJedisConfigBase = flinkJedisConfigBase;
    }

    /**
     * Called when new data arrives to the sink, and forwards it to Redis channel.
     * Depending on the specified Redis data type (see {@link -RedisDataType}),
     * a different Redis command will be applied.
     * Available commands are RPUSH, LPUSH, SADD, PUBLISH, SET, PFADD, HSET, ZADD.
     *
     * @param input The incoming data
     */


    /**
     * Initializes the connection to Redis by either cluster or sentinels or single server.
     *
     * @throws IllegalArgumentException if jedisPoolConfig, jedisClusterConfig and jedisSentinelConfig are all null
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        this.redisCommandsContainer = RedisCommandsContainerBuilder.build(this.flinkJedisConfigBase);
    }

    /**
     * Closes commands container.
     *
     * @throws IOException if command container is unable to close.
     */
    @Override
    public void close() throws IOException {
        if (redisCommandsContainer != null) {
            redisCommandsContainer.close();
        }
    }

    @Override
    public void run(SourceContext<Map<String, String>> ctx) throws Exception {
        Map<String, String> out = this.redisCommandsContainer.hgetAll("mass_condition");//for redis resource
        //放入流里面
        ctx.collect(out);
    }

    @Override
    public void cancel() {

        try {
            close();
        } catch (Exception e) {
            LOG.error("failed to close redis", e);
        }
    }


}
