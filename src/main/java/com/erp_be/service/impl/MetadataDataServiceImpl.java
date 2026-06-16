package com.erp_be.service.impl;


import com.erp_be.model.erp_fe.DynamicData;
import com.erp_be.model.erp_fe.EntityDefinition;
import com.erp_be.model.erp_fe.FieldDefinition;
import com.erp_be.repository.erp_fe.DynamicDataRepository;
import com.erp_be.repository.erp_fe.EntityDefinitionRepository;
import com.erp_be.service.MetadataDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MetadataDataServiceImpl implements MetadataDataService {

    @Autowired
    private EntityDefinitionRepository entityRepository;

    @Autowired
    private DynamicDataRepository dataRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate; // Dùng để truy vấn kiểm tra trùng lặp JSONB siêu tốc

    @Override
    @Transactional
    public Map<String, Object> create(String entityName, Map<String, Object> payload) {
        // 1. Kiểm tra cấu trúc dựa trên tầng Entity Metadata
        validateSchema(entityName, payload);

        // 2. Thực thi các ràng buộc nghiệp vụ (Constraints của Customers & Invoices)
        executeBusinessConstraints(entityName, payload);

        // 3. Đóng gói dữ liệu vào Thực thể DynamicData
        UUID newId = UUID.randomUUID();
        payload.put("id", newId.toString());

        DynamicData dynamicData = new DynamicData();
        dynamicData.setId(newId);
        dynamicData.setEntityName(entityName);
        dynamicData.setData(payload);

        dataRepository.save(dynamicData);
        return payload;
    }

    @Override
    public List<Map<String, Object>> findAll(String entityName) {
        List<DynamicData> records = dataRepository.findByEntityName(entityName);
        List<Map<String, Object>> result = new ArrayList<>();
        for (DynamicData record : records) {
            Map<String, Object> dataMap = record.getData();
            dataMap.put("id", record.getId().toString());
            result.add(dataMap);
        }
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> update(String entityName, UUID id, Map<String, Object> payload) {
        if (!dataRepository.existsById(id)) {
            throw new IllegalArgumentException("Bản ghi không tồn tại.");
        }
        validateSchema(entityName, payload);

        payload.put("id", id.toString());
        DynamicData dynamicData = new DynamicData();
        dynamicData.setId(id);
        dynamicData.setEntityName(entityName);
        dynamicData.setData(payload);

        dataRepository.save(dynamicData);
        return payload;
    }

    @Override
    @Transactional
    public void delete(String entityName, UUID id) {
        dataRepository.deleteById(id);
    }

    // =========================================================================
    // HÀM KIỂM TRA SCHEMA ĐỘNG (DỰA TRÊN TẦNG ENTITY METADATA)
    // =========================================================================
    private void validateSchema(String entityName, Map<String, Object> payload) {
        EntityDefinition def = entityRepository.findByCode(entityName)
                .orElseThrow(() -> new IllegalArgumentException("Thực thể '" + entityName + "' chưa được cấu hình."));

        for (FieldDefinition field : def.getFields()) {
            boolean isFieldRequired = Boolean.TRUE.equals(field.getRequired());
            String nameOfField = field.getFieldName();

            if (isFieldRequired && (!payload.containsKey(nameOfField) || payload.get(nameOfField) == null)) {
                throw new IllegalArgumentException("Trường dữ liệu bắt buộc [" + nameOfField + "] đang bị bỏ trống.");
            }
        }
    }

    // =========================================================================
    // QUY TẮC PHẦN MỀM THAY THẾ CHO TRIGGER / CHECK CONSTRAINTS TRONG DB VẬT LÝ
    // =========================================================================
    private void executeBusinessConstraints(String entityName, Map<String, Object> payload) {

        // --- Xử lý cho bảng CUSTOMERS ---
        if ("customers".equals(entityName)) {
            String customerCode = (String) payload.get("customer_code");
            String sql = "SELECT COUNT(*) FROM dynamic_data WHERE entity_name = 'customers' AND data->>'customer_code' = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, customerCode);
            if (count != null && count > 0) {
                throw new IllegalArgumentException("Xung đột dữ liệu: Mã khách hàng '" + customerCode + "' đã tồn tại!");
            }
        }

        // --- Xử lý cho bảng AP_INVOICES ---
        else if ("ap_invoices".equals(entityName)) {
            double amount = getAsDouble(payload.get("amount"));
            double taxAmount = getAsDouble(payload.get("tax_amount"));
            double totalAmount = getAsDouble(payload.get("total_amount"));
            double paidAmount = getAsDouble(payload.get("paid_amount"));

            // Mô phỏng CONSTRAINT chk_inv_amount (Các số tiền >= 0)
            if (amount < 0 || taxAmount < 0 || paidAmount < 0) {
                throw new IllegalArgumentException("Lỗi hạch toán: Các giá trị số tiền hàng, tiền thuế không được âm.");
            }
            // Mô phỏng CONSTRAINT chk_inv_total (total_amount = amount + tax_amount)
            if (Math.abs(totalAmount - (amount + taxAmount)) > 0.01) {
                throw new IllegalArgumentException("Lỗi hạch toán: Tổng tiền (total_amount) bắt buộc phải bằng [Tiền hàng + Tiền thuế].");
            }

            // Tránh ClassCastException bằng cách chuyển đổi chuỗi an toàn
            String supplierId = payload.get("supplier_id") != null ? String.valueOf(payload.get("supplier_id")) : null;
            String invoiceNumber = payload.get("invoice_number") != null ? String.valueOf(payload.get("invoice_number")) : null;

            // Mô phỏng CONSTRAINT uq_invoice_supplier (Mỗi nhà cung cấp duy nhất 1 số hóa đơn)
            String sql = "SELECT COUNT(*) FROM dynamic_data WHERE entity_name = 'ap_invoices' " +
                    "AND data->>'supplier_id' = ? AND data->>'invoice_number' = ?";

            // Đã sửa: Bổ sung Integer.class vào đúng vị trí tham số của JdbcTemplate
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, supplierId, invoiceNumber);

            if (count != null && count > 0) {
                throw new IllegalArgumentException("Số hóa đơn '" + invoiceNumber + "' đã được khai báo cho nhà cung cấp này trước đó.");
            }

            // Tự động sinh trường dữ liệu tính toán (outstanding_amount) trước khi lưu trữ
            payload.put("outstanding_amount", totalAmount - paidAmount);
        }
    }

    private double getAsDouble(Object val) {
        if (val instanceof Number) return ((Number) val).doubleValue();
        return 0.0;
    }
}