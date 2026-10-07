package com.monetrax.monetrax.ai.service.impl;

import com.monetrax.monetrax.ai.dto.ResponseApiKeyStatusMsg;
import com.monetrax.monetrax.ai.entity.ApiKeysEntity;
import com.monetrax.monetrax.ai.entity.KeyType;
import com.monetrax.monetrax.ai.exceptions.EncryptDecryptException;
import com.monetrax.monetrax.ai.exceptions.NoSuchApiKeyExistsException;
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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
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

    public String localEncrypt(String plainText, UUID userId, KeyType keyType)  {

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

           String aad = userId + ":" + keyType.name();
           cipher.updateAAD(aad.getBytes(StandardCharsets.UTF_8));

           // Encrypt the plaintext
           byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

           // Combine IV and encrypted text and encode them as Base64
           byte[] combinedIvAndCipherText = new byte[iv.length + encryptedBytes.length];
           System.arraycopy(iv, 0, combinedIvAndCipherText, 0, iv.length);
           System.arraycopy(encryptedBytes, 0, combinedIvAndCipherText, iv.length, encryptedBytes.length);

           return Base64.getEncoder().encodeToString(combinedIvAndCipherText);
       }
       catch (Exception e){
           throw new EncryptDecryptException("Encryption failed: " + e.getMessage());
       }
    }


    public String localDecrypt(String cipherText, UUID userId, KeyType keyType) {
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

            // this part here additional authentication data prevents case where hacker copy + pastes the api key from one account to another enabling him to use it via our app
            String aad = userId + ":" + keyType.name();
            cipher.updateAAD(aad.getBytes(StandardCharsets.UTF_8));


            // Decrypt the ciphertext
            byte[] decryptedBytes = cipher.doFinal(encryptedText);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
       catch (Exception e){
        throw new EncryptDecryptException("Decryption failed" + e.getMessage());
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
        String substring = rawKey.substring(rawKey.length() - 4);

        if(apiKeysEntityOptional.isPresent()){

            log.info("The entity for the keyType={} exists.", keyType);

            ApiKeysEntity apiKeys = apiKeysEntityOptional.get();
            apiKeys.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            apiKeys.setApiKeyEncrypted(localEncrypt(rawKey, userId, keyType));
            apiKeys.setKeyLast4(substring);
            apiKeysRepository.save(apiKeys);

            log.info("Updated entity for the keyType={}.", keyType);

            List<String> listOfPresentKeys = apiKeysRepository.fetchPresentKeys(userId);
            return ResponseApiKeyStatusMsg.builder().msg("Successfully updated key type: %s".formatted(keyType)).listOfPresentKeys(listOfPresentKeys).build();
        }
        else{
            log.info("Entity for the keyType={} doesn't exists.", keyType);
            ApiKeysEntity   apiKeysEntityToCreate = ApiKeysEntity.builder()
                    .apiKeyEncrypted(localEncrypt(rawKey, userId, keyType))
                    .active(true)
                    .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                    .keyLast4(substring)
                    .keyType(keyType)
                    .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                    .lastUsedAt(null)
                    .user(user)
                    .build();

            apiKeysRepository.save(apiKeysEntityToCreate);

            log.info("Created entity for the keyType={}.", keyType);
            List<String> listOfPresentKeys = apiKeysRepository.fetchPresentKeys(userId);
            return ResponseApiKeyStatusMsg.builder().msg("Successfully created key type: %s".formatted(keyType)).listOfPresentKeys(listOfPresentKeys).build();

        }

    }

    @Override
    public ResponseApiKeyStatusMsg deleteApiKey(KeyType keyType, UUID userId) {
        log.info("Deleting api key for userId={}, keyType={}", userId, keyType);

        ApiKeysEntity apiKeysEntityOptional = apiKeysRepository.fetchApiKey(keyType, userId).orElseThrow(() ->
        {log.warn("No Api Key found for  userId={} and keyType={}", userId, keyType);
        return new NoSuchApiKeyExistsException("No Api Key found for  userId={} and keyType={} " + userId);});

        apiKeysRepository.delete(apiKeysEntityOptional);
        log.info("Successfully deleted api key for userId={}, keyType={}", userId, keyType);

        List<String> listOfPresentKeys = apiKeysRepository.fetchPresentKeys(userId);
        return ResponseApiKeyStatusMsg.builder().msg("Successfully deleted the key.").listOfPresentKeys(listOfPresentKeys).build();

    }

    @Override
    public ResponseApiKeyStatusMsg getKeyStatus(UUID userId) {
        log.info("Fetching status of api keys for userId={}", userId);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("No user found with id={}", userId);
            return new NoSuchUserExistsException("No user with id: " + userId);
        });

        List<String> listOfPresentKeys = apiKeysRepository.fetchPresentKeys(user.getUserId());
        return ResponseApiKeyStatusMsg.builder().msg("Fetched present list of key types present in DB.").listOfPresentKeys(listOfPresentKeys).build();

    }

}
