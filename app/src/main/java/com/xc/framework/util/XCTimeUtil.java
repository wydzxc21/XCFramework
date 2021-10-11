package com.xc.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author ZhangXuanChen
 * @date 2015-11-20
 * @package com.xc.framework.utils
 * @description 时间工具类
 */
public class XCTimeUtil {
    /**
     * 日期格式（yyyy-MM-dd）
     */
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    /**
     * 日期格式（HH:mm:ss）
     */
    public static final String FORMAT_TIME = "HH:mm:ss";
    /**
     * 日期格式（yyyy-MM-dd HH:mm）
     */
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    /**
     * 日期格式（yyyy-MM-dd HH:mm:ss）
     */
    public static final String FORMAT_DATE_SECOND = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期格式（yyyyMMddHHmmss）
     */
    public static final String FORMAT_DATE_SECOND_NUMBER = "yyyyMMddHHmmss";
    /**
     * 日期格式（yyyy-MM-dd HH:mm:ss.SSS）
     */
    public static final String FORMAT_DATE_MSEC = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * 日期格式（yyyy-MM-dd E）
     */
    public static final String FORMAT_DATE_WEEK = "yyyy-MM-dd E";
    /**
     * 日期格式（yyyy-MM-dd E HH:mm）
     */
    public static final String FORMAT_DATE_WEEK_TIME = "yyyy-MM-dd E HH:mm";
    /**
     * 时差-世界
     */
    public static final String TIME_ZONE_WORLD = "GMT+00:00";
    /**
     * 时差-北京
     */
    public static final String TIME_ZONE_BEIJING = "GMT+:08:00";

    /**
     * 获取当前时间
     *
     * @param simpleDateFormat 时间格式
     * @return 时间格式字符串
     */
    public static String getCurrentTime(SimpleDateFormat simpleDateFormat) {
        return getTime(System.currentTimeMillis(), simpleDateFormat);
    }

    /**
     * 获取当前时间
     *
     * @param dateFormat 时间格式
     * @return 时间格式字符串
     */
    public static String getCurrentTime(String dateFormat) {
        return getTime(System.currentTimeMillis(), dateFormat);
    }

    /**
     * 获取时间
     *
     * @param milliseconds 时间戳字符串
     * @param dateFormat   时间格式
     * @return 时间格式字符串
     */
    public static String getTime(String milliseconds, String dateFormat) {
        if (!XCStringUtil.isEmpty(milliseconds)) {
            return getTime(Long.parseLong(milliseconds), dateFormat);
        }
        return "";
    }

    /**
     * 获取时间
     *
     * @param milliseconds 时间戳
     * @param dateFormat   时间格式
     * @return 时间格式字符串
     */
    public static String getTime(long milliseconds, String dateFormat) {
        return getTime(milliseconds, dateFormat, null);
    }

    /**
     * 获取时间
     *
     * @param milliseconds 时间戳
     * @param dateFormat   时间格式
     * @return 时间格式字符串
     */
    public static String getTime(long milliseconds, String dateFormat, String timeZone) {
        return getTime(milliseconds, getSimpleDateFormat(dateFormat, timeZone));
    }

    /**
     * 获取时间
     *
     * @param milliseconds     时间戳
     * @param simpleDateFormat 时间格式
     * @return 时间格式字符串
     */
    public static String getTime(long milliseconds, SimpleDateFormat simpleDateFormat) {
        try {
            if (milliseconds < 0) {
                milliseconds = System.currentTimeMillis();
            }
            if (simpleDateFormat == null) {
                simpleDateFormat = getSimpleDateFormat(null);
            }
            return simpleDateFormat.format(new Date(milliseconds));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/2 13:58
     * Description：获取日期
     */
    public static Date getDate(String dateStr, String dateFormat) {
        return getDate(dateStr, dateFormat, null);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/2 13:58
     * Description：获取日期
     */
    public static Date getDate(String dateStr, String dateFormat, String timeZone) {
        return getDate(dateStr, getSimpleDateFormat(dateFormat, timeZone));
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/2 13:58
     * Description：获取日期
     */
    public static Date getDate(String dateStr, SimpleDateFormat simpleDateFormat) {
        try {
            if (XCStringUtil.isEmpty(dateStr)) {
                dateStr = getCurrentTime(FORMAT_DATE);
            }
            if (simpleDateFormat == null) {
                simpleDateFormat = getSimpleDateFormat(FORMAT_DATE);
            }
            return simpleDateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前星期
     *
     * @return 例:星期一
     */
    public static String getCurrentWeek() {
        return getWeek(getCurrentTime(FORMAT_DATE));
    }

    /**
     * 获取星期
     *
     * @param date 日期格式yyyy-MM-dd
     * @return 例:星期一
     */
    public static String getWeek(String date) {
        return getWeek(date, "");
    }

    /**
     * 获取星期
     *
     * @param date 日期格式yyyy-MM-dd
     * @return 例:星期一
     */
    public static String getWeek(String date, String timeZone) {
        return getWeek(date, getSimpleDateFormat(FORMAT_DATE, timeZone));
    }

    /**
     * 获取星期
     *
     * @param date 日期格式
     * @return 例:星期一
     */
    public static String getWeek(String date, SimpleDateFormat simpleDateFormat) {
        String week = "";
        try {
            if (simpleDateFormat == null) {
                simpleDateFormat = getSimpleDateFormat(FORMAT_DATE);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(simpleDateFormat.parse(date));
            int weekKey = calendar.get(Calendar.DAY_OF_WEEK);
            switch (weekKey) {
                case 1:
                    week = "星期天";
                    break;
                case 2:
                    week = "星期一";
                    break;
                case 3:
                    week = "星期二";
                    break;
                case 4:
                    week = "星期三";
                    break;
                case 5:
                    week = "星期四";
                    break;
                case 6:
                    week = "星期五";
                    break;
                case 7:
                    week = "星期六";
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return week;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/11 11:39
     * Description：比较相差天数
     */
    public static int compareDay(String beforeDate, String afterDate, String dateFormat) {
        return compareDay(beforeDate, afterDate, getSimpleDateFormat(dateFormat));
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/11 11:39
     * Description：比较相差天数
     */
    public static int compareDay(String beforeDate, String afterDate, SimpleDateFormat simpleDateFormat) {
        int day = 0;
        try {
            if (simpleDateFormat == null) {
                simpleDateFormat = getSimpleDateFormat(FORMAT_DATE);
            }
            day = compareDay(simpleDateFormat.parse(beforeDate), simpleDateFormat.parse(afterDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/11 11:39
     * Description：比较相差天数
     */
    public static int compareDay(Date beforeDate, Date afterDate) {
        int day = 0;
        try {
            day = compareDay(beforeDate.getTime(), afterDate.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/11 11:39
     * Description：比较相差天数
     */
    public static int compareDay(long beforeMillisecond, long afterMillisecond) {
        long day = 0;
        try {
            day = ((afterMillisecond - beforeMillisecond) / (1000L * 3600L * 24L));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) day;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/18 14:36
     * Description：getSimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(String dateFormat) {
        return getSimpleDateFormat(dateFormat, null);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/18 14:36
     * Description：getSimpleDateFormat
     */
    public static SimpleDateFormat getSimpleDateFormat(String dateFormat, String timeZone) {
        if (XCStringUtil.isEmpty(dateFormat)) {
            dateFormat = FORMAT_DATE;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.CHINESE);
        if (!XCStringUtil.isEmpty(timeZone)) {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        return simpleDateFormat;
    }
}
