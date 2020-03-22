package com.demo.utils;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.session.RowBounds;

import java.io.Serializable;
import java.util.List;

/**
 * @author ZhaoHang
 */
public class PageInfo implements Serializable {

    private static final long serialVersionUID = 6390553004255803663L;

    public final static Integer DEFAULT_PAGE_ON = 1;

    public final static Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 当前页码
     */
    private Integer pageOn;

    /**
     * 每页显示数量
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 总记录数
     */
    private Integer total;

    /**
     * 数据
     */
    private List<?> rows;

    /**
     * 表尾行
     */
    private List<?> footer;

    public Integer getPageOn() {
        return pageOn;
    }

    public void setPageOn(Integer pageOn) {
        this.pageOn = pageOn;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        int t = total.intValue();
        this.total = t;
        this.totalPage = (t + this.pageSize - 1) / this.pageSize;
    }

    public void setDataToPageInfo(IPage<?> page){
        this.setTotal(page.getTotal());
        this.setRows(page.getRecords());
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public PageInfo() {
        this.pageOn = DEFAULT_PAGE_ON;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }


    public PageInfo(Integer pageOn) {
        this.pageOn = pageOn;
    }

    public PageInfo(Integer pageOn, Integer pageSize) {
        if (pageOn == null) {
            pageOn = PageInfo.DEFAULT_PAGE_ON;
        }
        if (pageSize == null) {
            pageSize = PageInfo.DEFAULT_PAGE_SIZE;
        }
        this.pageOn = pageOn;
        this.pageSize = pageSize;
    }

    /**
     * @return
     */
    @JSONField(serialize = false)
    public RowBounds getPageInfo() {
        int offset = (this.pageOn - 1) * this.pageSize;
        if (offset < 0) {
            offset = 0;
        }
        return new RowBounds(offset, this.pageSize);
    }

    public List<?> getFooter() {
        return footer;
    }

    public void setFooter(List<?> footer) {
        this.footer = footer;
    }

    @Override
    public String toString() {
        return "PageInfo [pageOn=" + pageOn + ", pageSize=" + pageSize
                + ", totalPage=" + totalPage + ", total=" + total + ", rows="
                + rows + ",footer=" + footer + "]";
    }


}
