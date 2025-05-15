package com.isaacwallace.api_gateway.Services.Transaction.Presentation;

import com.isaacwallace.api_gateway.Services.Transaction.Business.TransactionService;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionRequestModel;
import com.isaacwallace.api_gateway.Services.Transaction.Presentation.Models.TransactionResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
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
    public ResponseEntity<TransactionResponseModel> getMemberTransactionByTransactionId(@PathVariable String memberid, @PathVariable String transactionid) {
        return ResponseEntity.status(HttpStatus.OK).body(this.transactionService.getMemberTransactionByTransactionId(memberid, transactionid));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> addTransaction(@PathVariable String memberid, @RequestBody TransactionRequestModel transactionRequestModel) {
        transactionRequestModel.setMemberid(memberid);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.transactionService.addTransaction(transactionRequestModel));
    }

    @PutMapping(value = "{transactionid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> updateTransaction(@PathVariable String memberid, @PathVariable String transactionid, @RequestBody TransactionRequestModel transactionRequestModel) {
        transactionRequestModel.setMemberid(memberid);

        return ResponseEntity.status(HttpStatus.OK).body(this.transactionService.updateTransaction(transactionid, transactionRequestModel));
    }

    @DeleteMapping(value = "{transactionid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponseModel> deleteTransaction(@PathVariable String transactionid) {
        this.transactionService.deleteTransaction(transactionid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
