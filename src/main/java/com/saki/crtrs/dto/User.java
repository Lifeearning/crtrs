package com.saki.crtrs.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class User {
    private BigInteger uid; // 用户uid
    private String uCount; // 用户账号
    private String uPassword; // 用户密码
    private String uPho; // 用户手机号
    private String avatar; // 用户头像
    private BigDecimal balance; // 账号余额
}
