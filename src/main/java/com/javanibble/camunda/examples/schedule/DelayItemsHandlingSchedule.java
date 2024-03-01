package com.javanibble.camunda.examples.schedule;


import com.javanibble.camunda.examples.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Objects;

@Configuration
@Slf4j
public class DelayItemsHandlingSchedule {

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    @Value("${app.delay-items.delay}")
    private Long delayDuration;

    @Scheduled(fixedRate = 1000L)
    public void handleDelayItems() {
        //log.info("Handling delay items");
        //get all keys from redis by prefix
        Objects.requireNonNull(redisTemplate.keys(Constant.DELAY_ITEM_KEY_PREFIX + ":*")).forEach(key -> {
            String msisdn = key.split(":")[1];
            if (isItemReady(key)) {
                log.info("Current time: {}, {}", System.currentTimeMillis(), LocalDateTime.now());
                log.info("Item value {} is ready for msisdn: {}", redisTemplate.opsForValue().get(key), msisdn);
                executeRemainingItems(msisdn);
                redisTemplate.delete(key);
            }
        });
    }

    private boolean isItemReady(String key) {
        Long delayTimestamp = redisTemplate.opsForValue().get(key);
        if (delayTimestamp == null) {
            log.info("delayTimestamp is null for key: {}", key);
            return true;
        }
        return (delayTimestamp + delayDuration <= System.currentTimeMillis());
    }

    private void executeRemainingItems(String msisdn) {
        log.info("handling delay item for {}", msisdn);
        runtimeService.createProcessInstanceByKey("order-coffee-subprocess")
                .startBeforeActivity("make-coffee")
                .setVariable("order", "Latte")
                .setVariable("msisdn", msisdn)
                .execute();
    }
}
