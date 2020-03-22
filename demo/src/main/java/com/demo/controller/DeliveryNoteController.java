package com.demo.controller;

import com.demo.entity.DeliveryNote;
import com.demo.entity.TShop;
import com.demo.service.DeliveryNoteService;
import com.demo.service.TShopService;
import com.demo.utils.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/deliveryNote")
public class DeliveryNoteController {

    @Autowired
    private DeliveryNoteService deliveryNoteService;
    @Autowired
    private TShopService tShopService;

    @GetMapping(value = "/query/all")
    List<DeliveryNote> queryAllDeliveryNoteInfos(){
        List<DeliveryNote> list = new ArrayList<>();
        list = deliveryNoteService.queryAllDeliveryNoteInfos();
        return list;
    }
    @GetMapping(value = "/query/all/shop")
    List<TShop> queryAllShopInfos(){
        List<TShop> list = new ArrayList<>();
        list = tShopService.queryAllShopInfos();
        return list;
    }
    @PostMapping(value = "/insert/deliveryNote")
    int insertDeliveryNote(DeliveryNote deliveryNote){
        int result = 0;

        deliveryNote.setShopId(3);
        BigDecimal price = deliveryNote.getPrice();
        BigDecimal quantity = deliveryNote.getQuantity();
        deliveryNote.setTotalPrice(price.multiply(quantity));
        deliveryNote.setDeliveryTime(LocalDateTime.now());
        result = deliveryNoteService.insertDeliveryNote(deliveryNote);
        return result;
    }

 /*   @PostMapping(value = "/delete/id")
    public int delete(Integer id) {
        int result = 0;
        result= deliveryNoteService.deleteDeliveryNote(id);
        return result;

    }*/

    @GetMapping(value = "/get/shop_id")
    public List<DeliveryNote> get(Integer shop_id) {
        List<DeliveryNote> list = new ArrayList<>();
        list = deliveryNoteService.selectDeliveryNote(shop_id);
        return list;
    }

    @PostMapping(value = "/update/deliveryNote")
    int updateDeliveryNote(Integer id,DeliveryNote deliveryNote){
        int result = 0;
        BigDecimal price = deliveryNote.getPrice();
        BigDecimal quantity = deliveryNote.getQuantity();
        deliveryNote.setTotalPrice(price.multiply(quantity));
        deliveryNote.setDeliveryTime(LocalDateTime.now());
        result = deliveryNoteService.updateDeliveryNote(id,deliveryNote);
        return result;
    }

    @PutMapping(value = "/add")
    public String add(DeliveryNote deliveryNote) {
        var br = new BaseResult();
        deliveryNote.setShopId(3);
        BigDecimal price = deliveryNote.getPrice();
        BigDecimal quantity = deliveryNote.getQuantity();
        deliveryNote.setTotalPrice(price.multiply(quantity));
        deliveryNote.setDeliveryTime(LocalDateTime.now());
        deliveryNote.insert();
        br.success();
        return br.toJSONString();
    }


    @PutMapping(value = "/add/shop")
    public String add(TShop shop) {
        var br = new BaseResult();
        shop.setShopName(shop.getShopName());
        shop.setCreateData(LocalDateTime.now());
        shop.insert();
        br.success();
        return br.toJSONString();
    }

    @PostMapping(value = "/update")
    public String update(DeliveryNote deliveryNote) {
        var br = new BaseResult();

        var user = tShopService.getById(deliveryNote);
        if (user != null) {

            deliveryNote.setShopName(deliveryNote.getShopName());
            deliveryNote.setCategory(deliveryNote.getCategory());
            deliveryNote.updateById();
            br.success();
        } else {
            log.error("查询的数据不存在，id= {}", deliveryNote.getId());
            br.setErrorMsg("数据不存在");
            br.setErrorCode("10000");
        }
        return br.toJSONString();
    }

    @DeleteMapping(value = "/delete/{id}")
    public String delete(@PathVariable Integer id) {
        var br = new BaseResult();
        deliveryNoteService.removeById(id);
        br.success();
        return br.toJSONString();
    }
}
