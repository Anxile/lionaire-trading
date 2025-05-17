package com.lionaire.service;

import com.lionaire.domain.OrderType;
import com.lionaire.model.Coin;
import com.lionaire.model.Order;
import com.lionaire.model.OrderItem;
import com.lionaire.model.User;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long id) throws Exception;

    List<Order> getOrdersByUser(User user, OrderType orderType, String assetSymbol);

    Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;
}
