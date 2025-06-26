package com.lionaire.service;

import com.lionaire.model.User;
import com.lionaire.model.Withdraw;

public interface WithdrawService {
    Withdraw requestWithdraw(Long amount, User user);
}
