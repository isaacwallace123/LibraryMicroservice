package com.isaacwallace.transaction_service.Presentation.Models;

import com.isaacwallace.transaction_service.DataAccess.Payment;
import com.isaacwallace.transaction_service.DataAccess.Status;
import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionRequestModel {
    String memberid;
    String bookid;
    String employeeid;

    LocalDateTime transactionDate;

    Status status;

    Payment payment;
}
