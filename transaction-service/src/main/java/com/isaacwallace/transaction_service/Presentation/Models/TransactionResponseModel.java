package com.isaacwallace.transaction_service.Presentation.Models;

import com.isaacwallace.transaction_service.DataAccess.Payment;
import com.isaacwallace.transaction_service.DataAccess.Status;
import com.isaacwallace.transaction_service.DomainClient.Employee.Models.EmployeeResponseModel;
import com.isaacwallace.transaction_service.DomainClient.Inventory.Models.BookResponseModel;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.MemberResponseModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponseModel {
    private String transactionid;

    private String memberid;
    private String bookid;
    private String employeeid;

    private LocalDateTime transactionDate;

    private Status status;
    private Payment payment;

    private BookResponseModel book;
    private MemberResponseModel member;
    private EmployeeResponseModel employee;
}
