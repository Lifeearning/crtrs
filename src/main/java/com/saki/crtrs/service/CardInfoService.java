package com.saki.crtrs.service;

import com.saki.crtrs.dto.BCard;
import com.saki.crtrs.dvo.IndexCardDVO;
import com.saki.crtrs.dvo.ReCardDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface CardInfoService {
//    获取所有已经审核且未售出的订单
    public List<IndexCardDVO> getAll_isAudit_isNotSold();

//    获取商品详情
    public ReCardDetails getDetails(Integer bId);

//    发布商品
    public void publish(MultipartFile file, String uid, BCard bCard) throws IOException;

//    根据uid获取用户商品卡片信息
    public List<BCard> getInfoByUid(int uid);

//    根据bid删除商品卡片信息
    Integer deletCardByBid(Integer bid);
}