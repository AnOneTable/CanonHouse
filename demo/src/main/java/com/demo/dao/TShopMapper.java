package com.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.entity.TShop;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhaoHang
 * @since 2020-03-15
 */
public interface TShopMapper extends BaseMapper<TShop> {
    List<TShop> queryAllShopInfos();
}
