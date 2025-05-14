package com.isaacwallace.transaction_service.DataAccess;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Getter
public class TransactionIdentifier {
    @Field(name = "transactionid")
    private String transactionid;

    public TransactionIdentifier() {
        this.transactionid = UUID.randomUUID().toString();
    }

    public TransactionIdentifier(String transactionid) {
        this.transactionid = transactionid;
    }
}
