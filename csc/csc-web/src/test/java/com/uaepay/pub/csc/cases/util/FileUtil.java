package com.uaepay.pub.csc.cases.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class FileUtil {
    public static String readToString(String fileName) {
        File file;
        if (!fileName.startsWith("/")) {
            ClassLoader classLoader = FileUtil.class.getClassLoader();
            // getResource()方法会去classpath下找这个文件，获取到url resource, 得到这个资源后，调用url.getFile获取到 文件 的绝对路径
            URL url = classLoader.getResource(fileName);
            // url.getFile() 得到这个文件的绝对路径
            assert url != null;
            System.out.println(url.getFile());
            file = new File(url.getFile());
        } else {
            file = new File(fileName);
        }
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new RuntimeException("单元测试文件读取异常：" + fileName, e);
        }
    }
}
