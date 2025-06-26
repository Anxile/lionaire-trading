package com.lionaire.model;

import com.lionaire.domain.WithdrawStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Withdraw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private WithdrawStatus status;

    private Long amount;

    @ManyToOne
    private  User user;

    private LocalDateTime date = LocalDateTime.now();
}
