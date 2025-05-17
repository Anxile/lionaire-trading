package com.lionaire.controller;

import com.lionaire.domain.OrderType;
import com.lionaire.model.Coin;
import com.lionaire.model.Order;
import com.lionaire.model.User;
import com.lionaire.request.CreateOrderRequest;
import com.lionaire.service.CoinService;
import com.lionaire.service.OrderService;
import com.lionaire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private OrderService orderService;

    private UserService userSerivce;

    @Autowired
    private CoinService coinService;

    @Autowired
    private WalletTransactionService walletTransactionService;

//    private

    @Autowired
    public OrderController(OrderService orderService, UserService userSerivce) {
        this.orderService = orderService;
        this.userSerivce=userSerivce;
    }

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateOrderRequest req

    ) throws Exception {
        User user = userSerivce.findByJwt(jwt);
        Coin coin =coinService.findById(req.getCoinId());


        Order order = orderService.processOrder(coin,req.getQuantity(),req.getOrderType(),user);

        return ResponseEntity.ok(order);

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long orderId
    ) throws Exception {
        if (jwtToken == null) {
            throw new Exception("token missing...");
        }

        User user = userSerivce.findByJwt(jwtToken);

        Order order = orderService.getOrderById(orderId);
        if (order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(order);
        } else {
            throw new Exception("user not authorized to access this order");
        }
    }

    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrdersForUser(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam(required = false) String order_type,
            @RequestParam(required = false) String asset_symbol
    ) throws Exception {
        if (jwtToken == null) {
            throw new Exception("token missing...");
        }

        User userId = userSerivce.findByJwt(jwtToken);

        List<Order> userOrders = orderService.getOrdersByUser(userId, OrderType.valueOf(order_type),asset_symbol);
        return ResponseEntity.ok(userOrders);
    }




}
