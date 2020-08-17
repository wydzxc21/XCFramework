package com.xc.framework.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    public static String getSDCardDir() {
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
    public static String getCacheDir(Context context) {
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
    public static String getExternalCacheDir(Context context) {
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
    public static String getUsbPath(Context context) {
        List<String> usbDirs = XCFileUtil.getUsbPathList(context);
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
    public static List<String> getUsbPathList(Context context) {
        List<String> usbPaths = new ArrayList<>();
        try {
            StorageManager srgMgr = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Class<StorageManager> srgMgrClass = StorageManager.class;
            String[] paths = (String[]) srgMgrClass.getMethod("getVolumePaths").invoke(srgMgr);
            for (String path : paths) {
                Object volumeState = srgMgrClass.getMethod("getVolumeState", String.class).invoke(srgMgr, path);
                if (!path.contains("emulated") && Environment.MEDIA_MOUNTED.equals(volumeState))
                    usbPaths.add(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usbPaths;
    }

    /**
     * 获取下载文件名
     *
     * @param downloadUrl 下载地址
     * @return 文件名
     */
    public static String getDownloadFileName(String downloadUrl) {
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
    public static boolean createFolder(String folderPath) {
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
    public static boolean deleteFolder(String folderPath) {
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
    public static boolean isFileExist(String filePath) {
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
    public static boolean createFile(String filePath) {
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
    public static boolean deleteFile(String filePath) {
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
     * @param content  写入内容
     * @param filePath 文件绝对路径（含后缀名）
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 写入文件
     */
    public static boolean writeFile(String content, String filePath) {
        try {
            if (!XCStringUtil.isEmpty(content) && !XCStringUtil.isEmpty(filePath)) {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    FileOutputStream os = new FileOutputStream(file, true);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                    bw.write(content);
                    bw.newLine();
                    bw.close();
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
     * @description 读取文件
     */
    public static String readFile(String filePath) {
        return readFile(new File(filePath));
    }

    /**
     * @param file 文件
     * @return
     * @author ZhangXuanChen
     * @date 2020/2/19
     * @description 读取文件
     */
    public static String readFile(File file) {
        try {
            if (file != null) {
                if (file.exists() && file.isFile()) {
                    FileInputStream is = new FileInputStream(file);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    is.close();
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
    public static List<File> getFileList(String folderPath) {
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
    public static String getFileSizeStr(long fileSize) {
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
}
