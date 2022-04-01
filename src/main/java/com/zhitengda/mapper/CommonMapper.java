package com.zhitengda.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhitengda.entity.*;
import com.zhitengda.vo.OrderConstantDto;
import com.zhitengda.vo.PageVo;
import com.zhitengda.vo.RetScanPathDto;
import com.zhitengda.vo.TrackFreightVo;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 公共 Mapper 接口
 * </p>
 *
 * @author langao_q
 * @since 2021-02-23
 */
@Repository
public interface CommonMapper extends BaseMapper {

    /**
     * 从数据库获取订单号
     * @param from
     * @return
     */
    String getOrderCode(String from);

    /**
     * 校验运单号是否被使用
     * @param billCode
     * @return
     */
    Integer checkBillCode(String billCode);

    /**
     * 根据运单号获取绑定的所属网点
     * @param billCode
     * @return
     */
    Site getSendSiteByBillCode(String billCode);

    /**
     * 获取下单常量配置
     * @param dicCode
     * @return
     */
    List<OrderConstantDto> getOrderConstant(String dicCode);

    /**
     * 获取寄件网点面单库存
     * @param sendSiteCode
     * @return
     */
    Map<String, Object> checkInventory(String sendSiteCode);


    /**
     * 根据运单号查询扫描轨迹
     * @param billCode
     * @return
     */
    List<RetScanPathDto> getScanBillCode(String billCode);

    /**
     * 计算运费
     * @param trackFreight
     * @return
     */
    BigDecimal trackFreight(TrackFreightVo trackFreight);

    /**
     * 根据订单号或运单号查询订单数据
     * @param order
     * @return
     */
    Order getOrderByOrderBill(Order order);

    /**
     * 分页查询我寄/我收的数据
     * @param pageVo
     * @return
     */
    Page<Order> findMyOrderPage(IPage<Order> page, PageVo pageVo);

    /**
     * 统计我收的
     * @param aesEncryptString
     * @return
     */
    Integer getRecOrderCount(String aesEncryptString);

    /**
     * 分页查询我的运单
     * @param page
     * @return
     */
    IPage<Bill> findMyWaybillPage(IPage<Bill> page, PageVo pageVo);

    /**
     * 根据订单号集合获取运单List数据
     * @param param
     * @return
     */
    List<Bill> getOrderPrint(HashMap<String, Object> param);

    /**
     * 下单成功如果未匹配到寄件网点的需要给总部下的客服部门员工发送消息
     * @param param
     * @return
     */
    int sendRecMsg(Map param);

    /**
     * 生成团队ID
     * @return
     */
    String getGroupId();

    /**
     * 获取客户编码
     */
    String getCustomerCode();

    /**
     * 根据小程序的openId查询公众号openId·
     * @param openId
     * @return
     */
    String findGzhOpenId(String openId);

    /**
     * 根据客户编码查询账号及密码
     * @param customerCode
     * @return
     */
    Map<String, String> getUserMonthly(String customerCode);

    /**
     * 更改运单的打印状态
     * @param param
     * @return
     */
    int insertPrintLog(Map<String, Object> param);

    /**
     * 根据订单查询对于的运单号集合
     * @param orderBills
     * @return
     */
    List<Order> getOrderByOrderCode(List<String> orderBills);

    /**
     * 添加投诉建议
     * @param wxAdvice
     * @return
     */
    int addWxAdvice(WxAdvice wxAdvice);

    /**
     * 校验用户是否已经在通过大网验证的实名表中TAB_REAL_NAME
     * @param param
     * @return
     */
    BigNetRealName getCheckRealName(BigNetRealName param);

    /**
     * 添加大网实名验证记录
     * @param param
     * @return
     */
    int addCheckRealName(BigNetRealName param);

    /**
     * 更新大网实名验证记录
     * @param param
     * @return
     */
    int uptCheckRealName(BigNetRealName param);

    /**
     * 校验网点预付款账户是否欠费：目前结算余额 <= 警戒结算金额
     * @param sendSiteCode
     * @return
     */
    Integer checkSiteAccOwe(String sendSiteCode);

    /**
     * 校验网点COD账户是否欠费：COD目前结算余额 <= COD警戒结算金额
     * @param sendSiteCode
     * @return
     */
    Integer checkSiteCodOwe(String sendSiteCode);

    /**
     * 查询系统常规设置
     * @param shareCode
     * @return
     */
    SysShareSet getShareSet(String shareCode);
}
