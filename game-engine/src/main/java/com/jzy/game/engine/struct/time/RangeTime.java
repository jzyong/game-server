package com.jzy.game.engine.struct.time;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.engine.util.SymbolUtil;


/**
 * 年 月 日 周 秒(秒通过时*3600+分*60+秒得到) [][][3,22][1-3][0-3600,28800-32400];[2017][1][3][2][28800-32400]
 * @author JiangZhiYong
 * @QQ 359135103
 * 2017年8月2日 上午9:52:15
 */
public class RangeTime {
    private static final Logger LOGGER=LoggerFactory.getLogger(RangeTime.class);

    //年[]表示所有值,[2015,2016]表示指定年
    private RangeReg[] year;

    private RangeReg[] month;
    private RangeReg[] day;
    private RangeReg[] week;
    private RangeReg[] second;

    private transient String[] times;    //配置的时间字符串
    //时间匹配分组
    private static final Pattern pattern = Pattern.compile("\\[.*?\\]");

    public RangeTime(String timeReg) {
        if (SymbolUtil.isNullOrEmpty(timeReg)) {
            throw new RuntimeException(String.format("配置问题：%s 时间字符串未空", timeReg));
        }
        times = timeReg.split(SymbolUtil.FENHAO_REG);

        year = new RangeReg[times.length];
        month = new RangeReg[times.length];
        day = new RangeReg[times.length];
        week = new RangeReg[times.length];
        second = new RangeReg[times.length];
        for (int i = 0; i < times.length; i++) {
            String time = times[i].trim();
            Matcher matcher = pattern.matcher(time);
            int j = 0;
            while (matcher.find() && j < 5) {
                switch (j) {
                    case 0:  //年
                        year[i] = new RangeReg(matcher.group());
                        break;
                    case 1:
                        month[i] = new RangeReg(matcher.group());
                        break;
                    case 2:
                        day[i] = new RangeReg(matcher.group());
                        break;
                    case 3:
                        week[i] = new RangeReg(matcher.group());
                        break;
                    case 4:
                        second[i] = new RangeReg(matcher.group());
                        break;

                }
                j++;
            }
            if (j < 5) {
                throw new RuntimeException(String.format("配置问题：%s 时间字符格式错误", timeReg));
            }
        }

    }

    @Override
    public String toString() {
        return checkConfigTimeStr();
    }

    /**
     * 获取倒计时，当前时间内，距离结束倒计时
     */
    public long getDataEndTime() {
        return getDataEndTime(Calendar.getInstance());
    }

    /**
     * 获取倒计时，当前时间内，距离结束倒计时
     * <br>
     */
    public long getDataEndTime(Calendar calendar) {
        long remain = 0;
        String timeStr = checkConfigTimeStr();
        try {
            
            if (timeStr == null) {
                return 0;
            }
            RangeTime rangeTime = new RangeTime(timeStr);
            Calendar calendar1 = Calendar.getInstance();

            int y = Integer.parseInt(rangeTime.year[0].getRange());
            int m = Integer.parseInt(rangeTime.month[0].getRange());
            int d = Integer.parseInt(rangeTime.day[0].getRange());
            int endSeconds = Integer.parseInt(rangeTime.second[0].getRange().split("-")[1]);

            calendar1.set(y, m - 1, d, getDayOfHour(endSeconds), getHourOfMinute(endSeconds), getMinuteOfSecond(endSeconds));
            remain = calendar1.getTimeInMillis() - calendar.getTimeInMillis();
            remain = remain < 0 ? 0 : remain;
        } catch (Exception e) {
            LOGGER.error(String.format("时间格式%s错误", timeStr),e);
        }
        return remain;
    }

    /**
     * @return 当前时间是否在范围内
     */
    public boolean isConfigTime() {
        return checkConfigTimeStr() != null;
    }

