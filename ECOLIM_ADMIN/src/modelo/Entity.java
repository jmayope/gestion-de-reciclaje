package modelo;

import java.time.OffsetDateTime;

public class Entity {

    private long id;
    private String code;
    private String name;
    private String address;
    private String phone;
    private String type;
    private String typeName;
    private boolean status;
    private OffsetDateTime createdAt;
    private Integer createdBy;
    private OffsetDateTime updatedAt;
    private Integer updatedBy;

    public Entity() {
    }

    public Entity(long id, String code, String name,
                  String address, String phone,
                  String type, String typeName, boolean status,
                  OffsetDateTime createdAt,
                  Integer createdBy,
                  OffsetDateTime updatedAt,
                  Integer updatedBy) {

        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.type = type;
        this.typeName = typeName;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return getName();
    }
}
