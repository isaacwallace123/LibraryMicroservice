package com.isaacwallace.transaction_service.Business;

import com.isaacwallace.transaction_service.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionResponseModel;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface TransactionService {
    public List<TransactionResponseModel> getAllTransactions();
    public TransactionResponseModel getTransactionById(String transactionid);
    public TransactionResponseModel addTransaction(TransactionRequestModel transactionRequestModel);
    public TransactionResponseModel updateTransaction(String transactionid, TransactionRequestModel transactionRequestModel);
    public void deleteTransaction(String transactionid);

    public List<TransactionResponseModel> getTransactionsByMemberId(String memberid);
    public TransactionResponseModel getMemberTransactionByTransactionId(String memberid, String transactionid);

    public void deleteTransactionsByInventory(@PathVariable String bookid);
    public void deleteTransactionsByEmployee(@PathVariable String employeeid);
    public void deleteTransactionsByMember(@PathVariable String memberid);
}
