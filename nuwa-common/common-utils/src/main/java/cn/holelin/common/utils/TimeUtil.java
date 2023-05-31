package cn.holelin.common.utils;

/**
 * 时间操作类
 */

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeUtil {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM = "yyyy-MM";

    private TimeUtil() {
    }

    /**
     * Date to LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * LocalDateTime to Date
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate to Date
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date to LocalDate
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 使用默认模式 yyyy-MM-dd HH:mm:ss解析字符串转为LocalDateTime
     *
     * @param str
     * @return
     * @throws DateTimeParseException
     */
    public static LocalDateTime parse(String str) throws DateTimeParseException {
        return LocalDateTime.parse(str, DATETIME_FORMATTER);
    }

    /**
     * 计算日期区间
     * 以年为维度,startDate为2019,当前时间为2023 则返回2019 2020 2021 2022 2023
     *
     * @param startDateStr 开始日期字符串
     * @return 日期区间
     */
    public static List<String> calculateDateIntervalByYear(String startDateStr) {
        List<String> result = new ArrayList<>();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.parse(startDateStr, dateTimeFormatter);

        for (int year = startDate.getYear(); year <= endDate.getYear(); year++) {
            result.add(String.valueOf(year));
        }
        return result;
    }

    /**
     * 计算日期区间
     * 以月为维度,startDate为2023-01,当前时间为2023-03 则返回2023-01 2023-02 2023-03
     *
     * @param startDateStr 开始日期字符串
     * @return 日期区间
     */
    public static List<String> calculateDateIntervalByMonth(String startDateStr) {
        List<String> result = new ArrayList<>();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.parse(startDateStr, dateTimeFormatter);

        while (!startDate.isAfter(endDate)) {
            result.add(startDate.format(DateTimeFormatter.ofPattern(YYYY_MM)));
            startDate = startDate.plusMonths(1);
        }
        return result;
    }

    /**
     * 计算日期区间
     * 以天为维度,startDate为2023-01-01,当前时间为2023-01-04 则返回2023-01-01 2023-01-02 2023-01-03 2023-01-04
     *
     * @param startDateStr 开始日期字符串
     * @return 日期区间
     */
    public static List<String> calculateDateIntervalByDay(String startDateStr) {
        List<String> result = new ArrayList<>();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.parse(startDateStr, dateTimeFormatter);

        while (!startDate.isAfter(endDate)) {
            result.add(startDate.format(dateTimeFormatter));
            startDate = startDate.plusDays(1);
        }
        return result;
    }
}

