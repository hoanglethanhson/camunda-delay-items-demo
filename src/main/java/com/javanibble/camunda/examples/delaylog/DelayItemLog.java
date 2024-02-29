package com.javanibble.camunda.examples.delaylog;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "delay_item_log")
@Getter
@Setter
@IdClass(DelayItemPrimaryKey.class)
public class DelayItemLog {
    @Id
    @Column(name = "msisdn")
    private String msisdn;
    @Id
    @Column(name = "activity_key")
    private String activityKey;
}