    /**
     * 验证当前时间是否在范围内
     *
     * @return 满足条件的时间字符串
     */
    public String checkConfigTimeStr() {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < year.length; i++) {
            //年
            String yearStr = getDateStr(calendar.get(Calendar.YEAR), year[i].getRange());
            if (yearStr != null) {
                //月
                String monthStr;
                if ((monthStr = getDateStr(calendar.get(Calendar.MONTH) + 1, month[i].getRange())) != null) {
                    //星期
                    int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    if (w < 1) {     //星期天
                        w = 7;
                    }
                    String weekStr;
                    if ((weekStr = getDateStr(w, week[i].getRange())) != null) {
                        //日
                        String dayStr;
                        if ((dayStr = getDateStr(calendar.get(Calendar.DATE), day[i].getRange())) != null) {
                            //时间
                            String timeStr;
                            if ((timeStr = getTimeStr(calendar, second[i].getRange())) != null) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("[").append(yearStr).append("][")
                                        .append(monthStr).append("][")
                                        .append(dayStr).append("][")
                                        .append(weekStr).append("][")
                                        .append(timeStr).append("]");
                                return sb.toString();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 验证当前时间是否在指定范围内
     *
     * @param timeStr 时间范围字符串
     * @return 满足条件的时间字符串
     */
    public String checkConfigTimeStr(String timeStr) {
        return checkConfigTimeStr(Calendar.getInstance(), timeStr);
    }

    /**
     * 验证指定的时间是否在范围内
     *
     * @param calendar 指定时间
     * @param timeStr 时间范围字符串
     * @return 满足条件的时间字符串
     */
    private String checkConfigTimeStr(Calendar calendar, String timeStr) {
        if (SymbolUtil.isNullOrEmpty(timeStr)) {
            return null;
        }
        Matcher matcher = pattern.matcher(timeStr);
        if (!matcher.find() || matcher.groupCount() < 5) {
            return null;
        }
        //年
        if (checkDate(calendar.get(Calendar.YEAR), matcher.group(0))) {
            //月
            if (checkDate(calendar.get(Calendar.MONTH) + 1, matcher.group(1))) {
                //星期
                int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                if (w < 1) {     //星期天
                    w = 7;
                }
                if (checkDate(w, matcher.group(3))) {
                    //日
                    if (checkDate(calendar.get(Calendar.DATE), matcher.group(2))) {
                        //时间
                        if (checkTime(calendar, matcher.group(4))) {
                            return timeStr;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 验证当前时间 年，月，日，星期，是否符合
     *
     * @param nowItem 参数
     * @param items 1-7;表示 1 到 7; 1,7 表示 1 或者 7
     * @return
     */
    private boolean checkDate(int nowItem, String items) {
        return getDateStr(nowItem, items) != null;
    }

    /**
     * 获取满足当前时间 年，月，日，星期
     *
     * @param nowItem 参数
     * @param items 1-7;表示 1 到 7; 1,7 表示 1 或者 7
     * @return 返回满足条件的字符串 null不符合
     */
    private String getDateStr(int nowItem, String items) {
        String nowItemStr = String.valueOf(nowItem);
        if ("".equals(items.trim()) || nowItemStr.equals(items)) {
            return nowItemStr;
        } else if (items.indexOf("-") > 0) {//区间划分
            String[] itemsplit = items.split("-");
            int item1 = Integer.parseInt(itemsplit[0]);
            int item2 = Integer.parseInt(itemsplit[1]);
            if (item1 <= nowItem && nowItem <= item2) {
                return nowItemStr;
            }
        } else if (items.indexOf(",") > 0) {//或划分
            String[] weekssplit = items.split(SymbolUtil.DOUHAO_REG);
            for (String item : weekssplit) {
                if (nowItemStr.equals(item)) {
                    return nowItemStr;
                }
            }
        }
        return null;
    }

    /**
     * 验证当前时间格式
     *
     * @param date
     * @param itemTime 秒通过时*3600+分*60+秒得到 0-3600,28800-32400
     * @return
     */
    boolean checkTime(Calendar date, String itemTime) {
        return getTimeStr(date, itemTime) != null;
    }

    /**
     * 验证当前时间格式
     *
     * @param date
     * @param itemTime 秒通过时*3600+分*60+秒得到 0-3600,28800-32400
     * @return
     */
    String getTimeStr(Calendar date, String itemTime) {
        String ret = null;
        //全天24小时
        if (!"0-86400".equals(itemTime) && !"".equals(itemTime.trim())) {
            String[] items = itemTime.split(SymbolUtil.DOUHAO_REG);

            for (String item : items) {
                String[] itemTimes = item.split("-");
                int daySecond = Integer.parseInt(itemTimes[0]);
                int hour = getDayOfHour(daySecond);
                int minute = getHourOfMinute(daySecond);
                int s = getMinuteOfSecond(daySecond);

                //开始时间
                if (date.get(Calendar.HOUR_OF_DAY) > hour //小时
                        || (date.get(Calendar.HOUR_OF_DAY) == hour && date.get(Calendar.MINUTE) > minute) //分钟
                        || (minute == date.get(Calendar.MINUTE) && date.get(Calendar.SECOND) >= s)) //秒 
                {
                    daySecond = Integer.parseInt(itemTimes[1]);
                    hour = getDayOfHour(daySecond);
                    minute = getHourOfMinute(daySecond);
                    s = getMinuteOfSecond(daySecond);
                    if ("86400".equals(itemTimes[1]) || date.get(Calendar.HOUR_OF_DAY) < hour
                            || (date.get(Calendar.HOUR_OF_DAY) == hour && date.get(Calendar.MINUTE) < minute)
                            || (date.get(Calendar.MINUTE) == minute && date.get(Calendar.SECOND) <= s)) {
                        ret = item;
                        break;
                    }
                }
            }
        } else {
            ret = "0-86400";
        }
        return ret;
    }

    /**
     * 获取当天的小时
     *
     * @param second 一天中的秒数
     */
    private int getDayOfHour(int second) {
        return second / 3600;
    }

    /**
     * 获取当前小时的分钟
     *
     * @param second 一天中的秒数
     */
    private int getHourOfMinute(int second) {
        return second / 60 % 60;
    }

    /**
     * 获取当前分钟的秒数
     *
     * @param second 一天中的秒数
     */
    private int getMinuteOfSecond(int second) {
        return second % 60;
    }

    public RangeReg[] getYear() {
        return year;
    }

    public RangeReg[] getMonth() {
        return month;
    }

    public RangeReg[] getDay() {
        return day;
    }

    public RangeReg[] getWeek() {
        return week;
    }

    public RangeReg[] getSecond() {
        return second;
    }

}
