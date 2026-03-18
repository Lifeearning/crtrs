package com.saki.crtrs.dvo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IndexCardDVO {
    private Integer bId; // 商品卡片id（书籍id）
    private String bImageUrl; // 商品卡片图片地址
    private String title; // 书籍名称
    private String isbn; // ISBN码
    private BigDecimal price; // 价格
    private String major; // 关联专业
    private String course; // 关联课程
    private String author; // 书籍作者
    private String publisher; // 出版社
}
