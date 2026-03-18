package com.saki.crtrs.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class BCard {
    private Integer bId; // 商品卡片id（书籍id）
    private BigInteger uid; // 卡片所属用户uid
    private String bImageUrl; // 商品卡片图片地址
    private String title; // 书籍名称
    private String isbn; // ISBN码
    private BigDecimal price; // 价格
    private String major; // 关联专业
    private String course; // 关联课程
    private String note; // 备注
    private String author; // 书籍作者
    private String publisher; // 出版社
    private Integer isSold; // 卡片是否售出
}
