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
@RequestMapping("api/v1/members/{memberid}/transactions")
public class MemberTransactionController {
    private final TransactionService transactionService;

    public MemberTransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionResponseModel>> getAllTransactions(@PathVariable String memberid) {
        return ResponseEntity.status(HttpStatus.OK).body(this.transactionService.getTransactionsByMemberId(memberid));
    }

    @GetMapping(value = "{transactionid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> getTransactionById(@PathVariable String memberid, @PathVariable String transactionid) {
        return ResponseEntity.status(HttpStatus.OK).body(this.transactionService.getMemberTransactionByTransactionId(memberid, transactionid));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> addMember(@PathVariable String memberid, @RequestBody TransactionRequestModel transactionRequestModel) {
        transactionRequestModel.setMemberid(memberid);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.transactionService.addTransaction(transactionRequestModel));
    }

    @PutMapping(value = "{transactionid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> EditCustomer(@PathVariable String memberid, @PathVariable String transactionid, @RequestBody TransactionRequestModel transactionRequestModel) {
        transactionRequestModel.setMemberid(memberid);

        return ResponseEntity.status(HttpStatus.OK).body(this.transactionService.updateTransaction(transactionid, transactionRequestModel));
    }

    @DeleteMapping(value = "{transactionid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> DeleteCustomer(@PathVariable String transactionid) {
        this.transactionService.deleteTransaction(transactionid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
