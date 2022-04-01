package com.zhitengda.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 基本资料-员工资料表
 * </p>
 *
 * @author langao_q
 * @since 2021-04-28
 */
@Data
@TableName("TAB_EMPLOYEE")
@EqualsAndHashCode(callSuper = false)
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工编号
     */
    @TableId
    private String employeeCode;

    /**
     * 员工名称
     */
    private String employeeName;

    /**
     * 所属网点
     */
    private String ownerSite;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 员工手机
     */
    private String mobilePhone;

    /**
     * 联系住址
     */
    private String address;

    /**
     * 巴枪操作密码
     */
    private String barPassword;

    /**
     * 员工类型(仓库操作员 、 取派员、双重身份)
     */
    private String employeeType;

    /**
     * 所属承包区
     */
    private String ownerRange;

    /**
     * 所属承包区CODE
     */
    private String ownerRangeCode;

    /**
     * 所属网点CODE
     */
    private String ownerSiteCode;

    /**
     * 登录密码
     */
    private String ePwd;

    /**
     * 与该用户相应的机器ID，以此限制一个用户只能在第一次登录的电脑
     */
    private String eComputid;

    /**
     * 登陆密码最后修改时间
     */
    private Date ePwdUpdateDate;

    /**
     * 性别（1代表男，0代表女）
     */
    private String sex;

    /**
     * 身高
     */
    private String stature;

    /**
     * 体重
     */
    private String avoirdupois;

    /**
     * 服装尺码
     */
    private String dressSize;

    /**
     * 身份证号码
     */
    private String identityCard;

    /**
     * 员工账户编号
     */
    private String drivingLicence;

    /**
     * 开户行
     */
    private String openBank;

    /**
     * 开户账户
     */
    private String openAccount;

    /**
     * 支付宝帐号
     */
    private String alipay;

    /**
     * 认证手机
     */
    private String certifiedPhone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * POS机编号
     */
    private String posCode;

    /**
     * 手持终端编号
     */
    private String pdaCode;

    /**
     * 是否强制更新基础数据
     */
    private Integer updatebase;

    /**
     * 是否强制更新DLL
     */
    private Integer updatedll;

    /**
     * 是否接单员
     */
    private Integer blOperation;

    /**
     * 是否启用标识(1：启用，0：停用)
     */
    private Integer blOpen;

    /**
     * 出生日期
     */
    private Date birthDate;

    /**
     * 入职日期
     */
    private Date entryDate;

    /**
     * 合同日期
     */
    private Date endDate;

    /**
     * 收派范围
     */
    private String range;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 创建人
     */
    private String createMan;

    /**
     * 创建网点
     */
    private String createSite;

    /**
     * 创建网点CODE
     */
    private String createSiteCode;

    /**
     * 修改时间
     */
    private Date modifyDate;

    /**
     * 修改人
     */
    private String modifyMan;

    /**
     * 修改人CODE
     */
    private String modifyManCode;

    /**
     * 修改网点
     */
    private String modifySite;

    /**
     * 修改网点CODE
     */
    private String modifySiteCode;

    /**
     * 角色ID 多个角色用逗号分隔
     */
    private String rId;

    /**
     * 是否启用验证机器ID
     */
    private Integer blComputid;

    /**
     * 员工ID
     */
    private Integer empId;

    /**
     * 是否接收消息标识（1：接收，0：不接收）
     */
    private Integer blNotice;

    /**
     * 联系邮箱  --加密
     */
    private String email;

    private Integer sync;

    /**
     * 是否已推送员工信息标识（0：未推送，1：已推送）
     */
    private Integer blPushEmp;

    /**
     * 创建人CODE
     */
    private String createManCode;

    /**
     * 是否接收ERP处理消息标识（1：接收，0：不接收）
     */
    private Integer blErpNotice;

    /**
     * 是否经营支持岗标识（1：是，0：否）
     */
    private Integer blOperationPost;

    /**
     * 三段码名称
     */
    private String threeCode;


}
