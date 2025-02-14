package com.jesper.mapper;

import com.jesper.model.Shop;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface ShopMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_shop
     *
     * @mbggenerated Fri Mar 26 08:52:34 CST 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_shop
     *
     * @mbggenerated Fri Mar 26 08:52:34 CST 2021
     */
    int insert(Shop record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_shop
     *
     * @mbggenerated Fri Mar 26 08:52:34 CST 2021
     */
    Shop selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_shop
     *
     * @mbggenerated Fri Mar 26 08:52:34 CST 2021
     */
    List<Shop> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_shop
     *
     * @mbggenerated Fri Mar 26 08:52:34 CST 2021
     */
    int updateByPrimaryKey(Shop record);
    
    String selectByIds(String ids);

	int count(Shop shop);

	List<Shop> list(Shop shop);
}