package com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models;

import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeResponseModel;
import com.isaacwallace.api_gateway.Services.Inventory.Presentation.Models.InventoryResponseModel;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipResponseModel;
import com.isaacwallace.api_gateway.Services.Transaction.DataAccess.Payment;
import com.isaacwallace.api_gateway.Services.Transaction.DataAccess.Status;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseModel extends RepresentationModel<TransactionResponseModel> {
    private String transactionid;

    private String memberid;
    private String bookid;
    private String employeeid;

    private LocalDateTime transactionDate;

    private Status status;
    private Payment payment;

    private InventoryResponseModel book;
    private MembershipResponseModel member;
    private EmployeeResponseModel employee;
}
