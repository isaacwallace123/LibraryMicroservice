package com.isaacwallace.transaction_service.Mapper;

import com.isaacwallace.transaction_service.DataAccess.Transaction;
import com.isaacwallace.transaction_service.DomainClient.Employee.EmployeeServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Inventory.InventoryServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Membership.MembershipServiceClient;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionResponseModel;

import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionResponseMapper {
    @Mapping(expression = "java(transaction.getTransactionIdentifier().getTransactionid())", target = "transactionid")

    @Mapping(expression = "java(inventoryServiceClient.getInventoryById(transaction.getBookid()))", target = "book")
    @Mapping(expression = "java(membershipServiceClient.getMemberById(transaction.getMemberid()))", target = "member")
    @Mapping(expression = "java(employeeServiceClient.getEmployeeById(transaction.getEmployeeid()))", target = "employee")

    TransactionResponseModel entityToResponseModel(
            Transaction transaction,
            @Context InventoryServiceClient inventoryServiceClient,
            @Context MembershipServiceClient membershipServiceClient,
            @Context EmployeeServiceClient employeeServiceClient
    );

    default List<TransactionResponseModel> entityToResponseModelList(Transaction transaction, @Context InventoryServiceClient inventoryServiceClient, @Context MembershipServiceClient membershipServiceClient,@Context EmployeeServiceClient employeeService) {
        List<TransactionResponseModel> transactions = new ArrayList<>();

        transactions.add(entityToResponseModel(transaction, inventoryServiceClient, membershipServiceClient, employeeService));

        return transactions;
    }

    default List<TransactionResponseModel> entitiesToResponseModelList(List<Transaction> transactions, @Context InventoryServiceClient inventoryServiceClient, @Context MembershipServiceClient membershipServiceClient, @Context EmployeeServiceClient employeeService) {
        return transactions.stream()
                .map(transaction -> entityToResponseModel(transaction, inventoryServiceClient, membershipServiceClient, employeeService))
                .toList();
    }

    @AfterMapping
    default void mapResponseFields(@MappingTarget TransactionResponseModel transactionResponseModel, Transaction transaction) {
        transactionResponseModel.setStatus(transaction.getStatus());
    }
}