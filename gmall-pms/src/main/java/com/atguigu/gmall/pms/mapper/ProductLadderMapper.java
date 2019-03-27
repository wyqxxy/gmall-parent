package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.ProductLadder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 产品阶梯价格表(只针对同商品) Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductLadderMapper extends BaseMapper<ProductLadder> {

    /**
     * @param id
     * @return
     */
    List<ProductLadder> getListByProductId(Long id);
}
