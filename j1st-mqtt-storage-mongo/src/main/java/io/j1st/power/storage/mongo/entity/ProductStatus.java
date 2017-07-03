package io.j1st.power.storage.mongo.entity;

/**
 * Product status description
 */
public enum ProductStatus {
    SERVICE(1), //服务
    SUSPEND(2), //暂停
    ARREARS(3); //欠费

    private final int value;

    ProductStatus(int value) {
        this.value = value;
    }

    public static ProductStatus valueOf(int value) {
        for (ProductStatus r : values()) {
            if (r.value == value) {
                return r;
            }
        }
        throw new IllegalArgumentException("invalid product status: " + value);
    }

    public int value() {
        return value;
    }
}
