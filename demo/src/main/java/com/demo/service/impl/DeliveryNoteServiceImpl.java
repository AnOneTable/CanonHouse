package com.demo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.dao.DeliveryNoteMapper;
import com.demo.entity.DeliveryNote;
import com.demo.service.DeliveryNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhaoHang
 * @since 2020-03-14
 */
@Service
public class DeliveryNoteServiceImpl extends ServiceImpl<DeliveryNoteMapper, DeliveryNote> implements DeliveryNoteService {
    @Autowired
    private DeliveryNoteMapper deliveryNoteMapper;
    @Override
    public IPage<DeliveryNote> selectUserInfoList() {
        return deliveryNoteMapper.selectDeliveryNoteList();
    }

    @Override
    public List<DeliveryNote> queryAllDeliveryNoteInfos() {
        return deliveryNoteMapper.queryAllDeliveryNoteInfos();
    }

    @Override
    public int insertDeliveryNote(DeliveryNote deliveryNote) {
        return deliveryNoteMapper.insertDeliveryNote(deliveryNote);
    }

    @Override
    public int deleteDeliveryNote(int id) {
        return deliveryNoteMapper.deleteDeliveryNote(id);
    }

    @Override
    public int updateDeliveryNote(int id, DeliveryNote deliveryNote) {
        return deliveryNoteMapper.updateDeliveryNote(id,deliveryNote);
    }

    @Override
    public List<DeliveryNote> selectDeliveryNote(Integer shopId) {
        return deliveryNoteMapper.selectDeliveryNote(shopId);
    }
}
