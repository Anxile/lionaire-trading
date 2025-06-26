package com.lionaire.service;

import com.lionaire.model.User;
import com.lionaire.model.Withdraw;

import java.util.List;

public interface WithdrawService {
    Withdraw requestWithdraw(Long amount, User user);

    Withdraw processWithdrawal(Long withdrawalId, boolean accept) throws Exception;

    List<Withdraw> getUsersWithdrawHistory(User user);

    List<Withdraw> getAllWithdrawalRequest();
}
