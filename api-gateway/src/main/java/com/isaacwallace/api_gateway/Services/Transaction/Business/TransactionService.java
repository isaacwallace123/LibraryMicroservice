package com.isaacwallace.api_gateway.Services.Transaction.Business;

import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionResponseModel;

import java.util.List;

public interface TransactionService {
    public List<TransactionResponseModel> getAllTransactions();
    public TransactionResponseModel getTransactionById(String transactionid);
    public TransactionResponseModel addTransaction(TransactionRequestModel transactionRequestModel);
    public TransactionResponseModel updateTransaction(String transactionid, TransactionRequestModel transactionRequestModel);
    public void deleteTransaction(String transactionid);
    public List<TransactionResponseModel> getTransactionsByMemberId(String memberid);
    public TransactionResponseModel getMemberTransactionByTransactionId(String memberid, String transactionid);
}
