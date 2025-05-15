package com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models;

import com.isaacwallace.api_gateway.Services.Transaction.DataAccess.Payment;
import com.isaacwallace.api_gateway.Services.Transaction.DataAccess.Status;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestModel extends RepresentationModel<TransactionRequestModel> {
    String memberid;
    String bookid;
    String employeeid;

    LocalDateTime transactionDate;

    Status status;

    Payment payment;
}
