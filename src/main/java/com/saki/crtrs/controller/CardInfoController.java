package com.saki.crtrs.controller;

import com.saki.crtrs.dto.BCard;
import com.saki.crtrs.dvo.IndexCardDVO;
import com.saki.crtrs.dvo.ReCardDetails;
import com.saki.crtrs.service.CardInfoService;
import com.saki.crtrs.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/index")
public class CardInfoController {
    @Autowired
    private CardInfoService cardInfoService;

//    首页获取所有通过审核且未售出的商品卡片
    @GetMapping("/getAll")
    public List<IndexCardDVO> getCard(){
        return cardInfoService.getAll_isAudit_isNotSold();
    }

//    根据bId获取商品详情信息
    @GetMapping ("/getDetails")
    public ReCardDetails reDetails(@RequestParam("bId") Integer bId){
        return cardInfoService.getDetails(bId);
    }

//    发布商品
    @PostMapping("/publish")
    public ResultUtil<String> publish(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        String uid = request.getParameter("uid");
        String title = request.getParameter("title");
        String major = request.getParameter("major");
        String course = request.getParameter("course");
        String isbn = request.getParameter("isbn");
        String author = request.getParameter("author");
        String publisher = request.getParameter("publisher");
        // 1. 先获取String类型的price参数
        String priceStr = request.getParameter("price");
        BigDecimal price = null;
        // 2. 非空+格式校验
        if (priceStr != null && !priceStr.trim().isEmpty()) {
            try {
                // 用BigDecimal的字符串构造器转换（支持小数格式）
                price = new BigDecimal(priceStr.trim());
            } catch (NumberFormatException e) {
                // 处理“price不是合法数字格式”的异常
                return ResultUtil.error("price参数必须是有效的数字格式（支持小数）");
            }
        } else {
            // 处理price为空的情况
            return ResultUtil.error("price参数不能为空");
        }
        String note = request.getParameter("desc");
        BCard bCard = new BCard();
        bCard.setAuthor(author);
        bCard.setCourse(course);
        bCard.setTitle(title);
        bCard.setMajor(major);
        bCard.setIsbn(isbn);
        bCard.setPublisher(publisher);
        bCard.setPrice(price);
        bCard.setNote(note);
        try {
            cardInfoService.publish(file, uid, bCard);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultUtil.success();
    }
//    根据uid获取用户商品卡片信息
    @GetMapping("/getMyBooks")
    public ResultUtil<List<BCard>> getMyBooks(@RequestParam Integer uid){
        List<BCard> bCardList = cardInfoService.getInfoByUid(uid);
        return ResultUtil.success(bCardList);
    }

//    根据bid删除商品卡片
    @PostMapping("/deleteBook")
    public ResultUtil<Integer> deleteCard(@RequestBody Map<String, Integer> params){
//        从Map中取“bid”对应的数字
        Integer bid = params.get("bid");
        System.out.println(bid);
        return ResultUtil.success(cardInfoService.deletCardByBid(bid));
    }
}
