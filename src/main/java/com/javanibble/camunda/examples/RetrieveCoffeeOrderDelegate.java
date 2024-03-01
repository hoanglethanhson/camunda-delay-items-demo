package com.javanibble.camunda.examples;

import com.javanibble.camunda.examples.util.StringUtils;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RetrieveCoffeeOrderDelegate implements JavaDelegate {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    RedisTemplate<String, Long> redisTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(RetrieveCoffeeOrderDelegate.class.getName());

    public void execute(DelegateExecution execution) throws Exception {
        String coffeeOrder = (String) execution.getVariable("order");
        String msisdn = (String) execution.getVariable("msisdn");
        String currentActivityId = execution.getCurrentActivityId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(StringUtils.getRedisKey(msisdn, currentActivityId)))) {
            LOGGER.info("Redis Key: " + StringUtils.getRedisKey(msisdn, currentActivityId) + " exists in retrieve-coffee-order-delegate");
            return;
        }

        LOGGER.info("Order Coffee Process: " + execution.getCurrentActivityName() + " - " + coffeeOrder);
        redisTemplate.opsForValue().set(StringUtils.getRedisKey(msisdn, currentActivityId), System.currentTimeMillis());
        LOGGER.info("Key " + StringUtils.getRedisKey(msisdn, currentActivityId) + " is set" + " at " + LocalDateTime.now());
        //end curren process instance
        runtimeService.deleteProcessInstance(execution.getProcessInstanceId(), "delay_item_caught");
    }

}
