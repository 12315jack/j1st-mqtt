package com.github.j1stiot.mqtt.api.comm;

import com.github.j1stiot.mqtt.api.internal.*;

/**
 * Application's Listener for Processor Communicator
 */
@SuppressWarnings("unused")
public interface ApplicationListener {

    /**
     * Handle internal connect message
     *
     * @param msg InternalMessage<Connect>
     */
    void onConnect(InternalMessage<Connect> msg);

    /**
     * Handle internal publish message
     *
     * @param msg InternalMessage<Publish>
     */
    void onPublish(InternalMessage<Publish> msg);

    /**
     * Handle internal subscribe message
     *
     * @param msg InternalMessage<Subscribe>
     */
    void onSubscribe(InternalMessage<Subscribe> msg);

    /**
     * Handle internal unsubscribe message
     *
     * @param msg InternalMessage<Unsubscribe>
     */
    void onUnsubscribe(InternalMessage<Unsubscribe> msg);

    /**
     * Handle internal disconnect message
     *
     * @param msg InternalMessage<Disconnect>
     */
    void onDisconnect(InternalMessage<Disconnect> msg);
}
