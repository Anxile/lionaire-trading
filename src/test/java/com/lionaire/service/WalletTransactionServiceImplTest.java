package com.lionaire.service;

import com.lionaire.domain.WalletTransactionType;
import com.lionaire.model.Wallet;
import com.lionaire.model.WalletTransaction;
import com.lionaire.repository.WalletTransactionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletTransactionServiceImplTest {

    @Test
    void createTransaction_shouldReturnTransaction() {
        Wallet wallet = new Wallet();
        WalletTransactionType type = WalletTransactionType.ADD_MONEY;

        WalletTransactionRepository mockRepo = mock(WalletTransactionRepository.class);
        WalletTransaction expectedTx = new WalletTransaction();
        when(mockRepo.save(any())).thenReturn(expectedTx);

        WalletTransactionServiceImpl service = new WalletTransactionServiceImpl(mockRepo);

        WalletTransaction result = service.createTransaction(wallet, type, "111111111111", "add money", 100L);

        assertNotNull(result);
        assertEquals(expectedTx, result);
        verify(mockRepo, times(1)).save(any());
    }

    @Test
    void getTransactions() {
        Wallet wallet = new Wallet();
        WalletTransactionType type = WalletTransactionType.ADD_MONEY;

        WalletTransactionRepository mockRepo = mock(WalletTransactionRepository.class);
        WalletTransaction expectedTx = new WalletTransaction();
        when(mockRepo.save(any())).thenReturn(expectedTx);

        WalletTransactionServiceImpl walletTransactionService = new WalletTransactionServiceImpl(mockRepo);
        List<WalletTransaction> result = walletTransactionService.getTransactions(wallet,type);

        assertNotNull(result);
        verify(mockRepo);
    }

//    @Test
//    void getTransactions
}