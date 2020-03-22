package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.entity.TShop;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZhaoHang
 * @since 2020-03-15
 */
public interface TShopService extends IService<TShop> {
    List<TShop> queryAllShopInfos();
}
