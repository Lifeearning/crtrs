package com.saki.crtrs.service.serviceImpl;

import com.saki.crtrs.dto.BCard;
import com.saki.crtrs.dto.User;
import com.saki.crtrs.dvo.IndexCardDVO;
import com.saki.crtrs.dvo.ReCardDetails;
import com.saki.crtrs.mapper.CardInfoMapper;
import com.saki.crtrs.mapper.UserMapper;
import com.saki.crtrs.service.CardInfoService;
import com.saki.crtrs.utils.FileUploadUtil;
import com.saki.crtrs.utils.ResultUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Service
public class CardInfoServiceImpl implements CardInfoService {
    @Autowired
    private CardInfoMapper cardInfoMapper;
    @Resource
    private FileUploadUtil fileUploadUtil;
    @Autowired
    private UserMapper userMapper;
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


//     获取所有已经审核且未售出的卡片
    @Override
    public List<IndexCardDVO> getAll_isAudit_isNotSold() {
        return cardInfoMapper.getAll_isAudit_isNotSold();
    }

//    获取商品详情信息
    @Override
    public ReCardDetails getDetails(Integer bId) {
        ReCardDetails reCardDetails = new ReCardDetails();
//        根据bId获取用户头像与用户账户名
        User user=userMapper.getUserInfoByBid(bId);
//        根据bId获取书籍详情信息
        BCard bCard=cardInfoMapper.getBCardInfoByBid(bId);
//        将信息封装到reCardDetails中
        reCardDetails.setUCount(user.getUCount());
        reCardDetails.setFromCountAvatar(user.getAvatar());
        reCardDetails.setBImageUrl(bCard.getBImageUrl());
        reCardDetails.setTitle(bCard.getTitle());
        reCardDetails.setIsbn(bCard.getIsbn());
        reCardDetails.setPrice(bCard.getPrice());
        reCardDetails.setMajor(bCard.getMajor());
        reCardDetails.setCourse(bCard.getCourse());
        reCardDetails.setNote(bCard.getNote());
        reCardDetails.setAuthor(bCard.getAuthor());
        reCardDetails.setPublisher(bCard.getPublisher());
        return reCardDetails;
    }

    //     发布商品（待审核状态）
    @Override
    public void publish(MultipartFile file, String uid, BCard bCard) throws IOException {
        // 定义图片地址，默认为空
        String imgUrl = "";

        // ========== ✅核心：图片可以为空，不为空时才执行FTP上传 ==========
        if (file != null && !file.isEmpty()) {
            // 1. 文件名规则：uid + 时间戳 + 后缀
            String originalFilename = file.getOriginalFilename();
            String suffix = "." + FileUploadUtil.getFilenameExtension(originalFilename);
            String fileName = uid + "_" + System.currentTimeMillis() + suffix;

            // 2. 调用FTP工具类上传图片到宝塔，不上传本地文件了
            boolean isUploadSuccess = fileUploadUtil.uploadFile(fileName, file.getInputStream());
            if (isUploadSuccess) {
                // 上传成功，拼接宝塔的图片访问地址
                imgUrl = accessPrefix + fileName;
                bCard.setBImageUrl(imgUrl);
            }
        }
        // 拼接并返回图片访问URL
        BigInteger u_id = null;
        if (uid != null && !uid.trim().isEmpty()) {
            try {
                // 将String类型的uid转换为BigInteger
                u_id = new BigInteger(uid.trim());
                bCard.setUid(u_id);
            } catch (NumberFormatException e) {
                System.out.println("error,类型转换失败");
            }
        }
//        将商品卡片信息上传到数据库
        cardInfoMapper.publish(bCard.getUid(), bCard.getBImageUrl(), bCard.getTitle(),
                bCard.getIsbn(), bCard.getPrice(), bCard.getMajor(), bCard.getCourse(),
                bCard.getNote(), bCard.getAuthor(), bCard.getPublisher());
    }

//    根据用户uid查询商品卡片信息
    @Override
    public List<BCard> getInfoByUid(int uid) {
        return cardInfoMapper.getBCardInfoByUid(uid);
    }

//    根据商品bid删除商品卡片信息
    @Override
    public Integer deletCardByBid(Integer bid) {
//        获取商品图片对应的服务器url
        String imgUrl = cardInfoMapper.getImgByBid(bid);
//        删除服务器中的图片
        fileUploadUtil.deleteFile(imgUrl);
        return cardInfoMapper.deleteCardByBid(bid);
    }
}
