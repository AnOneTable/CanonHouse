package com.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author ZhaoHang
 * @since 2020-03-15
 */
@EqualsAndHashCode(callSuper = true)
@TableName("t_shop")
@Data
public class TShop extends Model<TShop> {

    private static final long serialVersionUID=1L;

      /**
     * 店铺id
     */
      @TableId(value = "SHOP_ID", type = IdType.AUTO)
      private Integer shopId;

      /**
     * 店铺名称
     */
      @TableField("SHOP_NAME")
    private String shopName;

      /**
     * 店主姓名
     */
      @TableField("SHOP_USER_NAME")
    private String shopUserName;

      /**
     * 店铺地址
     */
      @TableField("ADDRESS")
    private String address;

      /**
     * 手机号
     */
      @TableField("IPHONE")
    private String iphone;

    @TableField("TAG_1")
    private String tag1;

    @TableField("TAG_2")
    private String tag2;

    @TableField("TAG_3")
    private String tag3;

    @TableField("TAG_4")
    private String tag4;

      /**
     * 创建时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      @TableField("CREATE_DATA")
    private LocalDateTime createData;

      /**
     * 修改时间
     */
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
      @TableField("UPDATE_DATA")
    private LocalDateTime updateData;

      /**
     * 版本
     */
      @TableField("VERSION")
    private Integer version;



    @Override
    protected Serializable pkVal() {
          return this.shopId;
      }


}
