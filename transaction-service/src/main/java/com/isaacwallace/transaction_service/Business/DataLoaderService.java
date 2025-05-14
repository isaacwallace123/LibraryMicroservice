package com.isaacwallace.transaction_service.Business;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacwallace.transaction_service.DataAccess.Transaction;
import com.isaacwallace.transaction_service.DataAccess.TransactionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataLoaderService {

    private final TransactionRepository repository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void loadData() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("data.json")) {
            if (is == null) {
                throw new IllegalStateException("data.json not found in resources");
            }

            List<Transaction> transactions = objectMapper.readValue(is, new TypeReference<>() {});
            repository.saveAll(transactions);
            System.out.println("✔ Loaded transactions from JSON");
        } catch (Exception e) {
            System.err.println("❌ Failed to load transaction data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}