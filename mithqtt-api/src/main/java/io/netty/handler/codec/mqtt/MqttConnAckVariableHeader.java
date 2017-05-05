/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

/**
 * Variable header of {@link MqttConnectMessage}
 */
public class MqttConnAckVariableHeader {

    protected MqttConnectReturnCode returnCode;
    protected boolean sessionPresent;

    public MqttConnAckVariableHeader(MqttConnectReturnCode returnCode, boolean sessionPresent) {
        this.returnCode = returnCode;
        this.sessionPresent = sessionPresent;
    }

    public MqttConnectReturnCode returnCode() {
        return returnCode;
    }

    public boolean sessionPresent() {
        return sessionPresent;
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this)
                + '['
                + "returnCode=" + returnCode
                + ", sessionPresent=" + sessionPresent
                + ']';
    }
}
