package modelo;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Modelo para la tabla `wastes` de Supabase. equals/hashCode basados en id
 * (clave de negocio) y toString legible para depuración, siguiendo el mismo
 * patrón que RegistroRecoleccion.
 */
public class Waste {

    private long id;
    private String type;
    private double quantity;
    private String unitMeasurement;
    private LocalDate wasteGenerationDate;
    private boolean hasStorageLocation;
    private String state;
    private boolean status;               // true = activo, false = eliminado (soft delete)
    private OffsetDateTime createdAt;
    private Long createdBy;
    private OffsetDateTime updatedAt;
    private Long updatedBy;
    private Long entityId;
    private boolean dangerousness;
    private OffsetDateTime publishAt;
    private String entityName;
    private String typeName;
    private String unitMeasurementName;
    private String stateName;

    // ── Constructores ────────────────────────────────────────────────────────
    public Waste() {
    }

    public Waste(long id, String type, double quantity, String unitMeasurement, LocalDate wasteGenerationDate, boolean hasStorageLocation, String state, boolean status, OffsetDateTime createdAt, Long createdBy, OffsetDateTime updatedAt, Long updatedBy, Long entityId, boolean dangerousness, OffsetDateTime publishAt, String entityName, String typeName, String unitMeasurementName, String stateName) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
        this.unitMeasurement = unitMeasurement;
        this.wasteGenerationDate = wasteGenerationDate;
        this.hasStorageLocation = hasStorageLocation;
        this.state = state;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.entityId = entityId;
        this.dangerousness = dangerousness;
        this.publishAt = publishAt;
        this.entityName = entityName;
        this.typeName = typeName;
        this.unitMeasurementName = unitMeasurementName;
        this.stateName = stateName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnitMeasurement() {
        return unitMeasurement;
    }

    public void setUnitMeasurement(String unitMeasurement) {
        this.unitMeasurement = unitMeasurement;
    }

    public LocalDate getWasteGenerationDate() {
        return wasteGenerationDate;
    }

    public void setWasteGenerationDate(LocalDate wasteGenerationDate) {
        this.wasteGenerationDate = wasteGenerationDate;
    }

    public boolean isHasStorageLocation() {
        return hasStorageLocation;
    }

    public void setHasStorageLocation(boolean hasStorageLocation) {
        this.hasStorageLocation = hasStorageLocation;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public boolean isDangerousness() {
        return dangerousness;
    }

    public void setDangerousness(boolean dangerousness) {
        this.dangerousness = dangerousness;
    }

    public OffsetDateTime getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(OffsetDateTime publishAt) {
        this.publishAt = publishAt;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getUnitMeasurementName() {
        return unitMeasurementName;
    }

    public void setUnitMeasurementName(String unitMeasurementName) {
        this.unitMeasurementName = unitMeasurementName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
    
    

    // ── equals / hashCode (basados en clave de negocio: id) ──────────────────
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Waste)) {
            return false;
        }
        return id == ((Waste) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ── toString ─────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Waste{"
                + "id=" + id
                + ", type='" + type + '\''
                + ", quantity=" + quantity
                + ", unit='" + unitMeasurement + '\''
                + ", fecha=" + wasteGenerationDate
                + ", state='" + state + '\''
                + ", status=" + status
                + ", dangerousness=" + dangerousness
                + '}';
    }
}
