package com.erp_be.repository.erp_fe;


import com.erp_be.model.erp_fe.EntityDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntityDefinitionRepository extends JpaRepository<EntityDefinition, UUID> {

    /**
     * Tìm kiếm cấu hình thực thể bằng mã code (Ví dụ: "ap_invoices", "customers")
     * Dùng ở tầng Service để load động các thuộc tính validation.
     */
    Optional<EntityDefinition> findByCode(String code);
}