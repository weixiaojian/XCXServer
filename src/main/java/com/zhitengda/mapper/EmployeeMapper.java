package com.zhitengda.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhitengda.entity.Employee;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 基本资料-员工资料表 Mapper 接口
 * </p>
 *
 * @author langao_q
 * @since 2021-02-22
 */
@Repository
public interface EmployeeMapper extends BaseMapper<Employee> {

}
