package com.monetrax.monetrax.ai.dto;


import com.monetrax.monetrax.ai.entity.KeyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestApiKey {

    @NotBlank(message = "API key must not be blank")
    @Size(min = 4, message = "API key is too short")
    private String rawKey;

    @NotNull(message = "Key type must be provided")
    private KeyType keyType;


}
