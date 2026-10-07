package com.monetrax.monetrax.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseApiKeyStatusMsg {
    private String msg;
    private List<String> listOfPresentKeys;
}
