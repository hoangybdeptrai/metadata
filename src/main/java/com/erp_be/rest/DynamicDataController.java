package com.erp_be.rest;

import com.erp_be.service.MetadataDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/data")
@RequiredArgsConstructor
public class DynamicDataController {

    private final MetadataDataService metadataEngineService;

    @PostMapping("/{entity}")
    public ResponseEntity<Map<String, Object>> create(
            @PathVariable String entity,
            @RequestBody Map<String, Object> payload) {

        return ResponseEntity.ok(metadataEngineService.create(entity, payload));
    }

    @GetMapping("/{entity}")
    public ResponseEntity<List<Map<String, Object>>> read(@PathVariable String entity) {
        return ResponseEntity.ok(metadataEngineService.findAll(entity));
    }

    @PutMapping("/{entity}/{id}")
    public ResponseEntity<String> update(
            @PathVariable String entity,
            @PathVariable UUID id,
            @RequestBody Map<String, Object> payload) {
       // metadataEngineService.updateData(entity, id, payload);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    @DeleteMapping("/{entity}/{id}")
    public ResponseEntity<String> delete(@PathVariable String entity, @PathVariable UUID id) {
     //   metadataEngineService.deleteData(entity, id);
        return ResponseEntity.ok("Xóa thành công!");
    }
}