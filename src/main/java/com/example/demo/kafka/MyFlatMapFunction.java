package com.example.demo.kafka;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class MyFlatMapFunction implements FlatMapFunction<String, Tuple2<String, String>> {

    @Override
    public void flatMap(String s, Collector<Tuple2<String, String>> collector) throws Exception {

        if (StringUtils.isEmpty(s))
            return;
        System.out.println(s+"====");
        String[] split = s.split("\\W+");
        collector.collect(new Tuple2<>(split[0], split[1]));

    }
}