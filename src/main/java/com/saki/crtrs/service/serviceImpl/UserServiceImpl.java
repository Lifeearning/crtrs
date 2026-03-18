package com.saki.crtrs.service.serviceImpl;

import com.saki.crtrs.dto.User;
import com.saki.crtrs.dvo.ReLogin;
import com.saki.crtrs.mapper.UserMapper;
import com.saki.crtrs.service.UserService;
import com.saki.crtrs.utils.FileUploadUtil;
import com.saki.crtrs.utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Resource
    private FileUploadUtil fileUploadUtil;
    // FTP配置（从yml读取）
    @Value("${ftp.host}")
    private String ftpHost;
    @Value("${ftp.port}")
    private int ftpPort;
    @Value("${ftp.username}")
    private String ftpUsername;
    @Value("${ftp.password}")
    private String ftpPassword;
    @Value("${ftp.base-path}") // FTP服务器存储目录，如 /www/images/
    private String ftpBasePath;
    @Value("${file.upload.access-prefix}") // FTP图片访问前缀，如 http://xxx.xxx.xxx/images/
    private String accessPrefix;

    @Override
    public User findByUserCount(String uCount) {
        return userMapper.findByUserCount(uCount);
    }

    @Override
    public void register(String uCount, String uPassword, String uPho) {
        userMapper.register(uCount, uPassword, uPho);
    }

    @Override
    public String updateUserInfo(MultipartFile file, String phone, String password) {
        // 获取登录用户uid
        Map<String, Object> claim= ThreadLocalUtil.get();
        String uidStr = (String) claim.get("uid");
        BigInteger uid = new BigInteger(uidStr);

        // 查询用户信息
        User user = userMapper.getUserInfoByUid(uid);
        if(user == null){
            return "用户不存在";
        }

        // 最终要更新的字段（默认 = 原来的值，不修改）
        String finalAvatar = user.getAvatar(); // 头像默认不变
        String finalPhone = user.getUPho();    // 手机号默认不变
        String finalPassword = user.getUPassword(); // 密码默认不变

        // ==============================================
        // 1. 头像更新（有文件才上传 + 删除旧头像）
        // ==============================================
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String suffix = "." + FileUploadUtil.getFilenameExtension(originalFilename);
            String fileName = uid + "_" + System.currentTimeMillis() + suffix;

            boolean isUploadSuccess = false;
            try {
                isUploadSuccess = fileUploadUtil.uploadFile(fileName, file.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (isUploadSuccess) {
                // 删除旧头像
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    fileUploadUtil.deleteFile(user.getAvatar());
                }
                // 新头像
                finalAvatar = accessPrefix + fileName;
            }
        }

        // ==============================================
        // 2. 手机号更新（有值才改）
        // ==============================================
        if (phone != null && !phone.trim().isEmpty()) {
            finalPhone = phone.trim();
        }

        // ==============================================
        // 3. 密码更新（有值才校验 + 改）
        // ==============================================
        if (password != null && !password.trim().isEmpty()) {
            // 禁止和旧密码相同
            if (user.getUPassword().equals(password)) {
                return "密码不能与旧密码相同！";
            }
            finalPassword = password.trim();
        }

        // ==============================================
        // 执行更新（全部传参，不变的传原值）
        // ==============================================
        userMapper.updateUserInfo(uid, finalAvatar, finalPhone, finalPassword);

        return "修改成功！";
    }

//    获取用户基本信息
    @Override
    public ReLogin getInfo() {
//        获取登录用户uid
        Map<String, Object> claim= ThreadLocalUtil.get();
        String uidStr = (String) claim.get("uid");
        BigInteger uid = new BigInteger(uidStr);
//        查询用户信息
        User user = userMapper.getUserInfoByUid(uid);
        ReLogin loginInfo = new ReLogin();
        loginInfo.setBalance(user.getBalance());
        loginInfo.setAvatar(user.getAvatar());
        loginInfo.setUPho(user.getUPho());
        return loginInfo;
    }
}
