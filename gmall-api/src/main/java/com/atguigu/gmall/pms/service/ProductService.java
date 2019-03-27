package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.pmsvo.PmsProductParam;
import com.atguigu.gmall.pms.pmsvo.PmsProductQueryParam;
import com.atguigu.gmall.pms.pmsvo.PmsProductResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductService extends IService<Product> {

     Map<String,Object> getProductList(Integer pageSize, Integer pageNum);

    PmsProductResult getUpdateInfo(Long id);

    Map<String,Object> getPageProductList(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum);


    void creat(PmsProductParam productParam);

    void updateProductInfo(Long id, PmsProductQueryParam productParam);

    List<Product> getSimpleList(String keyword);

    void batchUpdatePublishStatus(List<Long> ids, Integer publishStatus);

    void batchUpdaterecommendStatus(List<Long> ids,Integer recommendStatus);

    void batchUpdateNewStatus(List<Long> ids, Integer newStatus);

    void batchUpdatedeleteStatus(List<Long> ids, Integer deleteStatus);
}
