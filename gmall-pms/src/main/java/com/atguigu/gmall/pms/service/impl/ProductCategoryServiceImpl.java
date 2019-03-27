package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.mapper.ProductCategoryMapper;
import com.atguigu.gmall.pms.pmsvo.PmsProductCategoryWithChildrenItem;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
@Slf4j
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public Map<String, Object> getPageList(Long parentId, Integer pageSize, Integer pageNum) {
        QueryWrapper<ProductCategory> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("parent_id",parentId);

        ProductCategoryMapper baseMapper = getBaseMapper();
        Page<ProductCategory> page = new Page<>(pageNum, pageSize);

        IPage<ProductCategory> brandIPage = baseMapper.selectPage(page, queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("pageSize", pageSize);
        result.put("totalPage", brandIPage.getPages());
        result.put("total", brandIPage.getTotal());
        result.put("pageNum", pageNum);
        result.put("list", brandIPage.getRecords());

        return result;

    }

    @Override
    public void batchUpdateNavStatus(List<Long> ids, Integer navStatus) {
        for (Long id : ids) {
            ProductCategory category = baseMapper.selectById(id);
            if(category!=null){
                category.setNavStatus(navStatus);
                baseMapper.updateById(category);
            }
        }

    }

    @Override
    public void batchUpdateshowStatus(List<Long> ids, Integer showStatus) {
        for (Long id : ids) {
            ProductCategory category = baseMapper.selectById(id);
            if(category!=null){
                category.setShowStatus(showStatus);
                baseMapper.updateById(category);
            }
        }
    }

    @Override
    public List<PmsProductCategoryWithChildrenItem> getProductCategoryWithChild() {
        ValueOperations<String, String> op = redisTemplate.opsForValue();

        //判断缓存中是否有数据
        String catchString = op.get(RedisCacheConstant.PRODUCT_CATEGORY_CACHE_KEY);
        if(!StringUtils.isEmpty(catchString)){
            log.debug("缓存命中，将缓存数据返回");
            List<PmsProductCategoryWithChildrenItem> items = JSON.parseArray(catchString, PmsProductCategoryWithChildrenItem.class);
            return items;
        }

        log.debug("未命中缓存，去数据库查");
        ProductCategoryMapper baseMapper = getBaseMapper();
        List<PmsProductCategoryWithChildrenItem> list =  baseMapper.getProductCategoryWithChild("0");

        //加入redis缓存中3天
        String jsonString = JSON.toJSONString(list);
        op.set(RedisCacheConstant.PRODUCT_CATEGORY_CACHE_KEY,jsonString,3, TimeUnit.DAYS);


        return list;
    }


}
