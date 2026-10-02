package com.monetrax.monetrax.transactions.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreateAIRequest {
    @NotNull
    @NotBlank(message = "Msg must be present.")
    @Size(min = 10, max = 4000, message = "Size of the msg must be between 10 and 4000 characters.")
    private String msg;

    @Past(message = "The creation date must be in the past.")
    private OffsetDateTime customCreationDate;

    @NotNull
    private GoogleGenAiChatModel.ChatModel geminiModel;

    @NotNull
    private List<@Valid RequestedCategoryInformation> categories;

    private TransactionRecurrenceRule transactionRecurrenceRule;
}
