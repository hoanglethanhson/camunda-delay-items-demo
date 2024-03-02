package com.javanibble.camunda.examples.util;

import com.javanibble.camunda.examples.constant.Constant;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    public String getRedisKey(String msisdn, String currentActivityId) {
        return String.join(":", Constant.DELAY_ITEM_KEY_PREFIX, msisdn, currentActivityId);
    }

    public String getMsisdn(String key) {
        return key.split(":")[1];
    }
}
