package com.erp_be.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MetadataDataService {
    Map<String, Object> create(String entityName, Map<String, Object> payload);
    List<Map<String, Object>> findAll(String entityName);
    Map<String, Object> update(String entityName, UUID id, Map<String, Object> payload);
    void delete(String entityName, UUID id);
}
