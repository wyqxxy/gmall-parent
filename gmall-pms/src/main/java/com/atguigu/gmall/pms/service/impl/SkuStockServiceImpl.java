package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.mapper.SkuStockMapper;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * sku的库存 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class SkuStockServiceImpl extends ServiceImpl<SkuStockMapper, SkuStock> implements SkuStockService {

    @Autowired
    SkuStockMapper skuStockMapper;
    @Override
    public List<SkuStock> getListLikeKeyword(Long pid, String keyword) {
        QueryWrapper<SkuStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("sku_code",keyword);
        List<SkuStock> skuStocks = skuStockMapper.selectList(queryWrapper);
        return skuStocks;
    }

    @Override
    public void batchUpdateSkuStock(Long pid, List<SkuStock> skuStockList) {
        for (SkuStock skuStock : skuStockList) {
            skuStock.setProductId(pid);
            skuStockMapper.updateById(skuStock);
        }
    }
}
