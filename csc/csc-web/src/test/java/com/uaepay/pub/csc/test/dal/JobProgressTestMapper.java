package com.uaepay.pub.csc.test.dal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * @author zc
 */
public interface JobProgressTestMapper {

    @Delete("delete from csc.t_job_progress")
    int deleteAll();

    @Delete("delete from csc.t_job_progress where job_code = #{jobCode}")
    int deleteByJobCode(@Param("jobCode") String jobCode);

}
