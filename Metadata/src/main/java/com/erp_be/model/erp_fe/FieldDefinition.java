package com.erp_be.model.erp_fe;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "field_definition")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "entity_code", length = 100)
    private String entityCode;
    @Column(name = "field_name", length = 100)
    private String fieldName;
    @Column(name = "field_label", length = 255)
    private String fieldLabel;
    @Column(name = "data_type", length = 50)
    private String dataType;
    @Column(name = "required")
    private Boolean required;
    @Column(name = "max_length")
    private Integer maxLength;
    @Column(name = "default_value", columnDefinition = "TEXT")
    private String defaultValue;
    @Column(name = "visible")
    private Boolean visible;
    @Column(name = "searchable")
    private Boolean searchable;
}