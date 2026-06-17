package com.erp_be.repository.erp_fe;

import com.erp_be.model.erp_fe.DynamicData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DynamicDataRepository extends JpaRepository<DynamicData, UUID> {

    /**
     * Lấy toàn bộ danh sách dữ liệu của một phân hệ cụ thể.
     * Ví dụ: truyền vào "ap_invoices" sẽ lấy ra toàn bộ hóa đơn mua vào từ bảng JSONB.
     */
    List<DynamicData> findByEntityName(String entityName);
}
