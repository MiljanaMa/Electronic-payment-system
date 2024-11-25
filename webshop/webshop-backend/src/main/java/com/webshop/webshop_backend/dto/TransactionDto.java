package com.webshop.webshop_backend.dto;

import com.webshop.webshop_backend.mapper.DtoEntity;
import com.webshop.webshop_backend.model.enums.TransactionStatus;
import com.webshop.webshop_backend.model.enums.TransactionType;
import jakarta.validation.constraints.NotEmpty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto implements DtoEntity {
    private String id;
    @NotEmpty(message = "Amount is required")
    private double amount;
    @NotEmpty(message = "Timestamp is required")
    private Date timestamp;
    @NotEmpty(message = "Timestamp is required")
    private TransactionStatus status;
    @NotEmpty(message = "Timestamp is required")
    private TransactionType type;
    @NotEmpty(message = "UserId is required")
    private String userId;
    @NotEmpty(message = "Purchase id is required")
    private  String purchaseId;
}
