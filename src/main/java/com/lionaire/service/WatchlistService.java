package com.lionaire.service;

import com.lionaire.model.Coin;
import com.lionaire.model.User;
import com.lionaire.model.Watchlist;

public interface WatchlistService {
    Watchlist findUserWatchlist(Long userId) throws Exception;
    Watchlist createWatchlist(User user);
    Watchlist findById(Long id) throws Exception;

    Coin addCoinToWatchlist(Coin coin, User user) throws Exception;
}
