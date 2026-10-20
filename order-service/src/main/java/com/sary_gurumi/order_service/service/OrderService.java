package com.sary_gurumi.order_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sary_gurumi.order_service.client.ProductClient;
import com.sary_gurumi.order_service.client.ProductDTO;
import com.sary_gurumi.order_service.dto.CreateOrderRequest;
import com.sary_gurumi.order_service.dto.OrderItemRequest;
import com.sary_gurumi.order_service.entity.Order;
import com.sary_gurumi.order_service.entity.OrderItem;
import com.sary_gurumi.order_service.repository.OrderRepository;

import feign.FeignException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    @Transactional
    public Order create(CreateOrderRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            if (itemRequest.getProductId() == null || itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each item needs a productId and quantity > 0");
            }

            ProductDTO product = fetchProduct(itemRequest.getProductId());
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setPrice(product.getPrice());
            item.setQuantity(itemRequest.getQuantity());
            order.addItem(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private ProductDTO fetchProduct(Long productId) {
        try {
            ProductDTO product = productClient.getProductById(productId);
            if (product == null || product.getPrice() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + productId + " is invalid");
            }
            return product;
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + productId + " not found");
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not fetch product " + productId);
        }
    }
}
