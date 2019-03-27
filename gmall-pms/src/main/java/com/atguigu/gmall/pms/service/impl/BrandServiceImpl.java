package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.mapper.BrandMapper;
import com.atguigu.gmall.pms.mapper.ProductMapper;
import com.atguigu.gmall.pms.service.BrandService;
import com.atguigu.gmall.utils.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Override
    public Map<String, Object> getPageList(String keyword, Integer pageNum, Integer pageSize) {
        BrandMapper baseMapper = getBaseMapper();
        Page<Brand> page = new Page<>(pageNum, pageSize);

        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(keyword)){
            queryWrapper.like("name",keyword).or().eq("first_letter",keyword);
        }
        IPage<Brand> brandIPage = baseMapper.selectPage(page, queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("pageSize", pageSize);
        result.put("totalPage", brandIPage.getPages());
        result.put("total", brandIPage.getTotal());
        result.put("pageNum", pageNum);
        result.put("list", brandIPage.getRecords());
        return result;
    }
    @Transactional
    @Override
    public Boolean batchUpdateStatus(List<Long> ids, Integer showStatus) {
        BrandMapper baseMapper = getBaseMapper();
        for (Long id : ids) {
            Brand brand = baseMapper.selectById(id);
            if(brand!=null){
                brand.setShowStatus(showStatus);
                baseMapper.updateById(brand);
            }else {
                throw new GuliException(500, "此品牌不存在");
            }
        }
        return true;
    }

    @Override
    public Boolean batchUpdatefactoryStatus(List<Long> ids, Integer factoryStatus) {
        BrandMapper baseMapper = getBaseMapper();
        for (Long id : ids) {

            Brand brand = baseMapper.selectById(id);

            if(brand!=null){
                brand.setFactoryStatus(factoryStatus);

                baseMapper.updateById(brand);
            }else {
                throw new GuliException(500, "此品牌不存在");
            }
        }
        return true;
    }
}
