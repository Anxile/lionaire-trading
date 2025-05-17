package com.lionaire.service;

import com.lionaire.domain.OrderType;
import com.lionaire.model.Order;
import com.lionaire.model.User;
import com.lionaire.model.Wallet;
import com.lionaire.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService{

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if(wallet!=null){
            wallet = new Wallet();
            wallet.setUser(user);
        }
        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, double amount) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance = balance.add(BigDecimal.valueOf(amount));

        wallet.setBalance(newBalance);

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findWalletById(Long id) {
        Optional<Wallet> wallet = walletRepository.findById(id);
        if(wallet.isPresent()){
            return wallet.get();
        }
        throw new RuntimeException("wallet not found");
    }

    @Override
    public Wallet walletTransfer(User fromUser, User toUser, double amount) {
        Wallet fromWallet = getUserWallet(fromUser);
        Wallet toWallet = getUserWallet(toUser);
        if(fromWallet.getBalance().compareTo(BigDecimal.valueOf(amount))>=0){
            toWallet.setBalance(toWallet.getBalance().add(BigDecimal.valueOf(amount)));
            fromWallet.setBalance(fromWallet.getBalance().subtract(BigDecimal.valueOf(amount)));
            walletRepository.save(toWallet);
            walletRepository.save(fromWallet);
            return toWallet;
        }
        throw new RuntimeException("insufficient balance");
    }

    @Override
    public Wallet payOrder(Order order, User user) {
        Wallet wallet = getUserWallet(user);

        if(order.getOrderType().equals(OrderType.BUY)) {
            if(wallet.getBalance().compareTo(order.getPrice())<0){
                throw new RuntimeException("insufficient balance");
            }
            wallet.setBalance(wallet.getBalance().subtract(order.getPrice()));
        }
        else {
            wallet.setBalance(wallet.getBalance().add(order.getPrice()));
        }
        walletRepository.save(wallet);
        return wallet;
    }
}
