package io.j1st.power.storage.mongo.entity;

/**
 * service type 服务类型
 */
public enum ServiceType {

    HARDWARE_MANAGER(0),
    ANALYSIS(1),
    AUTO_ENGINE(2),
    WEBHOOKER(3);

    private final int value;

    ServiceType(int value) {
        this.value = value;
    }

    public static ServiceType valueOf(int value) {
        for (ServiceType r : values()) {
            if (r.value == value) {
                return r;
            }
        }
        throw new IllegalArgumentException("invalid service type: " + value);
    }

    public int value() {
        return value;
    }

}
