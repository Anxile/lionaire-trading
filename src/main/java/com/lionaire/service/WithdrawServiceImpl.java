package com.lionaire.service;

import com.lionaire.domain.WithdrawStatus;
import com.lionaire.model.User;
import com.lionaire.model.Withdraw;
import com.lionaire.repository.WithdrawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WithdrawServiceImpl implements WithdrawService{
    @Autowired
    private WithdrawRepository withdrawRepository;

    @Override
    public Withdraw requestWithdraw(Long amount, User user) {
        Withdraw withdraw = new Withdraw();
        withdraw.setAmount(amount);
        withdraw.setUser(user);
        withdraw.setStatus(WithdrawStatus.PENDING);
        return withdrawRepository.save(withdraw);
    }

    @Override
    public Withdraw processWithdrawal(Long withdrawalId, boolean accept) throws Exception {
        Optional<Withdraw> WithdrawOptional=withdrawRepository.findById(withdrawalId);

        if(WithdrawOptional.isEmpty()){
            throw new Exception("Withdraw id is wrong...");
        }

        Withdraw withdrawal=WithdrawOptional.get();


        withdrawal.setDate(LocalDateTime.now());

        if(accept){
            withdrawal.setStatus(WithdrawStatus.SUCCESS);
        }
        else{
            withdrawal.setStatus(WithdrawStatus.DECLINE);
        }

        return withdrawRepository.save(withdrawal);
    }

    @Override
    public List<Withdraw> getUsersWithdrawHistory(User user) {
        return withdrawRepository.findByUserId(user.getId());
    }

    @Override
    public List<Withdraw> getAllWithdrawalRequest() {
        return withdrawRepository.findAll();
    }
}
