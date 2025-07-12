package com.lionaire.service;

import com.lionaire.domain.WalletTransactionType;
import com.lionaire.model.Wallet;
import com.lionaire.model.WalletTransaction;
import com.lionaire.repository.WalletTransactionRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WalletTransactionServiceImplTest {

    private Wallet wallet;
    private WalletTransactionType type;

    @Before
    public void steup(){
        wallet = new Wallet();
        type = WalletTransactionType.ADD_MONEY;
    }

    @Test
    void createTransaction_shouldReturnTransaction() {
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
        WalletTransactionRepository mockRepo = mock(WalletTransactionRepository.class);
        WalletTransaction expectedTx = new WalletTransaction();
        when(mockRepo.save(any())).thenReturn(expectedTx);

        WalletTransactionServiceImpl walletTransactionService = new WalletTransactionServiceImpl(mockRepo);
        List<WalletTransaction> result = walletTransactionService.getTransactions(wallet,type);

        assertNotNull(result);
        verify(mockRepo);
    }

}