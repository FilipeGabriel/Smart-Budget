package com.filipegabriel.smart_budget.resources.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardError {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

}
