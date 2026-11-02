package com.monetrax.monetrax.user.dto;

import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class UserInformationDetailed {
    private UserInformation userInfo;
    private List<String> aiApiKeys;
}
