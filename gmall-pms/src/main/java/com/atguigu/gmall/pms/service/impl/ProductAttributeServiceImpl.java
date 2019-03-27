package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.entity.ProductCategoryAttributeRelation;
import com.atguigu.gmall.pms.mapper.ProductAttributeMapper;
import com.atguigu.gmall.pms.mapper.ProductMapper;
import com.atguigu.gmall.pms.pmsvo.ProductAttrInfo;
import com.atguigu.gmall.pms.service.ProductAttributeService;
import com.atguigu.gmall.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 商品属性参数表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeServiceImpl extends ServiceImpl<ProductAttributeMapper, ProductAttribute> implements ProductAttributeService {
    @Autowired
    ProductAttributeMapper productAttributeMapper;
    @Override
    public List<ProductAttribute> getListByProductAttributeCategoryId(Long productAttributeCategoryId) {
        QueryWrapper<ProductAttribute> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_attribute_category_id",productAttributeCategoryId);
        List<ProductAttribute> list = baseMapper.selectList(queryWrapper);
        return list;
    }



    @Override
    public List<ProductAttrInfo> getListByProductCategoryId(Long productCategoryId) {
        List<ProductAttrInfo> list = productAttributeMapper.getListByProductCategoryId(productCategoryId);
        return list;
    }

    @Override
    public Map<String, Object> getListByType(Long cid, Integer type, Integer pageSize, Integer pageNum) {
        ProductAttributeMapper baseMapper = getBaseMapper();
        Page<ProductAttribute> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ProductAttribute> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",type).eq("product_attribute_category_id",cid);
        IPage<ProductAttribute> iPage = baseMapper.selectPage(page, queryWrapper);
        
        //分装数据
        Map<String, Object> result = PageUtils.getPageMap(iPage);

       return  result;
    }


}
