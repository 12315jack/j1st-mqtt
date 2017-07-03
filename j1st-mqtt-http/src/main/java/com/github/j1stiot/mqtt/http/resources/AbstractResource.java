package com.github.j1stiot.mqtt.http.resources;

import com.github.j1stiot.mqtt.http.util.Validator;
import com.github.j1stiot.mqtt.api.auth.Authenticator;
import com.github.j1stiot.mqtt.api.comm.HttpCommunicator;
import com.github.j1stiot.mqtt.api.metrics.MetricsService;
import com.github.j1stiot.mqtt.storage.redis.sync.RedisSyncStorage;

/**
 * Abstract Base Resource
 */
public abstract class AbstractResource {

    protected final String serverId;
    protected final Validator validator;
    protected final RedisSyncStorage redis;
    protected final HttpCommunicator communicator;
    protected final Authenticator authenticator;
    protected final MetricsService metrics;

    public AbstractResource(String serverId, Validator validator, RedisSyncStorage redis, HttpCommunicator communicator, Authenticator authenticator, MetricsService metrics) {
        this.serverId = serverId;
        this.validator = validator;
        this.redis = redis;
        this.communicator = communicator;
        this.authenticator = authenticator;
        this.metrics = metrics;
    }
}
