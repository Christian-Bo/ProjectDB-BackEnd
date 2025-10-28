package com.nexttechstore.nexttech_backend.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpCallResult {
    private Boolean ok;
    private String message;
    private Map<String, Object> data;
    private Integer total;
}