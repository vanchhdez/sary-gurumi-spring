package com.sary_gurumi.order_service.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class CreateOrderRequest {
    private List<OrderItemRequest> items;
}
