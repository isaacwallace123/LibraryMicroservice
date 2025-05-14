package com.isaacwallace.transaction_service.DataAccess;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Document(collection = "transactions")
@Data
@NoArgsConstructor
public class Transaction {
    @Id
    private String id;

    private TransactionIdentifier transactionIdentifier;

    private String memberid;
    private String bookid;
    private String employeeid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionDate;

    private Status status;

    private Payment payment;
}