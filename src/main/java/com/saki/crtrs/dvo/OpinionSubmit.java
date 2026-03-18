package com.saki.crtrs.dvo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class OpinionSubmit {
    private BigInteger orderId; // 订单id
    private String opinion; // 评价内容
    private Integer opinionStar; // 评价星级
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date opinionTime; // 评价时间
}
