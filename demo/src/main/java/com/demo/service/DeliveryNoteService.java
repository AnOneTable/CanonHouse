package com.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.entity.DeliveryNote;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZhaoHang
 * @since 2020-03-14
 */
public interface DeliveryNoteService extends IService<DeliveryNote> {
    IPage<DeliveryNote> selectUserInfoList();

    List<DeliveryNote> queryAllDeliveryNoteInfos();



    int insertDeliveryNote(DeliveryNote deliveryNote);

    int deleteDeliveryNote(int id);

    int updateDeliveryNote(int id,DeliveryNote deliveryNote);

    List<DeliveryNote> selectDeliveryNote(Integer shopId);
}
