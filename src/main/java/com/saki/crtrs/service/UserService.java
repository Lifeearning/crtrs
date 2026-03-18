package com.saki.crtrs.service;

import com.saki.crtrs.dto.User;
import com.saki.crtrs.dvo.ReLogin;
import com.saki.crtrs.dvo.UpdateDataDVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {
//    根据用户名查找用户
    User findByUserCount(String uCount);

//    用户注册
    void register(String uCount, String uPassword, String uPho);

    //    用户信息更新
    String updateUserInfo(MultipartFile file, String phone, String password);

//    查询登录用户基本信息
    ReLogin getInfo();
}
