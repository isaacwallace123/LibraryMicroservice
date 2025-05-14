package com.isaacwallace.transaction_service.Mapper;

import com.isaacwallace.transaction_service.DataAccess.Transaction;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionResponseModel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionResponseMapper {
    @Mapping(expression = "java(transaction.getTransactionIdentifier().getTransactionid())", target = "transactionid")

    @Mapping(expression = "java(bookServiceClient.getBookByBookId(transaction.getBookid()))", target = "book")
    @Mapping(expression = "java(memberServiceClient.getMemberByMemberId(transaction.getMemberid()))", target = "member")
    @Mapping(expression = "java(employeeServiceClient.getEmployeeByEmployeeid(transaction.getEmployeeid()))", target = "employee")
    TransactionResponseModel entityToResponseModel(Transaction transaction);
    List<TransactionResponseModel> entityToResponseModelList(List<Transaction> transactions);
}