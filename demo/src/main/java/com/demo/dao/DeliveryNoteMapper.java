package com.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.entity.DeliveryNote;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhaoHang
 * @since 2020-03-14
 */
public interface DeliveryNoteMapper extends BaseMapper<DeliveryNote> {
    IPage<DeliveryNote> selectDeliveryNoteList();

    List<DeliveryNote> queryAllDeliveryNoteInfos();

    int insertDeliveryNote(DeliveryNote deliveryNote);

    int deleteDeliveryNote(int id);

    int updateDeliveryNote(int id,DeliveryNote deliveryNote);

    List<DeliveryNote> selectDeliveryNote(Integer shopId);
}
