package com.isaacwallace.transaction_service.Presentation;

import com.isaacwallace.transaction_service.Business.TransactionService;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionResponseModel>> getAllTransactions() {
        return ResponseEntity.status(HttpStatus.OK).body(this.transactionService.getAllTransactions());
    }

    @GetMapping(value = "{transactionid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> getTransactionById(@PathVariable String transactionid) {
        return ResponseEntity.status(HttpStatus.OK).body(this.transactionService.getTransactionById(transactionid));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> addMember(@RequestBody TransactionRequestModel transactionRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.transactionService.addTransaction(transactionRequestModel));
    }

    @PutMapping(value = "{transactionid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> EditCustomer(@PathVariable String transactionid, @RequestBody TransactionRequestModel transactionRequestModel) {
        return ResponseEntity.status(HttpStatus.OK).body(this.transactionService.updateTransaction(transactionid, transactionRequestModel));
    }

    @DeleteMapping(value = "{transactionid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> DeleteCustomer(@PathVariable String transactionid) {
        this.transactionService.deleteTransaction(transactionid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/inventory/{bookid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteTransactionsByInventory(@PathVariable String bookid) {
        this.transactionService.deleteTransactionsByInventory(bookid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/employee/{employeeid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteTransactionsByEmployee(@PathVariable String employeeid) {
        this.transactionService.deleteTransactionsByEmployee(employeeid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/member/{memberid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteTransactionsByMember(@PathVariable String memberid) {
        this.transactionService.deleteTransactionsByMember(memberid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}