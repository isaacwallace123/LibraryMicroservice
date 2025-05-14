package com.isaacwallace.transaction_service.Business;

import com.isaacwallace.transaction_service.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionResponseModel;

import java.util.List;

public interface TransactionService {
    public List<TransactionResponseModel> getAllTransactions();
    public TransactionResponseModel getTransactionById(String transactionid);
    public TransactionResponseModel addTransaction(TransactionRequestModel transactionRequestModel);
    public TransactionResponseModel updateTransaction(String transactionid, TransactionRequestModel transactionRequestModel);
    public void deleteTransaction(String transactionid);
}
