package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.pmsvo.PmsProductCategoryWithChildrenItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 产品分类 Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    List<PmsProductCategoryWithChildrenItem> getProductCategoryWithChild(String parentId);

    void updateProductCountById(Long categoryId);
}
