package com.saki.crtrs.mapper;

import com.saki.crtrs.dvo.OpinionSubmit;
import com.saki.crtrs.dvo.OrderInfoDVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
//    根据uid查询用户订单信息
    @Select("SELECT o.order_id AS orderId, o.order_time AS orderTime,o.opinion AS opinion,o.opinion_star AS opinionStar,o.opinion_time AS opinionTime,bc.title AS title,bc.isbn AS isbn,bc.price AS price,bc.b_image_url AS bImageUrl, pub_user.u_count AS uCountPub,pub_user.u_pho AS uPhoPub,buy_user.u_count AS uCountBuy,buy_user.u_pho AS uPhoBuy " +
            "FROM `order` o " +
            "LEFT JOIN b_card bc ON o.bid = bc.b_id " +
            "LEFT JOIN `user` pub_user ON o.pub_uid = pub_user.uid " +
            "LEFT JOIN `user` buy_user ON o.buy_uid = buy_user.uid " +
            "WHERE  o.pub_uid = #{uid} OR o.buy_uid = #{uid} " +
            "ORDER BY o.order_time DESC;")
    List<OrderInfoDVO> getOrderInfoByUid(BigInteger uid);

//    提交评论
    @Update("UPDATE `order` " +
            "SET opinion=#{opinion}, opinion_star=#{star}, opinion_time=#{time} " +
            "WHERE order_id=#{orderId}")
    void submitOpinion(BigInteger orderId, String opinion, int star, Date time);

//    获取近5月的收益数据
    @Select("SELECT " +
            "    DATE_FORMAT(o.order_time, '%Y-%m') AS month, " +
            "    SUM(bc.price) AS totalProfit " +
            "FROM `order` o " +
            "LEFT JOIN b_card bc ON o.bid = bc.b_id " +
            "WHERE o.pub_uid = #{uid} " +
            "  AND o.order_time >= DATE_SUB(CURDATE(), INTERVAL 5 MONTH) " +
            "GROUP BY DATE_FORMAT(o.order_time, '%Y-%m') " +
            "ORDER BY month ASC")
    List<Map<String, Object>> selectMonthlyProfitByUid(BigInteger uid);

//    插入订单
    @Insert("INSERT INTO `order` (pub_uid, buy_uid, bid) VALUES (#{pubUid}, #{buyUid}, #{bid})")
    int insert(BigInteger buyUid, BigInteger pubUid, int bid);
}
