package io.j1st.power.storage.mongo.entity;

/**
 * Permission Level
 */
public enum PermissionLevel {
    OWNER(9),
    READ_WRITE(6),
    READ(3);

    private final int value;

    PermissionLevel(int value) {
        this.value = value;
    }

    public static PermissionLevel valueOf(int value) {
        for (PermissionLevel l : values()) {
            if (l.value == value) {
                return l;
            }
        }
        throw new IllegalArgumentException("invalid permission level " + value);
    }

    public int value() {
        return value;
    }
}
