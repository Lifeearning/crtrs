package com.saki.crtrs.dvo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
public class OrderInfoDVO {
    private BigInteger orderId; // 订单编号
    private Date orderTime; // 订单创建时间
    private String opinion; // 订单评价
    private Integer opinionStar; // 订单评价星级
    private Date opinionTime; // 评价时间
    private String title; // 商品名称
    private String isbn; // 商品isbn码
    private BigDecimal price; // 商品成交价
    private String bImageUrl; // 商品图片url
    private String uCountPub; // 卖家账号
    private String uCountBuy; // 买家账号
    private String uPhoPub; // 卖家手机号
    private String uPhoBuy; // 买家手机号
}
