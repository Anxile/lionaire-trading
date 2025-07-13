package com.lionaire.service;

import com.lionaire.domain.WalletTransactionType;
import com.lionaire.model.Wallet;
import com.lionaire.model.WalletTransaction;
import com.lionaire.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class WalletTransactionServiceImplTest {

    private Wallet wallet;
    private WalletTransactionType type;

    @BeforeEach
    public void steup(){
        wallet = new Wallet();
        type = WalletTransactionType.ADD_MONEY;
    }

    @ParameterizedTest
    @CsvSource({
            "123456, 'add money', 100",
            "789456, 'withdraw money', 12",
            "579412, 'no reason', 0"
    })
    void createTransaction_shouldReturnTransaction(String id, String purpose, Long amount) {
        WalletTransactionRepository mockRepo = mock(WalletTransactionRepository.class);
        WalletTransaction expectedTx = new WalletTransaction();
        when(mockRepo.save(any())).thenReturn(expectedTx);

        WalletTransactionServiceImpl service = new WalletTransactionServiceImpl(mockRepo);

        WalletTransaction result = service.createTransaction(wallet, type, id, purpose, amount);

        assertNotNull(result);
        assertEquals(expectedTx, result);
        verify(mockRepo, times(1)).save(any());
    }

    @Test
    void getTransactions() {
        WalletTransactionRepository mockRepo = mock(WalletTransactionRepository.class);
        WalletTransaction expectedTx = new WalletTransaction();
        when(mockRepo.save(any())).thenReturn(expectedTx);

        WalletTransactionServiceImpl walletTransactionService = new WalletTransactionServiceImpl(mockRepo);
        List<WalletTransaction> result = walletTransactionService.getTransactions(wallet,type);

        assertNotNull(result);
    }

}