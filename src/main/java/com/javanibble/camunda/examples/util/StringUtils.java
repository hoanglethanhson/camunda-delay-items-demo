package com.javanibble.camunda.examples.util;

import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class StringUtils {
    public String getRedisKey(String msisdn, String activityKey) {
        return String.join(":", msisdn, activityKey);
    }
}
