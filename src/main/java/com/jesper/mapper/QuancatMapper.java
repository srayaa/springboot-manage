package com.jesper.mapper;

import com.jesper.model.ItemCategory;
import com.jesper.model.Quancat;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuancatMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_quancat
     *
     * @mbggenerated Tue Mar 23 16:40:00 CST 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_quancat
     *
     * @mbggenerated Tue Mar 23 16:40:00 CST 2021
     */
    int insert(Quancat record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_quancat
     *
     * @mbggenerated Tue Mar 23 16:40:00 CST 2021
     */
    Quancat selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_quancat
     *
     * @mbggenerated Tue Mar 23 16:40:00 CST 2021
     */
    List<Quancat> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_quancat
     *
     * @mbggenerated Tue Mar 23 16:40:00 CST 2021
     */
    int updateByPrimaryKey(Quancat record);

	List<Quancat> list(Quancat quancat);
}