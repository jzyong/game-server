package com.jzy.game.engine.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间工具
 *
 * @author JiangZhiYong
 * @date 2017-05-03
 * QQ:359135103
 */
public class TimeUtil {

    private static final Logger log = LoggerFactory.getLogger(TimeUtil.class);

    /**
     * yyyy-MM-dd
     */
    public static final SimpleDateFormat DF1 = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final SimpleDateFormat DF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * yyyy-MM-dd HH:mm:ss:SSS
     */
    public static final SimpleDateFormat DF6 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    /**
     * yyyy-MM-dd HH
     */
    public static final SimpleDateFormat DF3 = new SimpleDateFormat("yyyy-MM-dd HH");

    /**
     * yyyy-MM-dd HH:mm
     */
    public static final SimpleDateFormat DF4 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * \\[([\\d|\\*]+)\\]\\[([\\d|\\*]+)\\](\\[([\\d|\\*|\\-|\\,]+)\\])?(\\[([w|W|\\d|\\-|\\,|\\*]+)\\])?(\\[(\\d+):(\\d+)-(\\d+):(\\d+)\\])
     */
    public static final String reg = "\\[([\\d|\\*]+)\\]\\[([\\d|\\*]+)\\](\\[([\\d|\\*|\\-|\\,]+)\\])?(\\[([w|W|\\d|\\-|\\,|\\*]+)\\])?(\\[(\\d+):(\\d+)-(\\d+):(\\d+)\\])";

    /**
     * 服务器时间偏移量
     */
    public static final long timeOffset = 0;


