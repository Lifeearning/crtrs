package com.saki.crtrs.dvo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreOrderDVO {
    private Integer bId; // 商品编码
    private BigDecimal price; // 商品价格
}
