package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Brand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface BrandService extends IService<Brand> {

    Map<String,Object> getPageList(String keyword, Integer pageNum, Integer pageSize);

    Boolean batchUpdateStatus(List<Long> ids, Integer showStatus);

    Boolean batchUpdatefactoryStatus(List<Long> ids, Integer factoryStatus);
}
