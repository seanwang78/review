package com.uaepay.pub.csc.test.dal;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.test.domain.TestData;

/**
 * @author zc
 */
public interface CompareTestMapper {

    @Delete("delete from csc.t_test_data where data_set = #{dataSet}")
    int deleteTestDataByDataSet(@Param("dataSet") String dataSet);

    @Insert("insert into csc.t_test_data (data_set, data_type, order_no, status, amount, currency, update_time)"
        + " values (#{dataSet}, #{dataType}, #{orderNo}, #{status}, #{amount}, #{currency}, #{updateTime})")
    int insertTestData(TestData testData);

    @Select("select define_id from csc.t_compare_define where define_name = #{defineName}")
    Long selectDefineByName(@Param("defineName") String defineName);

    @Delete("delete from csc.t_compare_define where define_id = #{defineId}")
    int deleteDefineById(@Param("defineId") long defineId);

    @Delete("delete from csc.t_compare_schedule where define_id = #{defineId}")
    int deleteScheduleByDefineId(@Param("defineId") long defineId);

    @Select("select * from csc.t_compare_task where task_type = 'S' and operator = 'system' and define_id = #{define_id} and memo = #{memo} order by create_time")
    @ResultMap("com.uaepay.pub.csc.core.dal.mapper.compare.CompareTaskMapper.BaseResultMap")
    List<CompareTask> selectTaskBySchedule(@Param("define_id") Long defineId, @Param("memo") String memo);

    @Select("select * from csc.t_compare_detail where task_id = #{taskId} order by detail_id")
    @ResultMap("com.uaepay.pub.csc.core.dal.mapper.compare.CompareDetailMapper.BaseResultMap")
    List<CompareDetail> selectDetailByTaskId(@Param("taskId") long taskId);

}
