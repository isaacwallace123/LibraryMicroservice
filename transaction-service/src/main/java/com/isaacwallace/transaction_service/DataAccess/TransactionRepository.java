package com.isaacwallace.transaction_service.DataAccess;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction,String> {
    Transaction findTransactionByTransactionIdentifier_Transactionid(String transactionid);
    Transaction findTransactionByMemberidAndTransactionIdentifier_Transactionid(String memberid, String transactionid);

    List<Transaction> findTransactionsByBookid(String bookid);
    List<Transaction> findTransactionsByEmployeeid(String employeeid);
    List<Transaction> findTransactionsByMemberid(String memberid);
}
