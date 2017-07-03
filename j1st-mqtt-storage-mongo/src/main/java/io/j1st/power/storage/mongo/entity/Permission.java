package io.j1st.power.storage.mongo.entity;

import org.bson.types.ObjectId;

/**
 * Permission
 */
public class Permission {

    private ObjectId userId;
    private PermissionLevel level;

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public PermissionLevel getLevel() {
        return level;
    }

    public void setLevel(PermissionLevel level) {
        this.level = level;
    }
}
