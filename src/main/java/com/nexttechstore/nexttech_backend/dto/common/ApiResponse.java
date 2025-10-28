package com.nexttechstore.nexttech_backend.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private Boolean ok;
    private String message;
    private T data;
    private Integer total;

    // Métodos estáticos de conveniencia
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .ok(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data, Integer total) {
        return ApiResponse.<T>builder()
                .ok(true)
                .message(message)
                .data(data)
                .total(total)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .ok(false)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .ok(false)
                .message(message)
                .data(data)
                .build();
    }
}
