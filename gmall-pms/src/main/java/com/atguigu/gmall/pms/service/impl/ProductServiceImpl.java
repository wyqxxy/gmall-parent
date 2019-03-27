package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.*;
import com.atguigu.gmall.pms.pmsvo.PmsProductParam;
import com.atguigu.gmall.pms.pmsvo.PmsProductQueryParam;
import com.atguigu.gmall.pms.pmsvo.PmsProductResult;
import com.atguigu.gmall.pms.service.MemberPriceService;
import com.atguigu.gmall.pms.service.ProductLadderService;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.atguigu.gmall.search.service.GmallSearchService;
import com.atguigu.gmall.to.es.EsProduct;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.atguigu.gmall.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {


    @Autowired
    ProductMapper productMapper;
    @Autowired
    SkuStockMapper skuStockMapper;
    @Autowired
    ProductLadderMapper productLadderMapper;
    @Autowired
    MemberPriceMapper memberPriceMapper;
    @Autowired
    ProductFullReductionMapper productFullReductionMapper;
    @Autowired
    ProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    ProductCategoryMapper productCategoryMapper;
    @Reference
    GmallSearchService gmallSearchService;

   ThreadLocal<Product> threadLocal = new ThreadLocal<>();

    @Override
    public Map<String, Object> getProductList(Integer pageSize, Integer pageNum) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        ProductMapper baseMapper = getBaseMapper();
        baseMapper.selectPage(page, null);

        Map<String, Object> result = new HashMap<>();
        result.put("pageSize", pageSize);
        result.put("totalPage", page.getPages());
        result.put("total", page.getTotal());
        result.put("pageNum", pageNum);
        result.put("list", page.getRecords());
        return result;
    }
    @Transactional
    @Override
    public void updateProductInfo(Long id, PmsProductQueryParam productParam) {
        //更新商品信息
        Product product = new Product();
        product.setId(id);
        BeanUtils.copyProperties(productParam,product);
        productMapper.updateById(product);

    }

    @Override
    public List<Product> getSimpleList(String keyword) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name",keyword).or().like("product_sn",keyword);
        List<Product> products = baseMapper.selectList(queryWrapper);
        return products;
    }

    @Override
    public void batchUpdatePublishStatus(List<Long> ids, Integer publishStatus) {
        //上架
        if(publishStatus==1){
            publishProduct(ids);
        }else {
            removeProduct(ids);
        }
    }
    //下架
    private void removeProduct(List<Long> ids) {
    }
    //上架
    private void publishProduct(List<Long> ids) {
        //1.查询当前要上架的spu和sku信息
        ids.forEach(productId -> {
            //1》查询spu信息
            Product product = productMapper.selectById(productId);
            //2》.查询sku信息
            List<SkuStock> skuStocks = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id", productId));
            //3》.查询商品所有属性的值
            List<EsProductAttributeValue> list = productAttributeValueMapper.getProductAttrVal(productId);

            //4>遍历分装sku信息
            //记录存入es中sku的数目，判断是否完全保存成功
            AtomicReference<Integer> count = new AtomicReference<>(0);
            skuStocks.forEach(skuStock -> {
                //4.1》分装到EsProduct中
                EsProduct esProduct = new EsProduct();
                BeanUtils.copyProperties(product,esProduct);
                //4.2>修改sku信息
                esProduct.setPrice(skuStock.getPrice());
                esProduct.setStock(skuStock.getStock());
                esProduct.setSale(skuStock.getSale());
                //4,3>改写商品的标题
                esProduct.setName(product.getName()+" "+skuStock.getSp1()+" "+
                        skuStock.getSp2()+" "+skuStock.getSp3()
                );
                //4.4>分装商品属性
                esProduct.setAttrValueList(list);

                //5.将sku发布到es中
                Boolean b = gmallSearchService.pulishSku2Es(esProduct);
                if(b){
                    count.set(count.get()+1);
                }
            });
            //6.sku信息全部保存到es以后，修改数据库product的状态
            if(count.get()==skuStocks.size()){
                for (Long id : ids) {
                  Product update = new Product();
                  update.setId(productId);
                  update.setPublishStatus(1);
                  productMapper.updateById(update);
                }
            }else {
                //TODO删除保存在es中的sku信息
            }

        });
    }

    @Override
    public void batchUpdaterecommendStatus(List<Long> ids,Integer recommendStatus) {
        for (Long id : ids) {
            Product product = baseMapper.selectById(id);
            product.setRecommandStatus(recommendStatus);
            baseMapper.updateById(product);
        }
    }

    @Override
    public void batchUpdateNewStatus(List<Long> ids, Integer newStatus) {
        for (Long id : ids) {
            Product product = baseMapper.selectById(id);
            product.setNewStatus(newStatus);
            baseMapper.updateById(product);
        }
    }

    @Override
    public void batchUpdatedeleteStatus(List<Long> ids, Integer deleteStatus) {
        for (Long id : ids) {
            Product product = baseMapper.selectById(id);
            product.setDeleteStatus(deleteStatus);
            baseMapper.updateById(product);
        }
    }


    @Override
    public PmsProductResult getUpdateInfo(Long id) {
        PmsProductResult pmsProductResult = new PmsProductResult();
        //1.商品基本信息
        Product product = productMapper.selectById(id);
        BeanUtils.copyProperties(product,pmsProductResult);

        //2.商品所选分类的父id  Long cateParentId;
        Long productCategoryId = product.getProductCategoryId();
        ProductCategory productCategory = productCategoryMapper.selectById(productCategoryId);
        Long parentId = productCategory.getParentId();
        pmsProductResult.setCateParentId(parentId);

        //3.@ApiModelProperty("商品阶梯价格设置")List<ProductLadder> productLadderList
        List<ProductLadder> productLadderList = productLadderMapper.getListByProductId(id);
        pmsProductResult.setProductLadderList(productLadderList);
        //4. "商品满减价格设置")List<ProductFullReduction> productFullReductionList;
        List<ProductFullReduction> productFullReductionList=productFullReductionMapper.getProductFullReductionListByProductId(id);
        pmsProductResult.setProductFullReductionList(productFullReductionList);

        //5.商品会员价格设置") List<MemberPrice> memberPriceList;
        List<MemberPrice> memberPriceList = memberPriceMapper.selectMemberPriceList(id);
        pmsProductResult.setMemberPriceList(memberPriceList);
        //6.("商品的sku库存信息")List<SkuStock> skuStockList;
        List<SkuStock> skuStockList = skuStockMapper.selectSkuStockByProductId(id);
        pmsProductResult.setSkuStockList(skuStockList);

        //7. @ApiModelProperty("商品参数及自定义规格属性")List<ProductAttributeValue> productAttributeValueList;
        List<ProductAttributeValue> productAttributeValueList = productAttributeValueMapper.selectAttriValueListByProductId(id);
        pmsProductResult.setProductAttributeValueList(productAttributeValueList);


        /**
        TODO
         @ApiModelProperty("专题和商品关系")
         private List<SubjectProductRelation> subjectProductRelationList;
         @ApiModelProperty("优选专区和商品的关系")
         private List<PrefrenceAreaProductRelation> prefrenceAreaProductRelationList;

         */
    return pmsProductResult;
    }

    @Override
    public Map<String, Object> getPageProductList(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum) {
        Page<Product> page = new Page<>(pageNum,pageSize);
        ProductMapper baseMapper = getBaseMapper();
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();

        if(productQueryParam!=null){
            Long brandId = productQueryParam.getBrandId();
            String keyword = productQueryParam.getKeyword();
            Long productCategoryId = productQueryParam.getProductCategoryId();
            String productSn = productQueryParam.getProductSn();
            Integer publishStatus = productQueryParam.getPublishStatus();
            Integer verifyStatus = productQueryParam.getVerifyStatus();

            if(!StringUtils.isEmpty(brandId)){
                queryWrapper.eq("brand_id",brandId);
            }
            if(!StringUtils.isEmpty(keyword)){
                queryWrapper.like("name",keyword).or()
                        .like("sub_title",keyword);
            }
            if(!StringUtils.isEmpty(productCategoryId)){
                queryWrapper.eq("product_category_id",productCategoryId);
            }
            if(!StringUtils.isEmpty(productSn)){
                queryWrapper.eq("product_sn",productSn);
            }
            if(!StringUtils.isEmpty(publishStatus)){
                queryWrapper.eq("publish_status",publishStatus);
            }
            if(!StringUtils.isEmpty(verifyStatus)){
                queryWrapper.eq("verify_status",verifyStatus);
            }
            System.out.println("商品publish_status："+publishStatus);
        }
        IPage<Product> page1 = baseMapper.selectPage(page, queryWrapper);

        Map<String, Object> map = PageUtils.getPageMap(page1);
        return map;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void creat(PmsProductParam productParam) {
        //生成自己的代理对象
        ProductServiceImpl psProxy = (ProductServiceImpl)AopContext.currentProxy();
        //1》保存商品基本信息
        psProxy.saveProductInfo(productParam);

        //2》.商品阶梯价格设置productLadderList
        psProxy.saveProductLadderList(productParam.getProductLadderList());

        //3》.商品满减价格设置")
        psProxy.saveProductFullReduction(productParam.getProductFullReductionList());
        //4》.商品会员价格设置")
        psProxy.saveMemberPriceList(productParam.getMemberPriceList());

        // 6>.商品参数及自定义规格属性")
        psProxy.saveProductAttributeValue(productParam.getProductAttributeValueList());
       //7、更新商品分类数目
        psProxy.updateCategoryProductCount();
    }

    //保存商品基本信息（必须成功的信息）
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductInfo(PmsProductParam productParam){
        ProductServiceImpl psProxy = (ProductServiceImpl) AopContext.currentProxy();
        //1.1保存商品基本信息
        psProxy.saveProduct(productParam);
        //1.2.保存商品sku信息
        psProxy.saveSkuInfo(productParam.getSkuStockList());
    }
    //7、更新商品分类数目
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCategoryProductCount() {
        Product product = threadLocal.get();
        Long categoryId = product.getProductCategoryId();
        productCategoryMapper.updateProductCountById(categoryId);
    }

    // 6>.商品参数及自定义规格属性")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductAttributeValue(List<ProductAttributeValue> productAttributeValueList) {
        Product product = threadLocal.get();
        productAttributeValueList.forEach(productAttributeValue -> {
            productAttributeValue.setProductId(product.getId());
            productAttributeValueMapper.insert(productAttributeValue);
        });

    }

    //4》.商品会员价格设置")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMemberPriceList(List<MemberPrice> memberPriceList) {
        Product product = threadLocal.get();
        memberPriceList.forEach(memberPrice -> {
            memberPrice.setProductId(product.getId());
            memberPriceMapper.insert(memberPrice);
        });
    }

    //3》.商品满减价格设置")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductFullReduction(List<ProductFullReduction> productFullReductionList) {
        Product product = threadLocal.get();
        productFullReductionList.forEach(productFullReduction -> {
            productFullReduction.setProductId(product.getId());
            productFullReductionMapper.insert(productFullReduction);
        });
    }

    //2.商品阶梯价格设置productLadderList
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductLadderList(List<ProductLadder> productLadderList) {
        Product product = threadLocal.get();
        productLadderList.forEach(productLadder -> {
            productLadder.setProductId(product.getId());
            productLadderMapper.insert(productLadder);
        });
    }

    //1.2.保存商品sku信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSkuInfo(List<SkuStock> skuStockList) {
        Product product = threadLocal.get();
        //lambda中遍历为了线程安全不可以是用int i=0； i++，用一下来代替
        AtomicReference<Integer> i = new AtomicReference<>(0);
        //格式化俩位数子
        NumberFormat numberInstance = DecimalFormat.getNumberInstance();
        numberInstance.setMaximumIntegerDigits(2);
        numberInstance.setMinimumIntegerDigits(2);

        skuStockList.forEach(skuStock -> {
            skuStock.setProductId(product.getId());
            String format = numberInstance.format(i.get());
            String code = "K:"+product.getId()+"_"+format;
            skuStock.setSkuCode(code);
            i.set(i.get()+1);
            skuStockMapper.insert(skuStock);
        });
    }
    //1.1保存商品基本信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveProduct(PmsProductParam productParam) {
        Product product = new Product();
        BeanUtils.copyProperties(productParam,product);
        productMapper.insert(product);
        //将商品信息存放到共享
        threadLocal.set(product);
    }


}
