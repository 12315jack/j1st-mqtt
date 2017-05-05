package io.j1st.power.storage.mongo.entity;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Agent
 */
public class Agent {

    private ObjectId id;
    private ObjectId productId;
    private String name;
    private String token;
    private boolean connected;
    private long msgCount;
    private long msgSizeSum;
    private Map<String, Object> attributes;
    private List<Permission> permissions;
    private Date activatedAt;
    private Date updatedAt;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getProductId() {
        return productId;
    }

    public void setProductId(ObjectId productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public long getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(long msgCount) {
        this.msgCount = msgCount;
    }

    public long getMsgSizeSum() {
        return msgSizeSum;
    }

    public void setMsgSizeSum(long msgSizeSum) {
        this.msgSizeSum = msgSizeSum;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public Date getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Date activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
