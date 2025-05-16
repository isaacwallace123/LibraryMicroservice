package com.isaacwallace.transaction_service.DataAccess;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
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

    public Transaction(String memberid, String bookid, String employeeid, LocalDateTime now, Status status, Payment payment) {
        this.memberid = memberid;
        this.bookid = bookid;
        this.employeeid = employeeid;
        this.transactionDate = now;
        this.status = status;
        this.payment = payment;
    }
}