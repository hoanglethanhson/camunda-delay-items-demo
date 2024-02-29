package com.javanibble.camunda.examples.schedule;


import com.javanibble.camunda.examples.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

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
        log.info("Handling delay items");
        //get all keys from redis by prefix
        redisTemplate.keys(Constant.DELAY_ITEM_KEY_PREFIX + ":*").forEach(key -> {
            String msisdn = key.split(":")[1];
            if (isItemReady(key)) {
                log.info("Item is ready for msisdn: " + msisdn);
                executeRemainingItems(msisdn);
                redisTemplate.delete(key);
            }
        });
    }

    private boolean isItemReady(String key) {
        Long delayTimestamp = redisTemplate.opsForValue().get(key);
        if (delayTimestamp == null) {
            return true;
        }
        return (delayTimestamp + delayDuration >= System.currentTimeMillis());
    }

    private void executeRemainingItems(String msisdn) {
        runtimeService.createProcessInstanceByKey("app.delay-items.delay")
                .startBeforeActivity("make-coffee")
                .setVariable("order", "Latte")
                .setVariable("msisdn", msisdn)
                .execute();
    }
}
