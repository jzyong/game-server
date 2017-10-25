package com.jjy.game.tool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Predicate;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Vicky
 * @mail eclipser@163.com
 * @phone 13618074943
 */
public class FileUtil {

    private final static Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 循环扫描,获得文件
     *
     * @param files
     * @param file
     * @param includes
     */
    public static void getRfFiles(List<File> files, File file, String[] includes) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File f : listFiles) {
                getRfFiles(files, f, includes);
            }
        } else {
            for (String include : includes) {
                if (file.getName().endsWith(include)) {
                    files.add(file);
                    break;
                }
            }

        }
    }

    /**
     * 创建目录
     *
     * @param dir
     */
    public static void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    /**
     * 创建文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean createFile(File file) throws IOException {
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                makeDir(file.getParentFile());
            }
        }
        return file.createNewFile();
    }

    public static void getFiles(String file, List<File> sourceFileList, final String endName, Predicate<String> condition) {
        File sourceFile = new File(file);
        getFiles(sourceFile, sourceFileList, endName, condition);
    }

    /**
     * 查找该目录下的所有的 endName 文件
     *
     * @param sourceFile ,单文件或者目录
     * @param sourceFileList 返回目录所包含的所有文件包括子目录
     * @param endName
     * @param condition
     */
    public static void getFiles(File sourceFile, List<File> sourceFileList, final String endName, Predicate<String> condition) {
        if (sourceFile.exists() && sourceFileList != null) {
            if (sourceFile.isDirectory()) {
                File[] childrenFiles = sourceFile.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if (pathname.isDirectory()) {
                            return true;
                        } else {
                            return pathname.getAbsolutePath().endsWith(endName);
                        }
                    }
                });
                for (File childFile : childrenFiles) {
                    getFiles(childFile, sourceFileList, endName, condition);
                }
            } else {
                if (condition == null || condition.test(sourceFile.getAbsolutePath())) {
                    sourceFileList.add(sourceFile);
                }
            }
        }
    }
    //</editor-fold>

    /**
     * 获取xml的实例
     *
     * @param <T>
     * @param path
     * @param fileName
     * @param configClass
     * @return
     */
    public static <T extends Object> T getConfigXML(String path, String fileName, Class<T> configClass) {
        T ob = null;
        fileName = path + File.separatorChar + fileName;
        if (!new File(fileName).exists()) {
            return ob;
        }
        Serializer serializer = new Persister();
        try {
            ob = serializer.read(configClass, new File(fileName));
        } catch (Exception ex) {
            log.error("文件" + fileName + "配置有误", ex);
        }
        return ob;
    }

    public static String readTxtFile(String path, String fileName) {
        return readTxtFile(path + File.separatorChar + fileName);
    }
    
    public static String readTxtFile(String filePath) {
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                StringBuffer sb = new StringBuffer();
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    sb.append(lineTxt).append("\n");
                }
                read.close();
                return sb.toString();
            } else {
                log.warn("文件{}配置有误,找不到指定的文件", file);
            }
        } catch (Exception e) {
            log.error("读取文件内容出错", e);
        }
        return null;
    }
}
