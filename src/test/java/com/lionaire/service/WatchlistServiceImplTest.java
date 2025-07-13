package com.lionaire.service;

import com.lionaire.model.Coin;
import com.lionaire.model.User;
import com.lionaire.model.Watchlist;
import com.lionaire.repository.WatchlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WatchlistServiceImplTest {
    private Long userId;
    private WatchlistServiceImpl watchlistService;
    private WatchlistRepository mockRepo;
    private User user;
    private Watchlist mockList;

    @BeforeEach
    void setup(){
        userId = 100000000L;
        mockRepo = mock(WatchlistRepository.class);
        mockList = new Watchlist();
        watchlistService = new WatchlistServiceImpl(mockRepo);
        user = new User();
        user.setId(userId);
    }

    @Test
    void findUserWatchlist_shouldReturnWatchList() throws Exception {
        when(mockRepo.findByUserId(userId)).thenReturn(mockList);
        Watchlist result = watchlistService.findUserWatchlist(userId);

        assertNotNull(result);
        assertEquals(mockList, result);
        verify(mockRepo).findByUserId(userId);
    }

    @Test
    void findUserWatchlist_shouldThrow_whenNotFound() {
        when(mockRepo.findByUserId(userId)).thenReturn(null);

        Exception ex = assertThrows(Exception.class,() -> watchlistService.findUserWatchlist(userId));
        assertEquals("watchlist not found", ex.getMessage());
    }

    @Test
    void createWatchlist_shouldSaveAndReturn() {
        when(mockRepo.save(any())).thenReturn(mockList);

        Watchlist result = watchlistService.createWatchlist(user);

        assertNotNull(result);
        verify(mockRepo).save(any());
    }


    @Test
    void findById_shouldReturn_notNull() throws Exception {
        when(mockRepo.findById(userId)).thenReturn(Optional.of(mockList));

        Watchlist result = watchlistService.findById(userId);

        assertNotNull(result);
        assertEquals(mockList, result);
        verify(mockRepo).findById(userId);
    }

    @Test
    void findById_shouldThrow() throws Exception {
        when(mockRepo.findById(userId)).thenReturn(Optional.empty());
        Exception ex = assertThrows(Exception.class, () -> watchlistService.findById(userId));

        assertEquals("watchlist not found", ex.getMessage());
        verify(mockRepo).findById(userId);
    }

    @Test
    void addCoinToWatchlist_shouldRemoved() throws Exception {
        Coin coin = new Coin();
        List<Coin> coinList = new ArrayList<>();
        coinList.add(coin);
        Watchlist watchlist = new Watchlist();
        watchlist.setCoin(coinList);

        WatchlistServiceImpl spyService = spy(watchlistService);
        doReturn(watchlist).when(spyService).findUserWatchlist(userId);
        spyService.addCoinToWatchlist(coin,user);

        assertFalse(watchlist.getCoin().contains(coin));
        verify(mockRepo).save(watchlist);
    }

    @Test
    void addCoinToWatchlist_shouldAdded() throws Exception {
        Coin coin = new Coin();
        Watchlist watchlist = new Watchlist();
        watchlist.setCoin(new ArrayList<>());

        WatchlistServiceImpl spyService = spy(watchlistService);
        doReturn(watchlist).when(spyService).findUserWatchlist(userId);

        Coin result = spyService.addCoinToWatchlist(coin,user);

        assertTrue(watchlist.getCoin().contains(coin));
        assertEquals(coin,result);
        verify(mockRepo).save(watchlist);
    }
}