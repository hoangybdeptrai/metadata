package com.erp_be.model.erp_fe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "entity_definition")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityDefinition {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "code", length = 100, unique = true)
    private String code;
    @Column(name = "name", length = 255)
    private String name;
    @Column(name = "table_name", length = 255)
    private String tableName;
    @Column(name = "schema_name", length = 100)
    private String schemaName;
    @Column(name = "active")
    private Boolean active;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "entity_code",
            referencedColumnName = "code"
    )
    private List<FieldDefinition> fields;
}