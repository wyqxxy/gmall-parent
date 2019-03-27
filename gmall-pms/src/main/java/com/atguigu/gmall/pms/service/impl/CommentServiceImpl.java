package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.Comment;
import com.atguigu.gmall.pms.mapper.CommentMapper;
import com.atguigu.gmall.pms.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品评价表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@com.alibaba.dubbo.config.annotation.Service
@Component
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
