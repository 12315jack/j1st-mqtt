package com.github.j1stiot.mqtt.http.entity;

/**
 * Subscription
 */
public class Subscription {

    private String topic;
    private int qos;

    public Subscription() {
    }

    public Subscription(String topic, int qos) {
        this.topic = topic;
        this.qos = qos;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }
}
