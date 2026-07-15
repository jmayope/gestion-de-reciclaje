package modelo;

import java.time.OffsetDateTime;

public class Devices {
    private long id;
    private OffsetDateTime createdAt;
    private String macaddress;
    private String operativeSystem;
    private long userId;
    private long entityId;
    private String userName;
    private String entityName;
    private boolean status;

    public Devices() {
    }

    public Devices(long id, OffsetDateTime createdAt, String macaddress, String operativeSystem, long userId, long entityId, String userName, String entityName, boolean status) {
        this.id = id;
        this.createdAt = createdAt;
        this.macaddress = macaddress;
        this.operativeSystem = operativeSystem;
        this.userId = userId;
        this.entityId = entityId;
        this.userName = userName;
        this.entityName = entityName;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getOperativeSystem() {
        return operativeSystem;
    }

    public void setOperativeSystem(String operativeSystem) {
        this.operativeSystem = operativeSystem;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
    