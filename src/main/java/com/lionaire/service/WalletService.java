package com.lionaire.service;

import com.lionaire.model.Order;
import com.lionaire.model.User;
import com.lionaire.model.Wallet;

public interface WalletService {
    Wallet getUserWallet(User user);

    Wallet addBalance(Wallet wallet, double amount);

    Wallet findWalletById(Long id);

    Wallet walletTransfer(User fromUser, User toUser, double amount);

    Wallet payOrder(Order order, User user);
}
