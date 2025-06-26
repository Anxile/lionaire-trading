package com.lionaire.repository;

import com.lionaire.domain.WalletTransactionType;
import com.lionaire.model.Wallet;
import com.lionaire.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long> {

    List<WalletTransaction> findByWalletOrderByDateDesc(Wallet wallet);

}
