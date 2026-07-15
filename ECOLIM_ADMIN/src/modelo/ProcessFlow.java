package modelo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class ProcessFlow {

    private long id;

    private Long wasteId;

    private String previousProcessId;
    private String currentProcessId;

    private BigDecimal quantity;

    private BigDecimal longitude;
    private BigDecimal latitude;

    private boolean completed;
    private boolean status;

    private Long entityGeneratorId;
    private Long entityOperatorId;

    private OffsetDateTime createdAt;
    private Long createdBy;
    private OffsetDateTime updatedAt;
    private Long updatedBy;

    public ProcessFlow() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getWasteId() {
        return wasteId;
    }

    public void setWasteId(Long wasteId) {
        this.wasteId = wasteId;
    }

    public String getPreviousProcessId() {
        return previousProcessId;
    }

    public void setPreviousProcessId(String previousProcessId) {
        this.previousProcessId = previousProcessId;
    }

    public String getCurrentProcessId() {
        return currentProcessId;
    }

    public void setCurrentProcessId(String currentProcessId) {
        this.currentProcessId = currentProcessId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Long getEntityGeneratorId() {
        return entityGeneratorId;
    }

    public void setEntityGeneratorId(Long entityGeneratorId) {
        this.entityGeneratorId = entityGeneratorId;
    }

    public Long getEntityOperatorId() {
        return entityOperatorId;
    }

    public void setEntityOperatorId(Long entityOperatorId) {
        this.entityOperatorId = entityOperatorId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}