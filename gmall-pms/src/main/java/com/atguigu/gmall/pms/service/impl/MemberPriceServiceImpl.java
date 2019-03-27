package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.MemberPrice;
import com.atguigu.gmall.pms.mapper.MemberPriceMapper;
import com.atguigu.gmall.pms.service.MemberPriceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品会员价格表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@com.alibaba.dubbo.config.annotation.Service
@Component
public class MemberPriceServiceImpl extends ServiceImpl<MemberPriceMapper, MemberPrice> implements MemberPriceService {

}
