package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.atguigu.gmall.pms.mapper.ProductAttributeCategoryMapper;
import com.atguigu.gmall.pms.mapper.ProductMapper;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.atguigu.gmall.pms.service.ProductAttributeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 产品属性分类表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeCategoryServiceImpl extends ServiceImpl<ProductAttributeCategoryMapper, ProductAttributeCategory> implements ProductAttributeCategoryService {


    @Override
    public Boolean saveproductAttributeCategory(String name) {
        ProductAttributeCategoryMapper baseMapper = getBaseMapper();
        ProductAttributeCategory category = new ProductAttributeCategory();
        category.setName(name);
        int insert = baseMapper.insert(category);
        return insert>0;
    }

    @Override
    public Map<String,Object> getPageList(Integer pageSize, Integer pageNum) {
        ProductAttributeCategoryMapper baseMapper = getBaseMapper();
        Page<ProductAttributeCategory> page = new Page<>(pageNum, pageSize);

        baseMapper.selectPage(page, null);

        Map<String, Object> result = new HashMap<>();
        result.put("pageSize", pageSize);
        result.put("totalPage", page.getPages());
        result.put("total", page.getTotal());
        result.put("pageNum", pageNum);
        result.put("list", page.getRecords());
        return result;
    }

    @Override
    public List<ProductAttributeCategory> getAllWithAttr() {
        ProductAttributeCategoryMapper baseMapper = getBaseMapper();
        List<ProductAttributeCategory> productAttributeCategories = baseMapper.selectList(null);
        return productAttributeCategories;
    }
}
