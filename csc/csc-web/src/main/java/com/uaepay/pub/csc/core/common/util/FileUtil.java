package com.uaepay.pub.csc.core.common.util;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author lzb
 */
@Slf4j
public class FileUtil {

    /**
     * Create temporary file
     *
     * @param prefix
     *            File prefix
     * @param suffix
     *            File suffix
     * @return File
     */
    public static File createTempFile(String prefix, String suffix) {
        try {
            return Files.createTempFile(prefix, suffix).toFile();
        } catch (IOException e) {
            log.error("Create temporary file exception", e);
            throw new ErrorException("Create temporary file exception");
        }
    }


    /**
     * Delete file
     *
     * @param file
     *            File
     */
    public static boolean deleteFile(File file) {
        if (file.exists() || file.isDirectory()) {
            try {
                return file.delete();
            } catch (Exception ignore) {
            }
        }
        return false;
    }
}
