package com.erp_be.model.erp_fe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "dynamic_data")
@Getter @Setter
public class DynamicData {
    @Id
    private UUID id;

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    // Hibernate 6 tự động map thuộc tính Map sang JSONB của PostgreSQL
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> data;
}