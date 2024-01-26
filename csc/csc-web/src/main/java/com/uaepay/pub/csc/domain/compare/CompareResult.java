package com.uaepay.pub.csc.domain.compare;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author zc
 */
@Data
public class CompareResult {

    public CompareResult() {}

    public CompareResult(int passCount, List<ErrorDetail> lackDetails, List<ErrorDetail> mismatchDetails) {
        this.passCount = passCount;
        this.lackDetails = lackDetails;
        this.mismatchDetails = mismatchDetails;
    }

    int passCount;

    List<ErrorDetail> lackDetails = new ArrayList<>();

    List<ErrorDetail> mismatchDetails = new ArrayList<>();

    /** 指示是否对账完成 */
    boolean complete = true;

    boolean containsCompensate;

    public void merge(CompareResult result, int maxErrorCount) {
        if (result == null) {
            return;
        }
        passCount += result.getPassCount();
        for (ErrorDetail detail : result.getLackDetails()) {
            if (getErrorCount() >= maxErrorCount) {
                break;
            }
            lackDetails.add(detail);
        }
        for (ErrorDetail detail : result.getMismatchDetails()) {
            if (getErrorCount() >= maxErrorCount) {
                break;
            }
            mismatchDetails.add(detail);
        }
    }

    public String buildCompareStatistic() {
        StringBuilder sb = new StringBuilder();
        sb.append("pass: ").append(passCount).append(", ");
        if (lackDetails.size() > 0) {
            sb.append("lack: ").append(lackDetails.size());
            if (!complete) {
                sb.append("+");
            }
            sb.append(", ");
        }
        if (mismatchDetails.size() > 0) {
            sb.append("mismatch: ").append(mismatchDetails.size());
            if (!complete) {
                sb.append("+");
            }
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    public int getAllCount() {
        return passCount + lackDetails.size() + mismatchDetails.size();
    }

    public int getErrorCount() {
        return lackDetails.size() + mismatchDetails.size();
    }

}
