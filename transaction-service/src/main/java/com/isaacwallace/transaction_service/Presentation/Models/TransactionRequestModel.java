package com.isaacwallace.transaction_service.Presentation.Models;

import com.isaacwallace.transaction_service.DataAccess.Payment;
import com.isaacwallace.transaction_service.DataAccess.Status;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestModel {
    String memberid;
    String bookid;
    String employeeid;

    LocalDateTime transactionDate;

    Status status;

    Payment payment;
}
