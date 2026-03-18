package com.saki.crtrs.dvo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReCardDetails {
    private String uCount; // 商品卡片所属用户名
    private String FromCountAvatar; // 商品所属用户头像地址
    private String bImageUrl; // 商品卡片图片地址
    private String title; // 书籍名称
    private String isbn; // ISBN码
    private BigDecimal price; // 价格
    private String major; // 关联专业
    private String course; // 关联课程
    private String note; // 备注
    private String author; // 书籍作者
    private String publisher; // 出版社
}
