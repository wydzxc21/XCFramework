package com.xc.framework.util;

import android.content.Context;

import com.xc.framework.util.XCFileUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * @author ZhangXuanChen
 * @date 2021/6/29
 * @package com.zxc.hotfix
 * @description 热修复工具-仅支持类修复-dex（app->build->intermediates->dex）
 */
public class XCHotfixUtil {
    public static final String DEX_DIR = "xcDex";//拷贝存放
    public static final String ODEX_DIR = "xcOdex";//dex优化为odex


    /**
     * @param dexDir dex文件目录
     * @author ZhangXuanChen
     * @date 2021/6/29
     * @description 加载dex-Application中调用，文件名称必须classes开头、dex格式，
     */
    public static boolean loadDex(Context context, File dexDir) {
        if (context == null || dexDir == null) {
            return false;
        }
        doDexCopy(context, dexDir);
        return doDexInject(context, new File(getCopyPath(context)));
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/30
     * @package com.zxc.hotfix
     * @description 删除dex
     */
    public static boolean deleteDex(Context context) {
        if (context == null) {
            return false;
        }
        boolean isCopy = XCFileUtil.deleteFolder(getCopyPath(context));
        boolean isOptimized = XCFileUtil.deleteFolder(getOptimizedPath(context));
        if (isCopy && isOptimized) {
            return true;
        }
        return false;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/30
     * @package com.zxc.hotfix
     * @description getCopyPath
     */
    private static String getCopyPath(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator + DEX_DIR;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/30
     * @package com.zxc.hotfix
     * @description getOptimizedPath
     */
    private static String getOptimizedPath(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator + ODEX_DIR;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/29
     * @description getDexList
     */
    private static HashSet<File> getDexList(File dexDir) {
        if (dexDir == null) {
            return null;
        }
        HashSet<File> dexList = new HashSet<File>();
        File[] listFiles = dexDir.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.getName().startsWith("classes") && file.getName().endsWith(".dex")) {
                    dexList.add(file);
                }
            }
        }
        return dexList;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/30
     * @package com.zxc.hotfix
     * @description doDexCopy
     */
    private static boolean doDexCopy(Context context, File dexDir) {
        if (dexDir == null) {
            return false;
        }
        HashSet<File> dexList = getDexList(dexDir);
        if (dexList != null && !dexList.isEmpty()) {
            File copyFile = new File(getCopyPath(context));
            if (!copyFile.exists()) {
                copyFile.mkdirs();
            }
            //
            for (File dexFile : dexList) {
                if (!XCFileUtil.copyFile(dexFile, new File(copyFile.getAbsoluteFile() + File.separator + dexFile.getName()))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/29
     * @description doDexInject
     */
    private static boolean doDexInject(Context context, File dexDir) {
        if (context == null || dexDir == null) {
            return false;
        }
        try {
            HashSet<File> dexList = getDexList(dexDir);
            if (dexList != null && !dexList.isEmpty()) {
                File optimizedFile = new File(getOptimizedPath(context));
                if (!optimizedFile.exists()) {
                    optimizedFile.mkdirs();
                }
                //
                PathClassLoader pathLoader = (PathClassLoader) context.getClassLoader();
                for (File dexFile : dexList) {
                    DexClassLoader dexLoader = new DexClassLoader(
                            dexFile.getAbsolutePath(),
                            optimizedFile.getAbsolutePath(),
                            null,
                            pathLoader
                    );
                    //
                    Object dexPathList = getPathList(dexLoader);
                    Object pathPathList = getPathList(pathLoader);
                    //
                    Object leftDexElements = getDexElements(dexPathList);
                    Object rightDexElements = getDexElements(pathPathList);
                    //
                    Object dexElements = combineArray(leftDexElements, rightDexElements);
                    //
                    Object pathList = getPathList(pathLoader);
                    setField(pathList, pathList.getClass(), "dexElements", dexElements);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/29
     * @description 反射给对象中的属性重新赋值
     */
    private static void setField(Object obj, Class<?> cl, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = cl.getDeclaredField(field);
        declaredField.setAccessible(true);
        declaredField.set(obj, value);
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/29
     * @description 反射得到对象中的属性值
     */
    private static Object getField(Object obj, Class<?> cl, String field) throws NoSuchFieldException, IllegalAccessException {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/29
     * @description 反射得到类加载器中的pathList对象
     */
    private static Object getPathList(Object baseDexClassLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/29
     * @description 反射得到pathList中的dexElements
     */
    private static Object getDexElements(Object pathList) throws NoSuchFieldException, IllegalAccessException {
        return getField(pathList, pathList.getClass(), "dexElements");
    }

    /**
     * @author ZhangXuanChen
     * @date 2021/6/29
     * @description 数组合并
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> clazz = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = Array.getLength(arrayRhs);
        int k = i + j;
        Object result = Array.newInstance(clazz, k);
        System.arraycopy(arrayLhs, 0, result, 0, i);
        System.arraycopy(arrayRhs, 0, result, i, j);
        return result;
    }
}

