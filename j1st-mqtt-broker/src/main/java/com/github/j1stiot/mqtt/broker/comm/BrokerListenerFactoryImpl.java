package com.github.j1stiot.mqtt.broker.comm;

import com.github.j1stiot.mqtt.api.comm.BrokerListener;
import com.github.j1stiot.mqtt.api.comm.BrokerListenerFactory;
import com.github.j1stiot.mqtt.broker.session.SessionRegistry;

/**
 * Broker Listener Factory Implementation
 */
public class BrokerListenerFactoryImpl implements BrokerListenerFactory {

    private final SessionRegistry registry;

    public BrokerListenerFactoryImpl(SessionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public BrokerListener newListener() {
        return new BrokerListenerImpl(this.registry);
    }
}
