package com.uaepay.pub.csc.core.common.util;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.rest.RestStatus;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.BadSqlGrammarException;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.domain.enums.TaskErrorCodeEnum;

/**
 * 异常判断工具
 * 
 * @author zc
 */
public class ErrorUtil {

    /**
     * 是否是无法恢复的异常
     * 
     * @param e
     * @return
     */
    public static boolean isErrorNotRecoverable(Throwable e) {
        if (e instanceof BadSqlGrammarException || e instanceof FailException
            || e instanceof InvalidDataAccessApiUsageException) {
            return true;
        } else if (e instanceof ElasticsearchStatusException) {
            RestStatus status = ((ElasticsearchStatusException)e).status();
            if (status == RestStatus.BAD_REQUEST || status == RestStatus.NOT_FOUND) {
                return true;
            }
        }
        return false;
    }

    public static String convertErrorCodeToRecord(Throwable e) {
        String code = CommonReturnCode.SYSTEM_ERROR.getCode();
        if (e instanceof FailException) {
            code = ((FailException)e).getCode();
        } else if (e instanceof ErrorException) {
            code = ((ErrorException)e).getCode();
        } else if (e instanceof BadSqlGrammarException || e instanceof InvalidDataAccessApiUsageException) {
            code = TaskErrorCodeEnum.BAD_SQL.getCode();
        } else if (e instanceof ElasticsearchStatusException) {
            RestStatus status = ((ElasticsearchStatusException)e).status();
            if (status == RestStatus.BAD_REQUEST || status == RestStatus.NOT_FOUND) {
                code = TaskErrorCodeEnum.BAD_SQL.getCode();
            }
        }
        return code;
    }

}
