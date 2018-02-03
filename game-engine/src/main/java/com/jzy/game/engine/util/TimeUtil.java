package com.jzy.game.engine.util;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Locale;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * 时间
 *
 */
public class TimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtil.class);

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public final static DateTimeFormatter YYYYMMDDHHMMSS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.getDefault());

    /**
     * yyyy-MM-dd HH:mm
     */
    public final static DateTimeFormatter YYYYMMDDHHMM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * yyyy-MM-dd HH
     */
    public final static DateTimeFormatter YYYYMMDDHH = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

    /**
     * yyyy-MM-dd
     */
    public final static DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 服务器时间偏移量
     */
    private static long timeOffset = 0;//86400000

    /**@
     * 返回按格式要求的文本，如yyyy-MM-dd HH:mm:ss
     *
     * @param localDateTime
     * @param formatter
     * @return
     */
    public static String getDateTimeFormat(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);
    }

    /**
     * @
     * @param time
     * @param formatter
     * @return
     */
    public static String getDateTimeFormat(long time, DateTimeFormatter formatter) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        return ldt.format(formatter);
    }

    /**@
     * 获取时间字符串
     * @param formatter
     * @return
     */
    public static String getDateTimeFormat(DateTimeFormatter formatter) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault());
        return ldt.format(formatter);
    }

    /**
     * 带时区的时间
     * @param zonedDateTime
     * @param formatter
     * @return
     */
    public static String getDateTimeFormat(ZonedDateTime zonedDateTime, DateTimeFormatter formatter) {
        return zonedDateTime.format(formatter);
    }

    /**@
     * 根据格式转换为LocalDateTime
     *
     * @param text
     * @param formatter
     * @return
     */
    public static LocalDateTime getLocalDateTime(String text, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(text, formatter);
        } catch (Exception e) {
            LOGGER.error("getLocalDateTime", e);
        }
        return null;
    }

    /**
     * @
     * @param text
     * @param formatter
     * @return
     */
    public static ZonedDateTime getZonedDateTime(String text, DateTimeFormatter formatter) {
        try {
            LocalDateTime m1 = LocalDateTime.parse(text, formatter);
            return m1.atZone(ZoneId.systemDefault());
        } catch (Exception e) {
            LOGGER.error("getLocalDateTime", e);
        }
        return null;
    }

    /**@
     * 获取与今日相差天数的日期格式，负为日期前，正为日期后。如yyyy-MM-dd HH
     *
     * @param days
     * @param formatter
     * @return
     */
    public static String getOffToDay(int days, DateTimeFormatter formatter) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault());
        if (days < 0) {
            ldt = ldt.minusDays(-days);
        } else {
            ldt = ldt.plusDays(days);
        }
        return ldt.format(formatter);
    }

    /**@
     * 获取与今日相差天数的日期凌晨的毫秒数，负为日期前，正为日期后。如yyyy-MM-dd HH
     *
     * @param days
     * @return
     */
    public static long getOffToDayZeroMil(int days) {
        try {
            ZonedDateTime ldt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault());
            if (days < 0) {
                ldt = ldt.minusDays(-days);
            } else {
                ldt = ldt.plusDays(days);
            }
            ldt = ZonedDateTime.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(), 0, 0, 0, 0, ZoneId.systemDefault());
            return ldt.toInstant().toEpochMilli();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return 0;
    }

    /**@
     * 获取传入毫秒的月份中的日 1-12
     *
     * @param time
     * @return
     */
    public static int getDayOfMonth(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).getDayOfMonth();
    }

    /**@
     * 获取系统当前月份中的日 1-12
     *
     * @return
     */
    public static int getDayOfMonth() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault()).getDayOfMonth();
    }

    /**@
     * 获取一月最小的天数
     *
     * @return
     */
    public static int getMinDaysOfMonth() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault()).getMonth().minLength();
    }

    /**
     * 获取传入毫秒的月份 1-12
     *
     * @param time
     * @return
     */
    public static int getMonth(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).getMonthValue();
    }

    /**
     * 获取系统当前月份 1-12
     *
     * @return
     */
    public static int getMonth() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault()).getMonthValue();
    }

    /**
     * 获取传入毫秒的当日小时 0 to 23
     *
     * @param time
     * @return
     */
    public static int getHour(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).getHour();
    }

    /**
     * 获取系统当前小时 0 to 23
     *
     * @return
     */
    public static int getHour() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault()).getHour();
    }

    /**
     * 获取传入毫秒的秒 0-59
     *
     * @param time
     * @return
     */
    public static int getSecond(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).getSecond();
    }

    /**
     * 获取系统当前秒 0-59
     *
     * @return
     */
    public static int getSecond() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault()).getSecond();
    }

    /**
     * 获取传入毫秒的当日分钟 0-59
     *
     * @param time
     * @return
     */
    public static int getMinute(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).getMinute();
    }

    /**
     * 获取系统当前分钟 0-59
     *
     * @return
     */
    public static int getMinute() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault()).getMinute();
    }

    /**
     * 获取传入毫秒的星期 1-7
     *
     * @param time
     * @return
     */
    public static int getDayOfWeek(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).getDayOfWeek().getValue();
    }

    /**
     * 获取年
     *
     * @param time
     * @return
     */
    public static int getYear(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).getYear();
    }

    public static int getYear() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault()).getYear();
    }

    /**
     * 获取系统当前的星期 1-7
     *
     * @return
     */
    public static int getDayOfWeek() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault()).getDayOfWeek().getValue();
    }

    /**
     * 获取传入毫秒的天 1 to 365, or 366
     *
     * @param time
     * @return
     */
    public static int getDayOfYear(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).getDayOfYear();
    }

    /**
     * 获取系统当前的天 1 to 365, or 366
     *
     * @return
     */
    public static int getDayOfYear() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault()).getDayOfYear();
    }

    /**@
     * 返回当周第一天格式
     *
     * @param formatter
     * @return
     */
    public static String getNowWeekMondayFormat(DateTimeFormatter formatter) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault());
        ldt = LocalDateTime.of(ldt.minusDays(ldt.getDayOfWeek().getValue() - 1).toLocalDate(), LocalTime.MIN);
        return ldt.format(formatter);
    }

    /**
     * 返回当月第一天格式
     *
     * @param formatter
     * @return
     */
    public static String getNowMonthFirstDayFormat(DateTimeFormatter formatter) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault());
        ldt = LocalDateTime.of(ldt.minusDays(ldt.getDayOfMonth() - 1).toLocalDate(), LocalTime.MIN);
        return ldt.format(formatter);
    }

    /**
     * 返回当年第一天格式
     *
     * @param formatter
     * @return
     */
    public static String getNowYearFirstDayFormat(DateTimeFormatter formatter) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault());
        ldt = LocalDateTime.of(ldt.minusDays(ldt.getDayOfYear() - 1).toLocalDate(), LocalTime.MIN);
        return ldt.format(formatter);
    }

    /**@
     * 判断两个时间是否在同一天
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameDay(long time1, long time2) {
        return Duration.ofMillis(time1).toDays() - Duration.ofMillis(time2).toDays() == 0;
    }

    /**
     * 判断两个时间是否在同一周(注意这里周日和周一判断是在一周里的)
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameWeek(long time1, long time2) {
        LocalDateTime ldt1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(time1), ZoneId.systemDefault());
        LocalDateTime ldt2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(time2), ZoneId.systemDefault());
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return ldt1.getYear() == ldt2.getYear() && ldt1.get(woy) == ldt2.get(woy);
    }

    /**@
     * 判断两个时间是否在同一月
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameMonth(long time1, long time2) {
        LocalDateTime ldt1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(time1), ZoneId.systemDefault());
        LocalDateTime ldt2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(time2), ZoneId.systemDefault());
        return ldt1.getYear() == ldt2.getYear() && ldt1.getMonthValue() == ldt2.getMonthValue();
    }

    /**@
     * 判断两个时间是否在同一季度
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameQuarter(long time1, long time2) {
        LocalDateTime ldt1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(time1), ZoneId.systemDefault());
        LocalDateTime ldt2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(time2), ZoneId.systemDefault());
        return ldt1.getYear() == ldt2.getYear() && ldt1.getMonthValue() / 4 == ldt2.getMonthValue() / 4;
    }

    /**@
     * 判断两个时间是否在同一年
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameYear(long time1, long time2) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time1), ZoneId.systemDefault()).getYear()
                == LocalDateTime.ofInstant(Instant.ofEpochMilli(time2), ZoneId.systemDefault()).getYear();
    }

    public static boolean isToDay(long time) {
        return isSameDay(time, currentTimeMillis());
    }

    /**@
     * 传入时间与当前时间的day差值
     *
     * @param time
     * @return
     */
    public static long dayOffsetNow(long time) {
        return Duration.ofMillis(currentTimeMillis()).toDays() - Duration.ofMillis(time).toDays();
    }

    /**@
     * 传入时间之间的day差值
     *
     * @param time1
     * @param time2
     * @return
     */
    public static long dayOffset(long time1, long time2) {
        return Duration.ofMillis(time1).toDays() - Duration.ofMillis(time2).toDays();
    }

    /**
     * @return 获取当前纪元毫秒 1970-01-01T00:00:00Z.
     */
    public static long currentTimeMillis() {
        return Clock.systemDefaultZone().instant().toEpochMilli() + timeOffset;
    }

    public static long offsetCurrentTimeMillis(int offsetDays, int hour, int minute, int secord) {
        ZonedDateTime ldt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault());
        if (offsetDays > 0) {
            ldt = ldt.plusDays(offsetDays);
        } else if (offsetDays < 0) {
            ldt = ldt.minusDays(offsetDays);
        }
        ldt = ldt.withHour(hour);
        ldt = ldt.withMinute(minute);
        ldt = ldt.withSecond(secord);
        return ldt.toEpochSecond() * 1000;
    }

    /**
     * @return 获取当前纪元秒 1970-01-01T00:00:00Z.
     */
    public static long epochSecond() {
        return Clock.systemDefaultZone().instant().getEpochSecond() + timeOffset / 1000;
    }

    /**
     * 设置当前系统时间
     *
     * @param datetime
     * @return
     */
    public static boolean setCurrentDateTime(String datetime) {
        ZonedDateTime zdt = getZonedDateTime(datetime, YYYYMMDDHHMMSS);
        if (zdt == null) {
            zdt = getZonedDateTime(datetime, YYYYMMDDHHMM);
        }
        if (zdt == null) {
            zdt = getZonedDateTime(datetime, YYYYMMDDHH);
        }
        if (zdt == null) {
            zdt = getZonedDateTime(datetime, YYYYMMDD);
        }
        if (zdt == null) {
            return false;
        }
        timeOffset = zdt.toEpochSecond() * 1000 - Clock.systemDefaultZone().instant().toEpochMilli();
        return true;
    }

    public static ZonedDateTime getZonedDateTime(String datetime) {
        ZonedDateTime zdt = getZonedDateTime(datetime, YYYYMMDDHHMMSS);
        if (zdt == null) {
            zdt = getZonedDateTime(datetime, YYYYMMDDHHMM);
        }
        if (zdt == null) {
            zdt = getZonedDateTime(datetime, YYYYMMDDHH);
        }
        if (zdt == null) {
            zdt = getZonedDateTime(datetime, YYYYMMDD);
        }
        return zdt;
    }

    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofTotalSeconds(Calendar.getInstance().getTimeZone().getRawOffset() / 1000);

    /**
     * 获取一个带时间偏移量和时区的LocalDate
     *
     * @return
     */
    public static LocalDate getLocalDate() {
        long currentTimeMillis = currentTimeMillis();
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(currentTimeMillis / 1000, 0, ZONE_OFFSET);
        return ldt.toLocalDate();
    }

    /**
     * 获取一个带时间偏移量和时区的LocalDateTime
     *
     * @return
     */
    public static LocalDateTime getLocalDateTime() {
        long currentTimeMillis = currentTimeMillis();
        return LocalDateTime.ofEpochSecond(currentTimeMillis / 1000, 0, ZONE_OFFSET);
    }

    public static long getDayOfSecond() {
        LocalDateTime localDateTime = getLocalDateTime();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        long second = localDateTime.getSecond();
        second = hour * 60 * 60 + minute * 60 + second;
        return second;
    }

    public static long getDayOfMinute() {
        LocalDateTime localDateTime = getLocalDateTime();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        minute = hour * 60 + minute;
        return minute;
    }

    /**
     * 根据传入的年月日时分秒 返回一个毫秒数
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minte
     * @param second
     * @return
     */
    public static long getTimeByLocal(int year, int month, int day, int hour, int minte, int second) {
        return LocalDateTime.of(year, month, month, hour, minte, second).atZone(ZONE_OFFSET).toInstant().toEpochMilli();
    }

    /**
     * 根据传入的描述获取该时刻所在日的秒数
     *
     * @param currentTimeMillis
     * @return
     */
    public static int getSecondByDay(long currentTimeMillis) {
        int hour = getHour(currentTimeMillis);
        int minute = getMinute(currentTimeMillis);
        int second = getSecond(currentTimeMillis);
        return hour * 3600 + minute * 60 + second;
    }

    public static int getSecondByDay() {
        int hour = getHour();
        int minute = getMinute();
        int second = getSecond();
        return hour * 3600 + minute * 60 + second;
    }
    
    /** @return The current value of the system timer, in nanoseconds. */
	public static long nanoTime () {
		return System.nanoTime();
	}
}
