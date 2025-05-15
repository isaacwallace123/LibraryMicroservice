package com.isaacwallace.api_gateway.Services.Transaction.Business;

import com.isaacwallace.api_gateway.DomainClient.TransactionServiceClient;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionResponseModel;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.TransactionController;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionServiceClient transactionServiceClient;

    public TransactionServiceImpl(TransactionServiceClient transactionServiceClient) {
        this.transactionServiceClient = transactionServiceClient;
    }

    public List<TransactionResponseModel> getAllTransactions() {
        return this.transactionServiceClient.getTransactions().stream().map(this::addLinks).toList();
    }

    public TransactionResponseModel getTransactionById(String transactionid) {
        return this.addLinks(this.transactionServiceClient.getTransactionByTransactionId(transactionid));
    }

    public TransactionResponseModel addTransaction(TransactionRequestModel transactionRequestModel) {
        return this.addLinks(this.transactionServiceClient.addTransaction(transactionRequestModel));
    }

    public TransactionResponseModel updateTransaction(String transactionid, TransactionRequestModel transactionRequestModel) {
        return this.addLinks(this.transactionServiceClient.updateTransaction(transactionid, transactionRequestModel));
    }

    public void deleteTransaction(String transactionid) {this.transactionServiceClient.deleteTransaction(transactionid);}

    public List<TransactionResponseModel> getTransactionsByMemberId(String memberid) {
        return this.transactionServiceClient.getTransactionsFromMember(memberid).stream().map(this::addLinks).toList();
    }

    public TransactionResponseModel getMemberTransactionByTransactionId(String memberid, String transactionid) {
        return this.addLinks(this.transactionServiceClient.getMemberTransactionById(memberid, transactionid));
    }

    private TransactionResponseModel addLinks(TransactionResponseModel transactionResponseModel) {
        Link selfLink = linkTo(methodOn(TransactionController.class)
                .getTransactionById(transactionResponseModel.getTransactionid()))
                .withSelfRel();
        transactionResponseModel.add(selfLink);

        Link allLink = linkTo(methodOn(TransactionController.class)
                .getTransactions())
                .withRel("transactions");
        transactionResponseModel.add(allLink);

        return transactionResponseModel;
    }
}
