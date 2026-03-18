package com.saki.crtrs.mapper;

import com.saki.crtrs.dto.User;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE u_count = #{uCount}")
    User findByUserCount(String uCount);

    @Insert("INSERT INTO user(u_count, u_password, u_pho) VALUES (#{uCount}, #{uPassword}, #{uPho})")
    void register(String uCount, String uPassword, String uPho);

//    根据书籍id获取所属用户账户与头像
    @Select("SELECT u_count, avatar FROM user,b_card WHERE b_card.b_id=#{bId} AND b_card.uid=user.uid")
    User getUserInfoByBid(Integer bId);

//    根据uid查询用户信息
    @Select("SELECT * FROM user WHERE uid=#{uid}")
    User getUserInfoByUid(BigInteger uid);

//    根据uid修改用户信息
    @Update("UPDATE user SET avatar=#{avatarImgUrl}, u_pho=#{phone}, u_password=#{password} WHERE uid=#{uid}")
    void updateUserInfo(BigInteger uid, String avatarImgUrl, String phone, String password);

    /**
     * 原子性扣减余额
     * 这里使用了 BigDecimal 运算，注意避免空指针
     */
    @Update("UPDATE user SET balance = balance - #{price} WHERE uid = #{uid} AND balance >= #{price};")
    int decreaseBalance(@Param("uid") BigInteger uid, @Param("price") BigDecimal price);

    /**
     * 原子性增加卖家余额
     */
    @Update("UPDATE user SET balance = balance + #{price} WHERE uid = #{uid}")
    int increaseBalance(@Param("uid") BigInteger uid, @Param("price") BigDecimal price);
}