    /**
     * 返回指定天数以后的时间，凌晨
     *
     * @param currentTimeMillis
     * @param days
     * @return
     */
    public static long getAddDays(long currentTimeMillis, int days) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DF1.parse(DF1.format(new Date(currentTimeMillis))));
            calendar.add(Calendar.DATE, days);
            return calendar.getTime().getTime();
        } catch (ParseException ex) {
        }
        return 0;
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd")
     *
     * @param date
     * @return
     */
    public static String getDateFormat1(Date date) {
        return DF1.format(date);
    }

    public static Date getDateFormat1(String fmt) {
        try {
            return DF1.parse(fmt);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd")
     *
     * @return
     */
    public static String getDateFormat1() {
        return getDateFormat1(new Date());
    }

    /**
     * 获取当前时间的星期
     *
     * @return
     */
    public static int getWeekOfDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return w;
    }

    /**
     * 获取昨天时间 SimpleDateFormat("yyyy-MM-dd")
     *
     * @return
     */
    public static String getUpDay() {
        return getUpDay(new Date());
    }

    /**
     * 获取昨天时间 SimpleDateFormat("yyyy-MM-dd")
     *
     * @param date
     * @return
     */
    public static String getUpDay(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.HOUR_OF_DAY, -1);//日期减去一天
        return DF1.format(instance.getTime());
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH")
     *
     * @return
     */
    public static String getDateFormatHH() {
        return getDateFormatHH(new Date());
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH")
     *
     * @param date
     * @return
     */
    public static String getDateFormatHH(Date date) {
        return DF3.format(date);
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     *
     * @return
     */
    public static String getDateFormat2() {
        return getDateFormat2(new Date());
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     *
     * @param date
     * @return
     */
    public static String getDateFormat2(Date date) {
        return DF2.format(date);
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     *
     * @param fmt
     * @return
     */
    public static Date getDateFormat2(String fmt) {
        try {
            return DF2.parse(fmt);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     *
     * @param fmt
     * @return
     */
    public static Date getDateFormat4(String fmt) {
        try {
            return DF4.parse(fmt);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * 获取日期
     *
     * @param time
     * @return
     */
    public static int getDayOfMonth(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取月份
     *
     * @param time
     * @return
     */
    public static int getMonth(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取小时
     *
     * @param time
     * @return
     */
    public static int getDayOfHour(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取分钟
     *
     * @param time
     * @return
     */
    public static int getDayOfMin(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.MINUTE);
    }

    //<editor-fold defaultstate="collapsed" desc="获取当前日期的星期一时间 public String getDateWeekMonday()">
    /**
     * 获取星期几,周日为 7
     *
     * @param time
     * @return
     */
    public static int getDayOfWeek(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        if (w == 0) {
            w = 7;
        }
        return w;
    }

    /**
     * 获取当前日期的星期一时间，
     * <BR/>
     *
     * 修正过的 如果是星期天，则计算为上一周
     * <BR/>
     * 国际时间 星期天 为 一周 的开始
     *
     * @return
     */
    public static String getDateWeekMonday() {
        Calendar cal = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        if (w == 0) {
            //国际时间 星期天 为 一周 的开始
            //如果是星期天的话，计算为上一周
            cal.add(Calendar.DATE, -1);
        }
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return DF1.format(cal.getTime());
    }

    //</editor-fold>
    /**
     * 返回指定日期的季度第一天 yyyy-MM-dd
     *
     * @return
     */
    public static String getQuarterOfYear() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int newmonth = month % 3;
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month - newmonth);
        return TimeUtil.DF1.format(calendar.getTime());
    }

    /**
     * 获取每一个月的第一天的日期 yyyy-MM-dd
     *
     * @return
     */
    public static String getDateMonthDayString() {
        Calendar cal = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return DF1.format(cal.getTime());
    }

    /**
     * 获取每一年的第一天的日期 yyyy-MM-dd
     *
     * @return
     */
    public static String getDateYearDayString() {
        Calendar cal = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return DF1.format(cal.getTime());
    }

    /**
     * 获取指定时间 是一月内的第几周
     *
     * @param time
     * @return
     */
    public static int getDayOfWeekInMonth(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 获取一年内的第几天
     *
     * @param time
     * @return
     */
    public static int getDayOfYear(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取1970至今的时间, 1获取秒，2 分钟，3小时，4天数,5周数
     *
     * @param x
     * @param time
     * @return
     */
    public static long GetCurTimeInMin(int x, long time) {
        TimeZone zone = TimeZone.getDefault();	//默认时区
        long s = time / 1000;
        if (zone.getRawOffset() != 0) {
            s = s + zone.getRawOffset() / 1000;
        }
        switch (x) {
            case 1:
                break;
            case 2:
                s = s / 60;
                break;
            case 3:
                s = s / 3600;
                break;
            case 4:
                s = s / 86400;
                break;
            case 5:
                s = s / 86400 + 3;// 补足天数，星期1到7算一周
                s = s / 7;
                break;
            default:
                break;
        }
        return s;
    }


    /**
     * 判断两个时间是否在同一天
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameDay(long time1, long time2) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time1);
        int d1 = instance.get(Calendar.DAY_OF_YEAR);
        int y1 = instance.get(Calendar.YEAR);
        instance.setTimeInMillis(time2);
        int d2 = instance.get(Calendar.DAY_OF_YEAR);
        int y2 = instance.get(Calendar.YEAR);
        return d1 == d2 && y1 == y2;
    }

    /**
     * @return 获取当前时间
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis() + timeOffset;
    }

    //<editor-fold desc="比较两个时间是否在提供的时间范围内 compareTime(Date beginDate,Date endDate)">
    /**
     * 比较两个时间是否在提供的时间范围内
     *
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @param copareDate
     * @return
     */
    public static Boolean compareTime(Date beginDate, Date endDate, Date copareDate) {
        long nowMillisecond = copareDate == null ? currentTimeMillis() : copareDate.getTime();//当前时间毫秒数
        Boolean copare = false;
        if (beginDate != null && endDate == null) {
            long beginDateMill = beginDate.getTime();
            copare = beginDateMill < nowMillisecond;
        } else if (beginDate == null && endDate != null) {
            long endDateMill = endDate.getTime();
            copare = endDateMill > nowMillisecond;
        } else if (beginDate != null && endDate != null) {
            long beginDateMill = beginDate.getTime();
            long endDateMill = endDate.getTime();
            copare = beginDateMill < nowMillisecond && endDateMill > nowMillisecond;
        } else {
            copare = false;
        }
        return copare;
    }
    //</editor-fold>

    //<editor-fold desc="比较两个时间是否在当前时间范围内 compareTime(Date beginDate,Date endDate)">
    /**
     * 比较两个时间是否在当前时间范围内
     *
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    public static Boolean compareTime(Date beginDate, Date endDate) {
        return compareTime(beginDate, endDate, null);
    }
    //</editor-fold>
}
