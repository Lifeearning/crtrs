package com.saki.crtrs.controller;

import com.saki.crtrs.dto.User;
import com.saki.crtrs.dvo.LoginDVO;
import com.saki.crtrs.dvo.ReLogin;
import com.saki.crtrs.dvo.RegisterDVO;
import com.saki.crtrs.dvo.UpdateDataDVO;
import com.saki.crtrs.service.UserService;
import com.saki.crtrs.utils.JwtUtil;
import com.saki.crtrs.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

//    注册
    @PostMapping("/register")
    public ResultUtil<User> register(@RequestBody RegisterDVO registerDVO){
        User user = userService.findByUserCount(registerDVO.getUsername());
        if(user == null){
            System.out.println("为空"+user);
//            用户名没有被占用则注册
            userService.register(registerDVO.getUsername(),registerDVO.getPassword(),registerDVO.getPhone());
            return ResultUtil.success();
        }else {
            System.out.println(111);
//            用户名已经被占用
            return ResultUtil.error("用户名已被占用");
        }
    }

//    登录
    @PostMapping("/login")
    public ResultUtil<ReLogin> login(@RequestBody LoginDVO loginDVO){
        User user = userService.findByUserCount(loginDVO.getU_name());
        if (user == null){
            return ResultUtil.error("用户不存在");
        }else {
            if (user.getUPassword().equals(loginDVO.getU_pwd())){
//                获取用户token
                Map<String, Object> claims = new HashMap<>();
                claims.put("uid", user.getUid().toString());
                claims.put("uCount", user.getUCount());
                String token = JwtUtil.genToken(claims);
//                返回用户基本信息
                ReLogin reLogin = new ReLogin();
                reLogin.setUCount(user.getUCount());
                reLogin.setUid(user.getUid());
                reLogin.setAvatar(user.getAvatar());
                reLogin.setBalance(user.getBalance());
                reLogin.setUPho(user.getUPho());
                reLogin.setToken(token);
                return ResultUtil.success(reLogin);
            }else {
                return ResultUtil.error("密码错误");
            }
        }
    }

    /**
     * 修改用户信息（包含头像上传）
     * @param file 头像文件（可选）
     * @param request 请求对象，获取其他参数
     * @return 统一返回结果
     */
    @PostMapping("/update")
    public ResultUtil<String> updateUserInfo(
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpServletRequest request)
    {
        // 1. 获取普通参数
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        // 2. 校验至少修改一项（头像/手机号/密码）
        boolean hasAvatar = file != null && !file.isEmpty();
        boolean hasPhone = phone != null && !phone.trim().isEmpty();
        boolean hasPassword = password != null && !password.trim().isEmpty();

        if (!hasAvatar && !hasPhone && !hasPassword) {
            return ResultUtil.error("请至少修改一项信息（头像/手机号/密码）");
        }

        // 3. 手机号格式校验（仅当传递手机号时校验）
        if (hasPhone) {
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return ResultUtil.error("手机号格式不正确");
            }
        }

        // 4. 密码格式校验（仅当传递密码时校验）
        if (hasPassword) {
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";
            if (!password.matches(passwordRegex)) {
                return ResultUtil.error("密码需8-16位，包含大小写字母、数字和特殊字符(@$!%*?&)");
            }
        }

        // 5. 调用服务层处理业务逻辑（头像上传 + 用户信息更新）
        // 注意：需要修改userService.updateUserInfo方法，支持单独更新手机号/密码/头像
        String info = userService.updateUserInfo(file, phone, password);
        return ResultUtil.success(info);
    }

//    查询用户基本讯息
    @GetMapping("/getInfo")
    public ReLogin getInfo(){
        return userService.getInfo();
    }
}
