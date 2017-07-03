package com.github.j1stiot.mqtt.api.comm;

import com.github.j1stiot.mqtt.api.internal.InternalMessage;
import com.github.j1stiot.mqtt.api.internal.Disconnect;
import com.github.j1stiot.mqtt.api.internal.Publish;

/**
 * Broker's Listener for Broker Communicator
 */
@SuppressWarnings("unused")
public interface BrokerListener {

    /**
     * Handle internal publish message
     *
     * @param msg InternalMessage<Publish>
     */
    void onPublish(InternalMessage<Publish> msg);

    /**
     * Handle internal disconnect message
     *
     * @param msg InternalMessage<Disconnect>
     */
    void onDisconnect(InternalMessage<Disconnect> msg);
}
