package com.github.j1stiot.mqtt.api.comm;

/**
 * Application Listener Factory
 */
@SuppressWarnings("unused")
public interface ApplicationListenerFactory {

    /**
     * Create a new ApplicationListener
     *
     * @return ApplicationListener
     */
    ApplicationListener newListener();
}
