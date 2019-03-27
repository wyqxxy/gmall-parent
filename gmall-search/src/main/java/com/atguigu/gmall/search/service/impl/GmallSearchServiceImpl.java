package com.atguigu.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.constant.EsConstant;
import com.atguigu.gmall.search.service.GmallSearchService;
import com.atguigu.gmall.to.es.EsProduct;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Service
@Component
public class GmallSearchServiceImpl implements GmallSearchService{

    @Autowired
    JestClient jestClient;

    @Override
    public Boolean pulishSku2Es(EsProduct esProduct) {
        Index index = new Index.Builder(esProduct).index(EsConstant.ES_PRODUCT_INDEX)
                .type(EsConstant.ES_PRODUCT_TYPE).build();
        DocumentResult execute=null;
        try {
             execute = jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return execute.isSucceeded();
    }

    /**
     * GET gulishop/_search
     {
     "query": {
     "match_all": {}
     },
     "aggs": {
     "brandIdAgg": {
     "terms": {
     "field": "brandId",
     "size": 100
     },
     "aggs": {
     "brandNameAgg": {
     "terms": {
     "field": "brandName",
     "size": 100
     }
     }
     }
     },
     "productCategoryIdAgg":{
     "terms": {
     "field": "productCategoryId",
     "size": 100
     },
     "aggs": {
     "productCategoryNameAgg": {
     "terms": {
     "field": "productCategoryName",
     "size": 100
     }
     }
     }
     },
     "productAttrAgg":{
     "nested": {
     "path": "attrValueList"
     },
     "aggs": {
     "productAttributeIdAgg": {
     "filter": {
     "term": {
     "attrValueList.type": "1"
     }
     },
     "aggs": {
     "productAttrIdAgg":{
     "terms": {
     "field": "attrValueList.productAttributeId",
     "size": 10
     },
     "aggs": {
     "productAttrNameAgg": {
     "terms": {
     "field": "attrValueList.name",
     "size": 10
     },
     "aggs": {
     "productAttrValue": {
     "terms": {
     "field": "attrValueList.value",
     "size": 10
     }
     }
     }
     }
     }
     }

     }
     }
     }
     }
     }

     }
     */
}
