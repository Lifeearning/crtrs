package com.saki.crtrs.service.serviceImpl;

import com.saki.crtrs.dto.BCard;
import com.saki.crtrs.dto.Order;
import com.saki.crtrs.dto.User;
import com.saki.crtrs.dvo.CreOrderDVO;
import com.saki.crtrs.dvo.OpinionSubmit;
import com.saki.crtrs.dvo.OrderInfoDVO;
import com.saki.crtrs.dvo.ProfitChartDVO;
import com.saki.crtrs.mapper.CardInfoMapper;
import com.saki.crtrs.mapper.OrderMapper;
import com.saki.crtrs.mapper.UserMapper;
import com.saki.crtrs.service.OrderService;
import com.saki.crtrs.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CardInfoMapper cardInfoMapper;

//    根据用户uid查询订单信息
    @Override
    public List<OrderInfoDVO> getOrderInfoByUid(BigInteger uid) {
        return orderMapper.getOrderInfoByUid(uid);
    }

//    提交评论
    @Override
    public void submitOpinion(OpinionSubmit opinionSubmit) {
        BigInteger orderId = opinionSubmit.getOrderId();
        String opinion = opinionSubmit.getOpinion();
        int star = opinionSubmit.getOpinionStar();
        Date time = opinionSubmit.getOpinionTime();
        orderMapper.submitOpinion(orderId, opinion, star, time);
    }

    @Override
    public ProfitChartDVO getProfitLast5M() {
        // 1. 获取当前登录用户uid
        Map<String, Object> claims = ThreadLocalUtil.get();
        String uidStr = (String) claims.get("uid");
        BigInteger uid = new BigInteger(uidStr);

        // 2. 从数据库查询近5个月收益数据
        List<Map<String, Object>> list = orderMapper.selectMonthlyProfitByUid(uid);

        // 3. 生成近5个月的所有月份（解决空月份问题）
        List<String> monthList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // 生成最近5个月：2025-11, 2025-12, 2026-01, 2026-02, 2026-03
        for (int i = 4; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            monthList.add(monthDate.format(formatter));
        }

        // 4. 封装 X轴（月份）、Y轴（收益）
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = new ArrayList<>();

        for (String month : monthList) {
            xAxisData.add(month);
            // 默认收益 0
            String profit = "0.00";

            // 数据库有这个月的数据 → 替换
            for (Map<String, Object> map : list) {
                String dbMonth = (String) map.get("month");
                if (month.equals(dbMonth)) {
                    BigDecimal totalProfit = (BigDecimal) map.get("totalProfit");
                    profit = totalProfit.toString();
                    break;
                }
            }
            yAxisData.add(profit);
        }

        // 5. 封装返回DVO
        ProfitChartDVO dvo = new ProfitChartDVO();
        dvo.setXAxisData(xAxisData);
        dvo.setYAxisData(yAxisData);
        return dvo;
    }

    // 创建订单
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务正常生效
    public String creOrder(CreOrderDVO creOrderDVO) {
        try {
            // 获取买家uid
            Map<String, Object> claims = ThreadLocalUtil.get();
            String uidStr = (String) claims.get("uid");
            BigInteger uid = new BigInteger(uidStr);

            // 商品bid
            Integer bid = creOrderDVO.getBId();
            // 商品价格
            BigDecimal price = creOrderDVO.getPrice();

            // 步骤一：参数合法性校验
            if (bid <= 0 || price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                return "无效的商品ID或价格";
            }

            // 步骤二：查询商品信息并加锁
            BCard bCard = cardInfoMapper.selectByIdWithLock(bid);
            if (bCard == null) {
                return "商品不存在";
            }

            // 校验商品状态
            if (bCard.getIsSold() == 1) {
                return "该商品已售出";
            }

            // 校验价格一致性
            if (bCard.getPrice().compareTo(price) != 0) {
                return "价格发生变化，请刷新重试";
            }

            // 步骤三：扣减买家余额
            User buyer = userMapper.getUserInfoByUid(uid);
            if (buyer == null) {
                return "用户不存在";
            }

            if (buyer.getBalance().compareTo(price) < 0) {
                return "余额不足";
            }

            int balanceUpdateRows = userMapper.decreaseBalance(uid, price);
            if (balanceUpdateRows <= 0) {
                return "余额扣减失败";
            }

            // 步骤四：给卖家增加余额
            BigInteger pubUid = bCard.getUid();
            int sellerBalanceUpdateRows = userMapper.increaseBalance(pubUid, price);
            if (sellerBalanceUpdateRows <= 0) {
                return "卖家余额增加失败";
            }

            // 步骤五：创建订单
            BigInteger buyUid = uid;
            int orderInsertRows = orderMapper.insert(buyUid, pubUid, bid);
            if (orderInsertRows <= 0) {
                return "订单创建失败";
            }

            // 步骤六：更新商品状态为已售出
            int cardUpdateRows = cardInfoMapper.updateById(bid);
            if (cardUpdateRows <= 0) {
                return "商品状态更新失败";
            }

            // 所有步骤成功
            return "订单创建成功！";

        } catch (Exception e) {
            // 捕获所有未知异常
            e.printStackTrace();
            return "系统异常：" + e.getMessage();
        }
    }
}
