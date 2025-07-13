package com.lionaire.service;

import com.lionaire.model.Coin;
import com.lionaire.model.User;
import com.lionaire.model.Watchlist;
import com.lionaire.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WatchlistServiceImpl implements WatchlistService{
    @Autowired
    private WatchlistRepository watchlistRepository;

    public WatchlistServiceImpl(WatchlistRepository watchlistRepository){
        this.watchlistRepository = watchlistRepository;
    }

    @Override
    public Watchlist findUserWatchlist(Long userId) throws Exception{
        Watchlist watchlist = watchlistRepository.findByUserId(userId);
        if(watchlist == null){
            throw new Exception("watchlist not found");
        }
        return watchlist;
    }

    @Override
    public Watchlist createWatchlist(User user) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        return watchlistRepository.save(watchlist);
    }

    @Override
    public Watchlist findById(Long id) throws Exception{
        Optional<Watchlist> watchlistOptional = watchlistRepository.findById((id));
        if (watchlistOptional.isEmpty()){
            throw new Exception("watchlist not found");
        }
        return watchlistOptional.get();
    }

    @Override
    public Coin addCoinToWatchlist(Coin coin, User user) throws Exception {
        Watchlist watchlist = findUserWatchlist(user.getId());
        if(watchlist.getCoin().contains(coin)){
            watchlist.getCoin().remove(coin);
        }
        else watchlist.getCoin().add(coin);
        watchlistRepository.save(watchlist);
        return coin;
    }
}
