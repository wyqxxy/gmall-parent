package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.ProductLadder;
import com.atguigu.gmall.pms.mapper.ProductLadderMapper;
import com.atguigu.gmall.pms.service.ProductLadderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品阶梯价格表(只针对同商品) 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@com.alibaba.dubbo.config.annotation.Service
@Component
public class ProductLadderServiceImpl extends ServiceImpl<ProductLadderMapper, ProductLadder> implements ProductLadderService {

}
