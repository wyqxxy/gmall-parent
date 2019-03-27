package com.atguigu.gmall.search.service;

import com.atguigu.gmall.to.es.EsProduct;

import java.io.IOException;

public interface GmallSearchService {
    Boolean pulishSku2Es(EsProduct esProduct);
}
