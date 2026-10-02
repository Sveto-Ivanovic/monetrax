package com.monetrax.monetrax.ai.service;

import com.monetrax.monetrax.ai.dto.ResponseApiKeyStatusMsg;
import com.monetrax.monetrax.ai.entity.KeyType;

import java.util.UUID;

public interface AiAPiService {
    public ResponseApiKeyStatusMsg createUpdateApiKey(String rawKey, KeyType keyType, UUID userId);
    public ResponseApiKeyStatusMsg deleteApiKey(KeyType keyType, UUID userId);
    public ResponseApiKeyStatusMsg getKeyStatus(UUID userId);
}
