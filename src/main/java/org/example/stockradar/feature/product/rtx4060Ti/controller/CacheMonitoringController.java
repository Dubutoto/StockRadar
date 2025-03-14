package org.example.stockradar.feature.product.rtx4060Ti.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.product.dto.ProductResponseDto;
import org.example.stockradar.feature.product.rtx4060Ti.service.Rtx4060TiStockCacheService;
import org.example.stockradar.feature.product.rtx4060Ti.service.Rtx4060TiStockCacheTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/monitoring")
@RequiredArgsConstructor
public class CacheMonitoringController {
    private final Rtx4060TiStockCacheTestService rtx4060TiStockCacheService;

    @GetMapping("/cache-stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        // 실제 사용 중인 서비스의 통계 반환
        return ResponseEntity.ok(rtx4060TiStockCacheService.getCacheStats());
    }

    @GetMapping("/reset")
    public ResponseEntity<String> resetStats() {
        rtx4060TiStockCacheService.resetStats();
        return ResponseEntity.ok("통계가 초기화되었습니다.");
    }
}
