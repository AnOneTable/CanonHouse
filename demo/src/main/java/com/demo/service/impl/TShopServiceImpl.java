package com.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.dao.TShopMapper;
import com.demo.entity.TShop;
import com.demo.service.TShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhaoHang
 * @since 2020-03-15
 */
@Service
public class TShopServiceImpl extends ServiceImpl<TShopMapper, TShop> implements TShopService {


    @Autowired
    private TShopMapper tShopMapper;
    @Override
    public List<TShop> queryAllShopInfos() {
        return tShopMapper.queryAllShopInfos();
    }
}
