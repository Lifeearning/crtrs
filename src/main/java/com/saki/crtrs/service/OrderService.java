package com.saki.crtrs.service;

import com.saki.crtrs.dvo.CreOrderDVO;
import com.saki.crtrs.dvo.OpinionSubmit;
import com.saki.crtrs.dvo.OrderInfoDVO;
import com.saki.crtrs.dvo.ProfitChartDVO;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public interface OrderService {
//    根据用户uid查询用户订单信息
    List<OrderInfoDVO> getOrderInfoByUid(BigInteger uid);

//    提交评论
    void submitOpinion(OpinionSubmit opinionSubmit);

//    获取用户近5月的收益
    ProfitChartDVO getProfitLast5M();

//    创建订单
    String creOrder(CreOrderDVO creOrderDVO);
}
