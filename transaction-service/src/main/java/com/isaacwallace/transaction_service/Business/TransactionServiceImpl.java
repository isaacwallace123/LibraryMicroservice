package com.isaacwallace.transaction_service.Business;

import com.isaacwallace.transaction_service.DataAccess.Transaction;
import com.isaacwallace.transaction_service.DataAccess.TransactionIdentifier;
import com.isaacwallace.transaction_service.DataAccess.TransactionRepository;
import com.isaacwallace.transaction_service.DomainClient.Employee.EmployeeServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Employee.Models.EmployeeResponseModel;
import com.isaacwallace.transaction_service.DomainClient.Inventory.InventoryServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Inventory.Models.BookResponseModel;
import com.isaacwallace.transaction_service.DomainClient.Membership.MembershipServiceClient;
import com.isaacwallace.transaction_service.DomainClient.Membership.Models.MemberResponseModel;
import com.isaacwallace.transaction_service.Mapper.TransactionRequestMapper;
import com.isaacwallace.transaction_service.Mapper.TransactionResponseMapper;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionResponseModel;
import com.isaacwallace.transaction_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.transaction_service.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionResponseMapper transactionResponseMapper;
    private final TransactionRequestMapper transactionRequestMapper;

    private final InventoryServiceClient inventoryServiceClient;
    private final MembershipServiceClient membershipServiceClient;
    private final EmployeeServiceClient employeeServiceClient;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionResponseMapper transactionResponseMapper, TransactionRequestMapper transactionRequestMapper, InventoryServiceClient inventoryServiceClient, MembershipServiceClient membershipServiceClient, EmployeeServiceClient employeeServiceClient) {
        this.transactionRepository = transactionRepository;
        this.transactionResponseMapper = transactionResponseMapper;
        this.transactionRequestMapper = transactionRequestMapper;

        this.inventoryServiceClient = inventoryServiceClient;
        this.membershipServiceClient = membershipServiceClient;
        this.employeeServiceClient = employeeServiceClient;
    }

    private void validateBookInvariant(Transaction transaction) {
        if (transaction.getBookid() == null) {
            throw new InvalidInputException("Transaction must be associated with a book.");
        }

        if (transaction.getMemberid() == null) {
            throw new InvalidInputException("Transaction must be associated with a member.");
        }

        if (transaction.getEmployeeid() == null) {
            throw new InvalidInputException("Transaction must be associated with an employee.");
        }

        if (transaction.getPayment().getAmount() <= 0) {
            throw new InvalidInputException("Transaction must have a positive amount.");
        }
    }

    public List<TransactionResponseModel> getAllTransactions() {
        return this.transactionResponseMapper.entitiesToResponseModelList(transactionRepository.findAll(), inventoryServiceClient, membershipServiceClient, employeeServiceClient);
    }

    public TransactionResponseModel getTransactionById(String transactionid) {
        Transaction transaction = this.transactionRepository.findTransactionByTransactionIdentifier_Transactionid(transactionid);

        if (transaction == null) {
            throw new NotFoundException("Unknown transaction id " + transactionid);
        }

        return this.transactionResponseMapper.entityToResponseModel(transaction, inventoryServiceClient, membershipServiceClient, employeeServiceClient);
    }

    public TransactionResponseModel addTransaction(TransactionRequestModel transactionRequestModel) {
        this.inventoryServiceClient.getInventoryById(transactionRequestModel.getBookid());
        this.membershipServiceClient.getMemberById(transactionRequestModel.getMemberid());
        this.employeeServiceClient.getEmployeeById(transactionRequestModel.getEmployeeid());

        Transaction transaction = this.transactionRequestMapper.requestModelToEntity(transactionRequestModel, new TransactionIdentifier());

        this.validateBookInvariant(transaction);

        return this.transactionResponseMapper.entityToResponseModel(this.transactionRepository.save(transaction), inventoryServiceClient, membershipServiceClient, employeeServiceClient);
    }

    public TransactionResponseModel updateTransaction(String transactionid, TransactionRequestModel transactionRequestModel) {
        this.inventoryServiceClient.getInventoryById(transactionRequestModel.getBookid());
        this.membershipServiceClient.getMemberById(transactionRequestModel.getMemberid());
        this.employeeServiceClient.getEmployeeById(transactionRequestModel.getEmployeeid());

        Transaction transaction = this.transactionRepository.findTransactionByTransactionIdentifier_Transactionid(transactionid);

        if (transaction == null) {
            throw new NotFoundException("Unknown transaction id " + transactionid);
        }

        this.transactionRequestMapper.updateEntityFromRequest(transactionRequestModel, transaction);

        Transaction updatedTransaction = this.transactionRepository.save(transaction);

        this.validateBookInvariant(transaction);

        return this.transactionResponseMapper.entityToResponseModel(updatedTransaction, inventoryServiceClient, membershipServiceClient, employeeServiceClient);
    }

    public void deleteTransaction(String transactionid) {
        Transaction transaction = this.transactionRepository.findTransactionByTransactionIdentifier_Transactionid(transactionid);

        if (transaction == null) {
            throw new NotFoundException("Unknown transaction id " + transactionid);
        }

        this.transactionRepository.delete(transaction);
    }
}
