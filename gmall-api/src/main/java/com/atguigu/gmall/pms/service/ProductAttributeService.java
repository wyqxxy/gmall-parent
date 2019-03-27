package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.entity.ProductCategoryAttributeRelation;
import com.atguigu.gmall.pms.pmsvo.ProductAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品属性参数表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductAttributeService extends IService<ProductAttribute> {

    List<ProductAttribute> getListByProductAttributeCategoryId(Long productAttributeCategoryId);

    List<ProductAttrInfo> getListByProductCategoryId(Long productCategoryId);

    Map<String,Object> getListByType(Long cid, Integer type, Integer pageSize, Integer pageNum);
}
