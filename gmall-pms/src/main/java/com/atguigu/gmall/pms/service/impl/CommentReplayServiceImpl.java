package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.CommentReplay;
import com.atguigu.gmall.pms.mapper.CommentReplayMapper;
import com.atguigu.gmall.pms.service.CommentReplayService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 产品评价回复表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@com.alibaba.dubbo.config.annotation.Service
@Component
public class CommentReplayServiceImpl extends ServiceImpl<CommentReplayMapper, CommentReplay> implements CommentReplayService {

}
