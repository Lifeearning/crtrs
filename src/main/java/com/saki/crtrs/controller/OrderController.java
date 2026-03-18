package com.saki.crtrs.controller;

import com.saki.crtrs.dvo.CreOrderDVO;
import com.saki.crtrs.dvo.OpinionSubmit;
import com.saki.crtrs.dvo.OrderInfoDVO;
import com.saki.crtrs.dvo.ProfitChartDVO;
import com.saki.crtrs.service.OrderService;
import com.saki.crtrs.utils.ResultUtil;
import com.saki.crtrs.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

//    根据uid获取用户所有的交易订单信息
    @GetMapping("/orderInfo")
    public List<OrderInfoDVO> orderInfo(){
//        获取登录用户uid
        Map<String, Object> claim= ThreadLocalUtil.get();
        String uidStr = (String) claim.get("uid");
        BigInteger uid = new BigInteger(uidStr);
//        根据uid查询用户订单信息
        List<OrderInfoDVO> orderInfo = orderService.getOrderInfoByUid(uid);
        return orderInfo;
    }

//    用户提交评论
    @PostMapping("/submitOpinion")
    public ResultUtil<String> submitOpinion(@RequestBody OpinionSubmit opinionSubmit){
        orderService.submitOpinion(opinionSubmit);
        return ResultUtil.success();
    }

//    获取用户订单情况并统计近5月收益
    @GetMapping("/profit")
    public ResultUtil<ProfitChartDVO> getProfitLast5M(){
        return ResultUtil.success(orderService.getProfitLast5M());
    }

//    用户提交订单
    @PostMapping("/createOrder")
    public ResultUtil<String> createOrder(@RequestBody CreOrderDVO creOrderDVO){
        return ResultUtil.success(orderService.creOrder(creOrderDVO));
    }
}
