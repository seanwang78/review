package com.uaepay.pub.csc.domain.enums;

import com.google.common.base.Charsets;
import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;
import com.uaepay.pub.csc.core.common.util.FileUtil;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.Constants;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.monitor.MonitorResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;

import static com.uaepay.basis.beacon.common.util.UuidUtil.generate;

/**
 * @author lzb
 */
@Slf4j
public enum AttachType implements CodeMessageEnum {

    /** */
    CSV("CSV", result -> {
        ColumnData columnData = result.getDetails().get(0).getColumnData();
        List<ColumnData.Column> columns = columnData.getColumnInfos();

        // 创建临时文件
        File csv = FileUtil.createTempFile(generate(), Constants.CSV_SUFFIX);
        try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(csv.toPath()))) {
            // 写入头
            output.write(columnData.joinColumn(Constants.CSV_SEPARATOR).getBytes(Charsets.UTF_8));
            // 写入数据
            for (RowData detail : result.getDetails()) {
                String[] row = new String[columns.size()];
                for (int i = 0; i < columns.size(); i++) {
                    ColumnData.Column column = columns.get(i);
                    Object item = detail.getItem(column.getName());
                    if (item != null) {
                        row[i] = String.valueOf(item);
                    } else {
                        row[i] = null;
                    }
                }
                output.write(Constants.LINE_SEPARATOR.getBytes());
                output.write(StringUtils.join(row, Constants.CSV_SEPARATOR).getBytes(Charsets.UTF_8));
            }
            output.flush();
        } catch (Exception e) {
            log.error("写入csv文件报错", e);
            throw new RuntimeException(e);
        }
        return csv;
    });

    AttachType(String code, Function<MonitorResult, File> monitorWriterFun) {
        this.code = code;
        this.monitorWriterFun = monitorWriterFun;
    }

    final String code;
    @Getter
    final Function<MonitorResult, File> monitorWriterFun;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return code;
    }

    public static AttachType getByCode(String code) {
        for (AttachType value : values()) {
            if (StringUtils.equalsIgnoreCase(code, value.code)) {
                return value;
            }
        }
        return null;
    }
}
