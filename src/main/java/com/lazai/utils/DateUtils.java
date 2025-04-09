package com.lazai.utils;


import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

/**
 * date utils
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    public static Date getNowDate() {
        return new Date();
    }

    /**
     * yyyy-MM-dd
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getCurrentUTCTime() {
        Instant instant = Instant.now();
        ZonedDateTime utc = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"));
        return Date.from(utc.toInstant());
    }

    public static long getCurrentUTCTimeStamp() {
        return getCurrentUTCTime().toInstant().toEpochMilli();
    }

    public static ZonedDateTime convertToUTC(LocalDateTime localDateTime) {
        ZonedDateTime localZonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return localZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
    }

    /**
     * yyyy/MM/dd
     */
    public static String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * yyyyMMdd
     */
    public static String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * parse date
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getYesterday() {
        return toDate(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public static Date getToday() {
        return toDate(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public static Date getStartOfToday(){
        return toDate(LocalDate.now().atStartOfDay());
    }

    public static Date getEndOfToday(){
        return toDate(LocalDate.now().atTime(LocalTime.MAX));
    }

    /**
     * get server start date
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * calculate time difference
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        long diff = endDate.getTime() - nowDate.getTime();
        long day = diff / nd;
        long hour = diff % nd / nh;
        long min = diff % nd % nh / nm;
        return day + "day" + hour + "hour" + min + "minute";
    }

    public static Date toDate(LocalDateTime temporalAccessor) {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    public static Date toDate(LocalDate temporalAccessor) {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        return toDate(localDateTime);
    }

    public static Date getAddDay(int day) {
        return toDate(LocalDate.now().plusDays(day).atStartOfDay(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public static Date getUTCTime(int year, int month, int day, int hour, int min, int sec) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, min, sec);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utc = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return Date.from(utc.toInstant());
    }

    public static Date getBeginOfWeek() {
        return toDate(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public static Date getEndOfWeek() {
        return toDate(LocalDate.now().with(DayOfWeek.SUNDAY).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public static Date getNextEndOfWeek() {
        return toDate(LocalDate.now().with(DayOfWeek.SUNDAY).plusWeeks(1).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

}
