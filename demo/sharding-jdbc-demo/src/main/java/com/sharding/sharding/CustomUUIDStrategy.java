package com.sharding.sharding;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

public class CustomUUIDStrategy implements PreciseShardingAlgorithm<String> {
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        String uuid = preciseShardingValue.getValue();
        int mode = Math.abs(uuid.hashCode() % collection.size());
        String[] strings = collection.toArray(new String[collection.size()]);
        return strings[mode];
    }
}
