package com.xc.framework.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ZhangXuanChen
 * @date 2015-10-14
 * @package com.xc.framework.utils
 * @description 文件工具类
 */
public class XCFileUtil {

    /**
     * SDCard是否存在
     *
     * @return 是否存在
     */
    public static boolean isSDCardExist() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    /**
     * 获取SDCard根目录
     *
     * @return 例:/storage/emulated/0
     */
    public static java.lang.String getSDCardDir() {
        if (isSDCardExist()) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (!XCStringUtil.isEmpty(path)) {
                return path;
            }
        }
        return "";
    }

    /**
     * 获取手机内部缓存路径
     *
     * @param context 上下文
     * @return 内存少时会被自动清除，例:/data/data/com.xc.sample/cache
     */
    public static java.lang.String getCacheDir(Context context) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            return cacheDir.getPath();
        }
        return "";
    }

    /**
     * 获取手机外部缓存路径
     *
     * @param context 上下文
     * @return 内存少时不会被自动清除，例:/storage/emulated/0/Android/data/com.xc.sample/cache
     */
    public static java.lang.String getExternalCacheDir(Context context) {
        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir != null) {
            return externalCacheDir.getPath();
        }
        return "";
    }

    /**
     * 获取U盘唯一路径
     *
     * @return
     */
    public static java.lang.String getUsbDir(Context context) {
        List<String> usbDirs = XCFileUtil.getUsbDirList(context);
        if (usbDirs == null || usbDirs.size() == 0) {
            return null;
        }
        return usbDirs.get(0);
    }

    /**
     * 获取U盘路径集合
     *
     * @return
     */
    public static List<java.lang.String> getUsbDirList(Context context) {
        List<String> usbDirs = new ArrayList<>();
        try {
            StorageManager srgMgr = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class<StorageManager> srgMgrClass = StorageManager.class;
            String[] paths = (String[]) srgMgrClass.getMethod("getVolumePaths").invoke(srgMgr);
            for (String path : paths) {
                Object volumeState = srgMgrClass.getMethod("getVolumeState", String.class).invoke(srgMgr, path);
                if (!path.contains("emulated") && Environment.MEDIA_MOUNTED.equals(volumeState)) {
                    usbDirs.add(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usbDirs;
    }

    /**
     * 获取下载文件名
     *
     * @param downloadUrl 下载地址
     * @return 文件名
     */
    public static java.lang.String getDownloadFileName(java.lang.String downloadUrl) {
        try {
            if (!XCStringUtil.isEmpty(downloadUrl)) {
                if (downloadUrl.contains("/")) {
                    String[] split = downloadUrl.split("/");
                    String temp = split[split.length - 1];
                    if (temp.contains("?")) {
                        return temp.split("\\?")[0];
                    } else {
                        return temp;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param folderPath 绝对路径
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 创建文件夹
     */
    public static boolean createFolder(java.lang.String folderPath) {
        try {
            if (!XCStringUtil.isEmpty(folderPath)) {
                File dirFile = new File(folderPath);
                if (!dirFile.exists()) {
                    return dirFile.mkdirs();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param folderPath 绝对路径
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 删除文件夹(含其根目录文件)
     */
    public static boolean deleteFolder(java.lang.String folderPath) {
        try {
            if (!XCStringUtil.isEmpty(folderPath)) {
                File dirFile = new File(folderPath);
                if (dirFile.exists() && dirFile.isDirectory()) {
                    File[] files = dirFile.listFiles();
                    if (files != null && files.length > 0) {
                        for (int i = files.length - 1; i >= 0; i--) {
                            File file = files[i];
                            if (file != null) {
                                deleteFile(file);
                            }
                        }
                    }
                    return dirFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param filePath 绝对路径
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 文件是否存在
     */
    public static boolean isFileExist(java.lang.String filePath) {
        try {
            if (!XCStringUtil.isEmpty(filePath)) {
                return new File(filePath).exists();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param filePath 绝对路径
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 创建文件（含后缀名）
     */
    public static boolean createFile(java.lang.String filePath) {
        try {
            if (!XCStringUtil.isEmpty(filePath)) {
                File file = new File(filePath);
                if (!file.exists()) {
                    return file.createNewFile();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param filePath 文件绝对路径
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 删除文件（含后缀名）
     */
    public static boolean deleteFile(java.lang.String filePath) {
        return deleteFile(new File(filePath));
    }

    /**
     * @param file 文件
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 删除文件
     */
    public static boolean deleteFile(File file) {
        try {
            if (file != null) {
                if (file.exists() && file.isFile()) {
                    return file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param bytes    字节数组
     * @param filePath 文件绝对路径（含后缀名）
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 写入字节数组
     */
    public static boolean writeBytes(byte[] bytes, java.lang.String filePath) {
        return writeBytes(bytes, new File(filePath));
    }

    /**
     * @param bytes 字节数组
     * @param file  文件
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 写入字节数组
     */
    public static boolean writeBytes(byte[] bytes, File file) {
        try {
            if (bytes != null && bytes.length > 0 && file != null) {
                if (file.exists() && file.isFile()) {
                    FileOutputStream os = new FileOutputStream(file, true);
                    BufferedOutputStream bos = new BufferedOutputStream(os);
                    bos.write(bytes);
                    bos.write("\r\n".getBytes());
                    bos.close();
                    os.close();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param filePath 文件绝对路径（含后缀名）
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 读取字节数组
     */
    public static byte[] readBytes(java.lang.String filePath) {
        return readBytes(new File(filePath));
    }

    /**
     * @param file 文件
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 读取字节数组
     */
    public static byte[] readBytes(File file) {
        try {
            if (file != null) {
                if (file.exists() && file.isFile()) {
                    FileInputStream is = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    ByteBuffer bb = ByteBuffer.allocate((int) file.length());
                    int len;
                    byte[] bytes = new byte[1024];
                    while ((len = bis.read(bytes)) != -1) {
                        bb.put(bytes, 0, len);
                    }
                    bis.close();
                    is.close();
                    return bb.array();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param content  写入内容
     * @param filePath 文件绝对路径（含后缀名）
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 写入文本
     */
    public static boolean writeText(java.lang.String content, java.lang.String filePath) {
        return writeText(content, new File(filePath));
    }

    /**
     * @param content 写入内容
     * @param file    文件
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 写入文本
     */
    public static boolean writeText(java.lang.String content, File file) {
        try {
            if (!XCStringUtil.isEmpty(content) && file != null) {
                if (file.exists() && file.isFile()) {
                    OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
                    BufferedWriter bw = new BufferedWriter(osw);
                    bw.write(content);
                    bw.flush();
                    bw.newLine();
                    bw.close();
                    osw.close();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param filePath 文件绝对路径（含后缀名）
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 读取文本
     */
    public static java.lang.String readText(java.lang.String filePath) {
        return readText(new File(filePath));
    }

    /**
     * @param file 文件
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 读取文本
     */
    public static java.lang.String readText(File file) {
        try {
            if (file != null) {
                if (file.exists() && file.isFile()) {
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
                    BufferedReader br = new BufferedReader(isr);
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    isr.close();
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param folderPath 文件夹绝对路径
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/20
     * @description 获取文件list
     */
    public static List<File> getFileList(java.lang.String folderPath) {
        List<File> fileList = new ArrayList<File>();
        if (!XCStringUtil.isEmpty(folderPath)) {
            File dirFile = new File(folderPath);
            if (dirFile.exists() && dirFile.isDirectory()) {
                File[] files = dirFile.listFiles();
                if (files != null && files.length > 0) {
                    Collections.addAll(fileList, files);
                }
            }
        }
        return fileList;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/5/11 9:02
     * Description：获取文件大小字符串
     */
    public static java.lang.String getFileSizeStr(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeStr;
        if (fileSize == 0) {
            return "0B";
        }
        if (fileSize < 1024) {
            fileSizeStr = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeStr = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeStr = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeStr = df.format((double) fileSize / 1073741824) + "GB";
        }
        return fileSizeStr;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/17 10:03
     * Description：复制文件
     * Param：sourceFile 资源文件
     * Param：targetFile 目标文件
     * Return：boolean
     */
    public static boolean copyFile(File sourceFile, File targetFile) {
        if (sourceFile == null || targetFile == null) {
            return false;
        }
        try {
            if (!sourceFile.exists()) {
                return false;
            }
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            FileInputStream fi = new FileInputStream(sourceFile);
            FileOutputStream fo = new FileOutputStream(targetFile);
            FileChannel in = fi.getChannel();
            FileChannel out = fo.getChannel();
            in.transferTo(0, in.size(), out);
            //
            fi.close();
            in.close();
            fo.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/8/17 10:35
     * Description：复制assets文件
     * Param：context
     * Param：fileName 资源文件
     * Param：outFile 输出文件
     * Return：boolean
     */
    public static boolean copyAssetsFile(Context context, java.lang.String fileName, File outFile) {
        if (context == null || XCStringUtil.isEmpty(fileName) || outFile == null) {
            return false;
        }
        try {
            InputStream is = context.getResources().getAssets().open(fileName);
            if (is == null) {
                return false;
            }
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            byte[] bytes = new byte[1024];
            int bt;
            FileOutputStream fos = new FileOutputStream(outFile);
            while ((bt = is.read(bytes)) != -1) {
                fos.write(bytes, 0, bt);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
