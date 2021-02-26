package com.libertex.demo.dto;

import com.libertex.demo.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDto {
    private ErrorCode code;
    private String message;
}
