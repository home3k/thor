/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 *  All rights reserved.
 */

package com.haoyayi.thor.conf;

/**
 * @author home3k
 */
public class BizContext {

    public static int DPL_SHARDING_THRESHOLD = 1000;

    public static int BIZ_SHARDING_THRESHOLD = 1000;

    public static int MAX_RESPONSE_THRESHOLD = 5000;

    public int getDplShardingThreshold() {
        return DPL_SHARDING_THRESHOLD;
    }

    public void setDplShardingThreshold(int dplShardingThreshold) {
        DPL_SHARDING_THRESHOLD = dplShardingThreshold;
    }

    public int getBizShardingThreshold() {
        return BIZ_SHARDING_THRESHOLD;
    }

    public void setBizShardingThreshold(int bizShardingThreshold) {
        BIZ_SHARDING_THRESHOLD = bizShardingThreshold;
    }

    public int getMaxResponseThreshold() {
        return MAX_RESPONSE_THRESHOLD;
    }

    public void setMaxResponseThreshold(int maxResponseThreshold) {
        MAX_RESPONSE_THRESHOLD = maxResponseThreshold;
    }
}
