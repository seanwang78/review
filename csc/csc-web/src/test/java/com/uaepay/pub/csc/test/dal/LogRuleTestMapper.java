package com.uaepay.pub.csc.test.dal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface LogRuleTestMapper {

    @Delete("delete from csc.t_log_rule where id = #{id,jdbcType=BIGINT}")
    int deleteById(@Param("id") Integer id);

    @Delete("delete from csc.t_log_rule where function_code = #{functionCode,jdbcType=VARCHAR}")
    int deleteByFunctionCode(@Param("functionCode") String functionCode);

}
