package com.isaacwallace.transaction_service.DataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
public class TransactionRepositoryIntegrationTest {
    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setup() {
        transactionRepository.deleteAll();
    }

    @Test
    void testTransactionConstructor() {
        LocalDateTime now = LocalDateTime.now();

        TransactionIdentifier transactionIdentifier = new TransactionIdentifier();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);

        transaction.setTransactionIdentifier(transactionIdentifier);

        assertEquals(transaction.getTransactionIdentifier(), transactionIdentifier);
        assertEquals(transaction.getMemberid(), "1");
        assertEquals(transaction.getBookid(), "2");
        assertEquals(transaction.getEmployeeid(), "3");

        assertEquals(transaction.getTransactionDate(), now);
        assertEquals(Status.COMPLETED, transaction.getStatus());

        assertEquals(transaction.getPayment(), payment);
        assertEquals(Method.CASH, transaction.getPayment().getMethod());
        assertEquals(9.99, transaction.getPayment().getAmount());
        assertEquals(Currency.CAD, transaction.getPayment().getCurrency());
    }

    @Test
    void testPaymentConstructor() {
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        assertEquals(Method.CASH, payment.getMethod());
        assertEquals(9.99, payment.getAmount());
        assertEquals(Currency.CAD, payment.getCurrency());
    }

    @Test
    void testTransactionIdentifierConstructor() {
        TransactionIdentifier transactionIdentifier = new TransactionIdentifier("1");

        assertEquals(transactionIdentifier.getTransactionid(), "1");
    }

    @Test
    void testTransactionGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();

        Transaction transaction = new Transaction("1", "2", "3", LocalDateTime.now(), Status.COMPLETED, new Payment(Method.CASH, Currency.CAD, 9.99));

        transaction.setTransactionIdentifier(new TransactionIdentifier("1"));
        transaction.setMemberid("2");
        transaction.setBookid("3");
        transaction.setEmployeeid("3");
        transaction.setTransactionDate(now);
        transaction.setStatus(Status.COMPLETED);
        transaction.setPayment(new Payment(Method.CASH, Currency.CAD, 9.99));

        assertEquals(transaction.getTransactionIdentifier().getTransactionid(), "1");
        assertEquals(transaction.getMemberid(), "2");
        assertEquals(transaction.getBookid(), "3");
        assertEquals(transaction.getEmployeeid(), "3");
        assertEquals(transaction.getTransactionDate(), now);
        assertEquals(transaction.getStatus(), Status.COMPLETED);
        assertEquals(transaction.getPayment().getMethod(), Method.CASH);
        assertEquals(transaction.getPayment().getAmount(), 9.99);
        assertEquals(transaction.getPayment().getCurrency(), Currency.CAD);
    }

    @Test
    void testSaveTransaction() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);

        transaction.setTransactionIdentifier(new TransactionIdentifier("1"));
        transaction.setMemberid("2");
        transaction.setBookid("3");
        transaction.setEmployeeid("3");
        transaction.setTransactionDate(now);
        transaction.setStatus(Status.COMPLETED);
        transaction.setPayment(new Payment(Method.CASH, Currency.CAD, 9.99));

        transactionRepository.save(transaction);

        assertEquals(transaction.getTransactionIdentifier().getTransactionid(), "1");
        assertEquals(transaction.getMemberid(), "2");
        assertEquals(transaction.getBookid(), "3");
        assertEquals(transaction.getEmployeeid(), "3");
        assertEquals(transaction.getTransactionDate(), now);
        assertEquals(transaction.getStatus(), Status.COMPLETED);
        assertEquals(transaction.getPayment().getMethod(), Method.CASH);
        assertEquals(transaction.getPayment().getAmount(), 9.99);
        assertEquals(transaction.getPayment().getCurrency(), Currency.CAD);
    }

    @Test
    void testToStringContainsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);

        transaction.setTransactionIdentifier(new TransactionIdentifier("1"));

        transactionRepository.save(transaction);

        String toString = transaction.toString();

        assertTrue(toString.contains(transaction.getTransactionIdentifier().getTransactionid()));
        assertTrue(toString.contains(transaction.getMemberid()));
        assertTrue(toString.contains(transaction.getBookid()));
        assertTrue(toString.contains(transaction.getEmployeeid()));
        assertTrue(toString.contains(transaction.getTransactionDate().toString()));
        assertTrue(toString.contains(transaction.getStatus().toString()));
        assertTrue(toString.contains(transaction.getPayment().getMethod().toString()));
        assertTrue(toString.contains(transaction.getPayment().getAmount().toString()));
        assertTrue(toString.contains(transaction.getPayment().getCurrency().toString()));
    }

    @Test
    void testEqualsSameObject() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);

        transaction.setTransactionIdentifier(new TransactionIdentifier("1"));

        transactionRepository.save(transaction);

        assertEquals(transaction, transaction);
    }

    @Test
    void testEqualsDifferentClass() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);

        transaction.setTransactionIdentifier(new TransactionIdentifier("1"));

        transactionRepository.save(transaction);

        assertNotEquals(transaction, transaction.toString());
    }

    @Test
    void testEqualsNull() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);

        transaction.setTransactionIdentifier(new TransactionIdentifier("1"));

        transactionRepository.save(transaction);

        assertNotEquals(transaction, null);
    }

    @Test
    void testNotEqualsDifferentFields() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);

        Transaction transaction2 = new Transaction("1", "2", "3", now, Status.PENDING, payment);

        assertNotEquals(transaction, transaction2);
        assertNotEquals(transaction.toString(), transaction2.toString());
        assertNotEquals(transaction.hashCode(), transaction2.hashCode());
    }

    @Test
    void testEqualsDifferentId() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);

        Transaction transaction2 = new Transaction("2", "2", "3", now, Status.COMPLETED, payment);

        transaction.setTransactionIdentifier(new TransactionIdentifier());
        transaction2.setTransactionIdentifier(new TransactionIdentifier());

        assertNotEquals(transaction, transaction2);
        assertNotEquals(transaction.toString(), transaction2.toString());
        assertNotEquals(transaction.hashCode(), transaction2.hashCode());
        assertNotEquals(transaction.getTransactionIdentifier().getTransactionid(), transaction2.getTransactionIdentifier().getTransactionid());
    }

    @Test
    void testHashCodeDifferentObjects() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);

        Transaction transaction2 = new Transaction("1", "2", "3", now, Status.PENDING, payment);

        assertNotEquals(transaction.hashCode(), transaction2.hashCode());
    }

    @Test
    void testHashCodeConsistency() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        transaction.setTransactionIdentifier(new TransactionIdentifier("1"));

        int hash = transaction.hashCode();

        assertEquals(hash, transaction.hashCode());

        transaction.setTransactionIdentifier(new TransactionIdentifier("2"));

        assertNotEquals(hash, transaction.hashCode());
    }

    @Test
    void whenTransactionExists_thenReturnAllTransactions() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        transaction.setTransactionIdentifier(new TransactionIdentifier("1"));

        Transaction transaction2 = new Transaction("1", "2", "3", now, Status.PENDING, payment);
        transaction2.setTransactionIdentifier(new TransactionIdentifier("2"));

        transactionRepository.save(transaction);
        transactionRepository.save(transaction2);

        List<Transaction> transactions = transactionRepository.findAll();

        assertNotNull(transactions);
        assertNotEquals(0, transactions.size());
        assertEquals(2, transactions.size());
        assertEquals(transactions.size(), this.transactionRepository.count());
    }

    @Test
    void testEquals_DifferentMemberId() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        Transaction transaction2 = new Transaction("1", "3", "3", now, Status.COMPLETED, payment);
        assertNotEquals(transaction, transaction2);
    }

    @Test
    void testEquals_DifferentBookId() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        Transaction transaction2 = new Transaction("1", "2", "4", now, Status.COMPLETED, payment);
        assertNotEquals(transaction, transaction2);
    }

    @Test
    void testEquals_DifferentEmployeeId() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        Transaction transaction2 = new Transaction("1", "2", "3", now, Status.PENDING, payment);
        assertNotEquals(transaction, transaction2);
    }

    @Test
    void testEquals_DifferentTransactionDate() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        Transaction transaction2 = new Transaction("1", "2", "3", now.plusDays(1), Status.COMPLETED, payment);
        assertNotEquals(transaction, transaction2);
    }

    @Test
    void testEquals_DifferentStatus() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        Transaction transaction2 = new Transaction("1", "2", "3", now, Status.PENDING, payment);
        assertNotEquals(transaction, transaction2);
    }

    @Test
    void testEquals_DifferentPayment() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Payment payment2 = new Payment(Method.CASH, Currency.CAD, 10.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        Transaction transaction2 = new Transaction("1", "2", "3", now, Status.COMPLETED, payment2);
        assertNotEquals(transaction, transaction2);
    }

    @Test
    void testEquals_DifferentTransactionIdentifier() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        Transaction transaction2 = new Transaction("2", "2", "3", now, Status.COMPLETED, payment);
        assertNotEquals(transaction, transaction2);
    }

    @Test
    void testEquals_MixedNullFields() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        Transaction transaction2 = new Transaction("1", "2", "3", null, Status.COMPLETED, payment);
        assertNotEquals(transaction, transaction2);
    }

    @Test
    void whenTransactionExists_thenReturnTransactionsByTransactionId() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment(Method.CASH, Currency.CAD, 9.99);
        Transaction transaction = new Transaction("1", "2", "3", now, Status.COMPLETED, payment);
        transaction.setTransactionIdentifier(new TransactionIdentifier("1"));

        transactionRepository.save(transaction);

        Transaction foundTransaction = transactionRepository.findTransactionByTransactionIdentifier_Transactionid("1");

        assertNotNull(foundTransaction);

        assertEquals(transaction.getTransactionIdentifier().getTransactionid(), foundTransaction.getTransactionIdentifier().getTransactionid());

        assertEquals(transaction.getMemberid(), foundTransaction.getMemberid());
        assertEquals(transaction.getBookid(), foundTransaction.getBookid());
        assertEquals(transaction.getEmployeeid(), foundTransaction.getEmployeeid());
        assertEquals(transaction.getStatus(), foundTransaction.getStatus());
        assertEquals(transaction.getPayment().getMethod(), foundTransaction.getPayment().getMethod());
        assertEquals(transaction.getPayment().getAmount(), foundTransaction.getPayment().getAmount());
        assertEquals(transaction.getPayment().getCurrency(), foundTransaction.getPayment().getCurrency());
    }


}
