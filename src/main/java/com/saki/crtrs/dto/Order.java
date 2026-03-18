package com.saki.crtrs.dto;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class Order {
    private BigInteger orderId; // 订单id
    private BigInteger pubUid; // 卖家uid
    private BigInteger buyUid; // 买家uid
    private Integer bid; // 卡片bid
    private Date orderTime; // 订单创建时间
    private String opinion; // 买家评价
}
