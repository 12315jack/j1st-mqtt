package com.github.j1stiot.mqtt.api.comm;

import com.github.j1stiot.mqtt.api.internal.InternalMessage;
import org.apache.commons.configuration.AbstractConfiguration;

/**
 * Http Communicator
 * Link http server to other modules
 */
public interface HttpCommunicator {

    /**
     * Init the communicator
     *
     * @param config   Communicator Configuration
     * @param serverId Server Id
     */
    void init(AbstractConfiguration config, String serverId);

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
