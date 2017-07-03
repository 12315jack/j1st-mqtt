package com.github.j1stiot.mqtt.api.comm;

import com.github.j1stiot.mqtt.api.internal.InternalMessage;
import org.apache.commons.configuration.AbstractConfiguration;

/**
 * Broker Communicator
 * Link broker to other modules
 */
@SuppressWarnings("unused")
public interface BrokerCommunicator {

    /**
     * Init the communicator
     *
     * @param config   Communicator Configuration
     * @param brokerId Broker Id
     * @param factory  Broker Listener Factory
     */
    void init(AbstractConfiguration config, String brokerId, BrokerListenerFactory factory);

    /**
     * Destroy the communicator
     */
    void destroy();

    /**
     * Send internal message to broker
     *
     * @param brokerId Broker Id
     * @param message  Internal Message
     */
    void sendToBroker(String brokerId, InternalMessage message);

    /**
     * Send internal message to outside
     * 3rd party application can handle the message from there
     *
     * @param message Internal Message
     */
    void sendToApplication(InternalMessage message);
}
