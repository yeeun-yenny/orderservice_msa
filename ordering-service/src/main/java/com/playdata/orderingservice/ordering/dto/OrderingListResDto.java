package com.playdata.orderingservice.ordering.dto;

import com.playdata.orderingservice.ordering.entity.OrderStatus;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderingListResDto {

    // 하나의 주문에 대한 내용
    private Long id;
    private String userEmail;
    private OrderStatus orderStatus;

    // 주문 상세 내용
    @Getter @Setter @ToString
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class OrderDetailDto {
        private Long id;
        private String productName;
        private int count;
    }

}
