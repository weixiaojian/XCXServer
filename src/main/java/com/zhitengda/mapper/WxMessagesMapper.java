package com.zhitengda.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhitengda.entity.WxMessages;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author langao_q
 * @since 2021-03-10
 */
@Repository
public interface WxMessagesMapper extends BaseMapper<WxMessages> {

    /**
     * 查询微信模板消息任务表
     * @return
     */
    List<WxMessages> qryWxMessage();

}
