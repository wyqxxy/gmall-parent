package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.pmsvo.ProductAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 商品属性参数表 Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Component
public interface ProductAttributeMapper extends BaseMapper<ProductAttribute> {



    List<ProductAttrInfo> getListByProductCategoryId(@Param("productCategoryId") Long productCategoryId);
            ;
}
