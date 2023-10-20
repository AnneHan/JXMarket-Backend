package com.hyl.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


/**
 * 日期工具类,
 * 继承org.apache.commons.lang.time.DateUtils类
 * @author AnneHan
 * @date 2023-09-15
 */
@SuppressWarnings("unused")
public class HylDateUtils extends org.apache.commons.lang3.time.DateUtils {


    private static final long ONE_MILLIS = 1000;
    private static final long ONE_MINUTE = 60;
    private static final long ONE_HOUR = 3600;
    private static final long ONE_DAY = 86400;
    private static final long ONE_MONTH = 2592000;
    private static final long ONE_YEAR = 31104000;


    private static final String[] PARSE_PATTERNS = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
            "yyyyMMdd", "yyyyMMddHHmmss", "yyyyMMddHHmm", "yyyyMM"};

    /**
     * 日期型字符串转化为日期 格式
     * {
     * "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
     * "yyyyMMdd", "yyyyMMddHHmmss", "yyyyMMddHHmm", "yyyyMM"}
     */
    public static Date parseDate(String str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str, PARSE_PATTERNS);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String formatCurrentDate() {
        return formatCurrentDate(PARSE_PATTERNS[0]);
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatCurrentDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, String pattern) {
        String formatDate;
        if (pattern != null) {
            formatDate = DateFormatUtils.format(date, pattern);
        } else {
            formatDate = DateFormatUtils.format(date, PARSE_PATTERNS[0]);
        }
        return formatDate;
    }


    /**
     * 获取当前时间戳（yyyyMMddHHmmss）
     *
     * @return nowTimeStamp
     */
    public static long getCurrentTimestamp() {
        return Long.parseLong(getCurrentTimestampStr());
    }

    /**
     * 获取当前时间戳（yyyyMMddHHmmss）
     */
    public static String getCurrentTimestampStr() {
        return formatDate(new Date(), PARSE_PATTERNS[13]);
    }

    /**
     * 获取Unix时间戳
     */
    public static long getCurrentUnixTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取Unix时间戳
     */
    public static String getCurrentUnixTimestampStr() {
        return String.valueOf(getCurrentUnixTimestamp());
    }

    /**
     * 转换Unix时间戳
     *
     * @return nowTimeStamp
     */
    public static long parseUnixTimeStamp(long time) {
        return time / ONE_MILLIS;
    }


    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String formatTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatCurrentDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String formatYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String formatMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String formatDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String formatWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 获取过去的天数
     */
    public static long getBeforeDays(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (ONE_DAY * ONE_MILLIS);
    }

    /**
     * 获取过去的小时
     */
    public static long getBeforeHours(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (ONE_HOUR * ONE_MILLIS);
    }

    /**
     * 获取过去的分钟
     */
    public static long getBeforeMinutes(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (ONE_MINUTE * ONE_MILLIS);
    }

    /**
     * 获取过去的秒
     */
    public static long getBeforeSeconds(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / ONE_MILLIS;
    }

    /**
     * 获取两个日期之间的天数
     */
    public static double getDays(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return Math.abs((afterTime - beforeTime) / (ONE_MILLIS * ONE_DAY));
    }


    /**
     * 距离今天多久
     */
    public static String formatTextFromToday(Date createAt) {
        // 定义最终返回的结果字符串。
        String interval;
        if (createAt == null) {
            return "";
        }
        long millisecond = System.currentTimeMillis() - createAt.getTime();

        long second = millisecond / ONE_MILLIS;

        if (second <= 0) {
            second = 0;
        }
        if (second == 0) {
            interval = "刚刚";
        } else if (second < ONE_MINUTE / 2) {
            interval = second + "秒以前";
        } else if (second < ONE_MINUTE) {
            interval = "半分钟前";
        } else if (second < ONE_MINUTE * ONE_MINUTE) {
            //大于1分钟 小于1小时
            long minute = second / ONE_MINUTE;
            interval = minute + "分钟前";
        } else if (second < ONE_DAY) {
            //大于1小时 小于24小时
            long hour = (second / ONE_MINUTE) / ONE_MINUTE;
            interval = hour + "小时前";
        } else if (second <= ONE_DAY * 2) {
            //大于1D 小于2D
            interval = "昨天" + formatDate(createAt, "HH:mm");
        } else if (second <= ONE_DAY * 7) {
            //大于2D小时 小于 7天
            long day = ((second / ONE_MINUTE) / ONE_MINUTE) / 24;
            interval = day + "天前";
        } else if (second <= ONE_DAY * 365) {
            //大于7天小于365天
            interval = formatDate(createAt, "MM-dd HH:mm");
        } else {
            //大于365天
            interval = formatDate(createAt, "yyyy-MM-dd HH:mm");
        }
        return interval;
    }


    /**
     * 距离截止日期还有多长时间
     */
    public static String formatTextFromDeadline(Date date) {
        long deadline = date.getTime() / ONE_MILLIS;
        long now = (System.currentTimeMillis()) / ONE_MILLIS;
        long remain = deadline - now;
        if (remain <= ONE_HOUR) {
            return "只剩下" + remain / ONE_MINUTE + "分钟";
        } else if (remain <= ONE_DAY) {
            return "只剩下" + remain / ONE_HOUR + "小时"
                    + (remain % ONE_HOUR / ONE_MINUTE) + "分钟";
        } else {
            long day = remain / ONE_DAY;
            long hour = remain % ONE_DAY / ONE_HOUR;
            long minute = remain % ONE_DAY % ONE_HOUR / ONE_MINUTE;
            return "只剩下" + day + "天" + hour + "小时" + minute + "分钟";
        }

    }


    /**
     * Unix时间戳转换成指定格式日期字符串
     *
     * @param timestampString 时间戳 如："1473048265";
     * @param pattern         要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
     * @return 返回结果 如："2016-09-05 16:06:42";
     */
    public static String unixTimeStamp2Date(String timestampString, String pattern) {
        if (HylStringUtils.isBlank(pattern)) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        long timestamp = Long.parseLong(timestampString) * ONE_MINUTE;
        return new SimpleDateFormat(pattern, Locale.CHINA).format(new Date(timestamp));
    }

    /**
     * 日期格式字符串转换成Unix时间戳
     */
    public static String date2UnixTimeStamp(String dateStr, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return String.valueOf(sdf.parse(dateStr).getTime() / ONE_MINUTE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 计算接口处理时间
     *
     * @param time1 source1
     * @param time2 source2
     * @return 时间差format
     */
    public static String getDistanceTime(long time1, long time2) {
        long day;
        long hour;
        long min;
        long sec;
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0) {
            return day + "天" + hour + "小时" + min + "分钟" + sec + "秒";
        }
        if (hour != 0) {
            return hour + "小时" + min + "分钟" + sec + "秒";
        }
        if (min != 0) {
            return min + "分钟" + sec + "秒";
        }
        if (sec != 0) {
            return sec + "秒";
        }
        return "0秒";
    }


    /**
     * 获取指定天后的日期
     *
     * @return 获取第n天日期
     */
    public static Date getNextForDay(long day) {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime nextTime = localDateTime.plusDays(day);
        return localDateTime2Date(nextTime);
    }

    /**
     * 获取指定天后的日期
     *
     * @return 获取第n天日期
     */
    public static Date getPreForDay(long day) {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime nextTime = localDateTime.minusDays(day);
        return localDateTime2Date(nextTime);
    }

    /**
     * localDateTime 转成 date
     *
     * @param localDateTime localDatetime
     * @return date
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    /**
     * Date to LocalDateTime
     */
    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 时间戳格式化日期字符串
     *
     * @param timestamp 时间戳
     * @param pattern   格式
     * @return 格式化时间
     */
    public static String timestampToPatterString(Long timestamp, String pattern) {
        if (null != timestamp) {
            if (String.valueOf(timestamp).length() >= 13) {
                timestamp = timestamp / 1000;
            }
            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(8));
            DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
            return df.format(localDateTime);
        }
        return "";
    }

    /**
     * 判断是否是日期格式字符串
     *
     * @param str dateStr
     * @return boolean
     */
    public static boolean isDateStr(String str) {
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDateTime = LocalDate.parse(str, df);
            return localDateTime.getYear() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
