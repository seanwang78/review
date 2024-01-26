package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class GoodsDetail implements Serializable {
    private static final long serialVersionUID = 824069447L;

    /**
     *
     */
    private String body;

    private String categoriesTree;

    private String goodsCategory;

    private String goodsId;

    private String goodsName;
    /**
     * 商品单价
     */
    private ExternalMoney price;

    private BigDecimal quantity;

    private String showUrl;

    public String getBody() {
        return this.body;
    }

    public String getCategoriesTree() {
        return this.categoriesTree;
    }

    public String getGoodsCategory() {
        return this.goodsCategory;
    }

    public String getGoodsId() {
        return this.goodsId;
    }

    public String getGoodsName() {
        return this.goodsName;
    }

    public ExternalMoney getPrice() {
        return this.price;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public String getShowUrl() {
        return this.showUrl;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCategoriesTree(String categoriesTree) {
        this.categoriesTree = categoriesTree;
    }

    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public void setPrice(ExternalMoney price) {
        this.price = price;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setShowUrl(String showUrl) {
        this.showUrl = showUrl;
    }

    public GoodsDetail(String body, String categoriesTree, String goodsCategory, String goodsId, String goodsName,
        ExternalMoney price, BigDecimal quantity, String showUrl) {
        this.body = body;
        this.categoriesTree = categoriesTree;
        this.goodsCategory = goodsCategory;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.price = price;
        this.quantity = quantity;
        this.showUrl = showUrl;
    }

    public GoodsDetail() {}

    @Override
    public String toString() {
        return "GoodsDetail(body=" + this.body + ", categoriesTree=" + this.categoriesTree + ", goodsCategory="
            + this.goodsCategory + ", goodsId=" + this.goodsId + ", goodsName=" + this.goodsName + ", price="
            + this.price + ", quantity=" + this.quantity + ", showUrl=" + this.showUrl + ")";
    }

}
