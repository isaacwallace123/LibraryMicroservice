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
            System.out.println("Loaded transactions from JSON");
        } catch (Exception e) {
            System.err.println("Failed to load transaction data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/*
*
* package com.isaacwallace.transaction_service.Business;

import com.isaacwallace.transaction_service.DataAccess.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataLoaderService {
    private final TransactionRepository repository;

    @PostConstruct
    public void loadData() {
        repository.deleteAll();

        List<Transaction> transactions = List.of(
                new Transaction(null, new TransactionIdentifier("8b0a2c1b-734f-4cb1-b123-5f1e3e2b5a4f"),
                        "823e4567-e89b-12d3-a456-556642440007", "c1e2b3d4-5f6e-7a8b-9c0d-a112b2c3d4e5", "61d2d9f8-e144-4984-8bcb-7fa29ef4fdf6",
                        LocalDateTime.parse("2025-05-14T13:00:00"), Status.PENDING, new Payment(Method.CREDIT, Currency.USD, 23.45)
                ),
                new Transaction(null, new TransactionIdentifier("e61f972b-b5ad-4d2e-b43a-83776bc9dc6d"),
                        "123e4567-e89b-12d3-a456-556642440000", "g6i7e8f9-0f2e-3d5b-4c3d-e556f7g8i9j0", "710d4d5c-7243-47e7-bf10-9efb08740974",
                        LocalDateTime.parse("2025-05-14T13:00:00"), Status.COMPLETED, new Payment(Method.DEBIT, Currency.CAD, 18.75)
                ),
                new Transaction(null, new TransactionIdentifier("1d22efb9-cf6a-47d4-a683-0c95b0f7aaf7"),
                        "223e4567-e89b-12d3-a456-556642440001", "h7j8f9g0-1f3e-4d6b-5c4d-f667g8i0j1k1", "f1a83c15-3c1d-48cb-a4e7-3e96aa6d4ef3",
                        LocalDateTime.parse("2025-05-14T13:00:00"), Status.CANCELED, new Payment(Method.CASH, Currency.EUR, 5.50)
                ),
                new Transaction(null, new TransactionIdentifier("dfc25b13-502b-4d2d-80e5-8c7c4869f7ad"),
                        "723e4567-e89b-12d3-a456-556642440006", "f5h6d7e8-9f1e-2d4b-3c2d-d445e6f7g809", "2980698b-82b5-42fd-8dbb-33255eb637ed",
                        LocalDateTime.parse("2025-05-14T13:00:00"), Status.COMPLETED, new Payment(Method.CREDIT, Currency.CAD, 14.25)
                ),
                new Transaction(null, new TransactionIdentifier("a5a6a7a8-b5b5-4c5c-9d9d-eeeeeeeeeeee"),
                        "323e4567-e89b-12d3-a456-556642440002", "k0m1i2j3-4f6e-7d9b-8c7d-i990j1k3l414", "5aa1428a-a785-44f9-81cf-8545650251ed",
                        LocalDateTime.parse("2025-05-14T13:00:00"), Status.PENDING, new Payment(Method.DEBIT, Currency.CAD, 29.99)
                ),
                new Transaction(null, new TransactionIdentifier("f7d16f24-0a4b-43df-9fcb-9bd8c1f0aa67"),
                        "523e4567-e89b-12d3-a456-556642440004", "j9l0h1i2-3f5e-6d8b-7c6d-h889i0j2k313", "eec58e2f-44ad-41fb-b4cb-59b1ae89a68f",
                        LocalDateTime.parse("2025-05-14T13:00:00"), Status.COMPLETED, new Payment(Method.CASH, Currency.CAD, 7.99)
                ),
                new Transaction(null, new TransactionIdentifier("fe1c28bd-4af3-4b7a-90d7-f871f4e070ad"),
                        "423e4567-e89b-12d3-a456-556642440003", "e4g5c6d7-8f9e-0d3b-1c2d-c334d5e6f7g8", "b139a9b1-6c37-486b-80b3-5e2e0b3d90fa",
                        LocalDateTime.parse("2025-05-14T13:00:00"), Status.CANCELED, new Payment(Method.CREDIT, Currency.CAD, 12.99)
                ),
                new Transaction(null, new TransactionIdentifier("d50a8c92-f0e5-4aaf-8e4f-f8ad6e3a7b34"),
                        "923e4567-e89b-12d3-a456-556642440008", "i8k9g0h1-2f4e-5d7b-6c5d-g778h9j1k212", "e1b6f6d9-2c44-45da-bf7e-3d93f3a7c6a1",
                        LocalDateTime.parse("2025-05-14T13:00:00"), Status.PENDING, new Payment(Method.DEBIT, Currency.CAD, 15.00)
                ),
                new Transaction(null, new TransactionIdentifier("cc55b880-b78e-403e-b02b-4d4dfba3315a"),
                        "a23e4567-e89b-12d3-a456-556642440009", "l1n2j3k4-5f7e-8d0b-9c8d-j101k2l4n515", "8da6cebf-5868-456f-959f-ff6725310d78",
                        LocalDateTime.parse("2025-05-14T13:00:00"), Status.COMPLETED, new Payment(Method.CREDIT, Currency.USD, 21.99)
                )
        );

        repository.saveAll(transactions);
        System.out.println("✔ Loaded transactions (hardcoded)");
    }
}
*
*
* */