package io.j1st.power.storage.mongo.entity;

/**
 * User Role
 */
public enum UserRole {
    DEVELOPER(1),
    PLAYER(2);

    private final int value;

    UserRole(int value) {
        this.value = value;
    }

    public static UserRole valueOf(int value) {
        for (UserRole r : values()) {
            if (r.value == value) {
                return r;
            }
        }
        throw new IllegalArgumentException("invalid user role: " + value);
    }

    public int value() {
        return value;
    }
}
