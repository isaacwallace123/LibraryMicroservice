package com.isaacwallace.transaction_service.DataAccess;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction,String> {
    Transaction findTransactionByTransactionIdentifier_Transactionid(String transactionid);
    List<Transaction> findTransactionsByBookid(String bookid);
}
