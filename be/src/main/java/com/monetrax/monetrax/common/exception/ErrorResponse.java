package com.monetrax.monetrax.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private int status;
    private List<Map<String,Object>> errors;

    public ErrorResponse(List<Map<String,Object>> listOfErrors)
    {
        super();
        this.errors = listOfErrors;
    }
}