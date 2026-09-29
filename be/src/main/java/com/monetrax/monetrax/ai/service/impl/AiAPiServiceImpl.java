package com.monetrax.monetrax.ai.service.impl;

import com.monetrax.monetrax.ai.dto.ResponseApiKeyStatusMsg;
import com.monetrax.monetrax.ai.entity.ApiKeysEntity;
import com.monetrax.monetrax.ai.entity.KeyType;
import com.monetrax.monetrax.ai.repository.ApiKeysRepository;
import com.monetrax.monetrax.ai.service.AiAPiService;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AiAPiServiceImpl implements AiAPiService {

    private final ApiKeysRepository apiKeysRepository;
    private final UserRepository userRepository;

    public AiAPiServiceImpl(ApiKeysRepository apiKeysRepository, UserRepository userRepository) {
        this.apiKeysRepository = apiKeysRepository;
        this.userRepository = userRepository;
    }

    @Value("${aes-encryption.sha-crypt:${SHA_CRYPT:SHA-256}}")
    private String SHA_CRYPT;

    @Value("${aes-encryption.aes-algorithm:${AES_ALGORITHM:AES}}")
    private String AES_ALGORITHM;

    @Value("${aes-encryption.aes-algorithm-gcm:${AES_ALGORITHM_GCM:AES/GCM/NoPadding}}")
    private String AES_ALGORITHM_GCM;

    @Value("${aes-encryption.iv-length-encrypt:${IV_LENGTH_ENCRYPT:12}}")
    private Integer IV_LENGTH_ENCRYPT;

    @Value("${aes-encryption.tag-length-encrypt:${TAG_LENGTH_ENCRYPT:16}}")
    private Integer TAG_LENGTH_ENCRYPT;

    @Value("${aes-encryption.local-passphrase}")
    private String LOCAL_PASSPHRASE;

    private SecretKeySpec generateAesKeyFromPassphrase() throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance(SHA_CRYPT);
        // generates 32 byte hash based on password which can be any kind
        byte[] keyBytes = sha256.digest(LOCAL_PASSPHRASE.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    public String localEncrypt(String plainText)  {

       try {
           // Generate a random IV
           byte[] iv = new byte[IV_LENGTH_ENCRYPT];
           SecureRandom secureRandom = new SecureRandom();
           secureRandom.nextBytes(iv);

           // Generate the AES key from the local passphrase
           SecretKeySpec aesKey = generateAesKeyFromPassphrase();

           // Initialize cipher in AES-GCM mode
           Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
           GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
           cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);

           // Encrypt the plaintext
           byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

           // Combine IV and encrypted text and encode them as Base64
           byte[] combinedIvAndCipherText = new byte[iv.length + encryptedBytes.length];
           System.arraycopy(iv, 0, combinedIvAndCipherText, 0, iv.length);
           System.arraycopy(encryptedBytes, 0, combinedIvAndCipherText, iv.length, encryptedBytes.length);

           return Base64.getEncoder().encodeToString(combinedIvAndCipherText);
       }
       catch (Exception e){
           throw new RuntimeException("Encryption failed", e);
       }
    }


    public String localDecrypt(String cipherText) {
        try {
            byte[] decodedCipherText = Base64.getDecoder().decode(cipherText);

            // Generate the AES key from the local passphrase
            SecretKeySpec aesKey = generateAesKeyFromPassphrase();

            // Extract IV and encrypted text
            byte[] iv = new byte[IV_LENGTH_ENCRYPT];
            System.arraycopy(decodedCipherText, 0, iv, 0, iv.length);
            byte[] encryptedText = new byte[decodedCipherText.length - IV_LENGTH_ENCRYPT];
            System.arraycopy(decodedCipherText, IV_LENGTH_ENCRYPT, encryptedText, 0, encryptedText.length);

            // Initialize cipher in AES-GCM mode
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

            // Decrypt the ciphertext
            byte[] decryptedBytes = cipher.doFinal(encryptedText);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
       catch (Exception e){
        throw new RuntimeException("Encryption failed", e);
    }
    }


    @Override
    public ResponseApiKeyStatusMsg createUpdateApiKey(String rawKey, KeyType keyType, UUID userId) {
        log.info("Creating/Updating api key for userId={}, keyType={}", userId, keyType);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("No user found with id={}", userId);
            return new NoSuchUserExistsException("No user with id: " + userId);
        });

        Optional<ApiKeysEntity> apiKeysEntityOptional = apiKeysRepository.fetchApiKey(keyType, userId);

        if(apiKeysEntityOptional.isPresent()){
            ApiKeysEntity apiKeys = apiKeysEntityOptional.get();
            apiKeys.setUpdatedAt(OffsetDateTime.now());
            apiKeys.setApiKeyEncrypted(localEncrypt(rawKey));
            apiKeys.setKeyLast4(rawKey.substring(rawKey.length()-4));
            apiKeysRepository.save(apiKeys);

            return null;
        }

        return null;
    }

    @Override
    public ResponseApiKeyStatusMsg deleteApiKey(KeyType keyType, UUID userId) {
        return null;
    }

    @Override
    public ResponseApiKeyStatusMsg getKeyStatus(UUID userId) {
        return null;
    }

}
