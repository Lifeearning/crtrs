package com.saki.crtrs.mapper;

import com.saki.crtrs.dto.BCard;
import com.saki.crtrs.dvo.IndexCardDVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Mapper
public interface CardInfoMapper {
//    获取已经通过审核并且未售出的所有订单
    @Select("SELECT * FROM b_card WHERE is_sold=0")
    public List<IndexCardDVO> getAll_isAudit_isNotSold();

//    发布订单
    @Insert("INSERT INTO b_card (uid, b_image_url, title, isbn, price, major, course, note, author, publisher)" +
            " VALUES (#{uid}, #{imgUrl}, #{title}, #{isbn}, #{price}, #{major}, #{course}, #{note}, #{author}," +
            "#{publisher})")
    public void publish(BigInteger uid, String imgUrl, String title, String isbn, BigDecimal price,
                        String major, String course, String note, String author, String publisher);

//    根据商品bid查询商品信息
    @Select("SELECT * FROM b_card WHERE b_id=#{bId}")
    public BCard getBCardInfoByBid(Integer bId);

//    根据uid查询商品卡片信息
    @Select("SELECT * FROM b_card WHERE uid=#{uid}")
    public List<BCard> getBCardInfoByUid(Integer uid);

//    根据商品bid删除商品卡片信息
    @Delete("DELETE FROM b_card WHERE b_id=#{bid}")
    Integer deleteCardByBid(Integer bid);

//    根据bid获取商品卡片图片地址
    @Select("SELECT b_image_url FROM b_card WHERE b_id=#{bid}")
    String getImgByBid(Integer bid);

    /**
     * 根据ID查询，并加锁
     */
    @Select("SELECT * FROM b_card WHERE b_id = #{bid} FOR UPDATE")
    BCard selectByIdWithLock(@Param("bid") Integer bid);

//    根据bid更新商品销售状态
    @Update("UPDATE b_card SET is_sold=1 WHERE b_id=#{bid}")
    int updateById(int bid);
}
