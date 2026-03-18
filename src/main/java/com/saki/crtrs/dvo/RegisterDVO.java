package com.saki.crtrs.dvo;

import lombok.Data;

@Data
public class RegisterDVO {
    private String username; // 用户账号
    private String password; // 用户密码
    private String phone; // 用户手机号
}
