package com.lionaire.service;

import com.lionaire.domain.OrderStatus;
import com.lionaire.domain.OrderType;
import com.lionaire.model.*;
import com.lionaire.repository.OrderItemRepository;
import com.lionaire.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AssetService assetService;

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
        double price = orderItem.getCoin().getCurrentPrice()*orderItem.getQuantity();

        Order order = new Order();
        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setPrice(BigDecimal.valueOf(price));
        order.setOrderType(orderType);
        order.setPlacedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);


        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long id) throws Exception {

        return orderRepository.findById(id).orElseThrow(()->new Exception("order not found")) ;
    }

    @Override
    public List<Order> getOrdersByUser(User user, OrderType orderType, String assetSymbol) {
        return orderRepository.findByUserId(user.getId());
    }

    private OrderItem createOrderItem(Coin coin, double quantity, double buyPrice, double sellPrice){
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return orderItemRepository.save(orderItem) ;
    }

    @Transactional
    public Order buyAsset(Coin coin,double quantity, User user) throws Exception {
        if(quantity<0)throw new Exception("quantity should be > 0");
        double buyPrice=coin.getCurrentPrice();

        OrderItem orderItem = createOrderItem(coin,quantity,buyPrice,0);


        Order order = createOrder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);


        walletService.payOrder(order, user);

        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setOrderType(OrderType.BUY);

        Order savedOrder = orderRepository.save(order);

        Asset oldAsset = assetService.findAssetByUserIdAndCoinId(
                order.getUser().getId(),
                order.getOrderItem().getCoin().getId()
        );

        if (oldAsset == null) {
            assetService.createAsset(
                    user,orderItem.getCoin(),
                    orderItem.getQuantity()
            );
        } else {
            assetService.updateAsset(
                    oldAsset.getId(),quantity
            );
        }

        return savedOrder;
    }

    @Transactional
    public Order sellAsset(Coin coin,double quantity, User user) throws Exception {
        double sellPrice =coin.getCurrentPrice();

        Asset assetToSell = assetService.findAssetByUserIdAndCoinId(
                user.getId(),
                coin.getId()
        );

        if (assetToSell != null) {

            OrderItem orderItem = createOrderItem(coin,quantity, assetToSell.getBuyPrice(), sellPrice);

            Order order = createOrder(user, orderItem, OrderType.SELL);

            orderItem.setOrder(order);

            Order savedOrder = orderRepository.save(order);

            if (assetToSell.getQuantity() >= quantity) {

                walletService.payOrder(order, user);

                Asset updatedAsset=assetService.updateAsset(
                        assetToSell.getId(),
                        -quantity
                );
                if(updatedAsset.getQuantity()*coin.getCurrentPrice()<=1){
                    assetService.deleteAsset(updatedAsset.getId());
                }
                return savedOrder;
            } else {

                orderRepository.delete(order);
                throw new Exception("Insufficient quantity to sell");
            }
        }

        throw new Exception("Asset not found for selling");

    }

    @Override
    @Transactional
    public Order processOrder(Coin coin,double quantity,OrderType orderType, User user) throws Exception {

        if (orderType == OrderType.BUY) {
            return buyAsset(coin, quantity, user);
        } else if (orderType == OrderType.SELL) {
            return sellAsset(coin, quantity, user);
        } else {
            throw new Exception("Invalid order type");
        }
    }
}
