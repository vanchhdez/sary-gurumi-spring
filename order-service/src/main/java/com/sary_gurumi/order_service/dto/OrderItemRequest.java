package com.sary_gurumi.order_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}
