package cn.holelin.test.jdk8;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.TimeZone;

import static java.time.temporal.TemporalAdjusters.*;

/**
 * @Description: JDK8新的日期和时间测试类
 * @Author: HoleLin
 * @CreateDate: 2023/4/1 15:56
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/4/1 15:56
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Slf4j
public class NewDateAndTimeTest {

    @Test
    public void testLocalDateAndLocalTime() {
        final LocalDate nowLocalDate = LocalDate.now();
        log.info("当前日期: " + nowLocalDate);
        final LocalDate date = LocalDate.of(2023, 4, 1);
        log.info("当前日期: " + date);
        final int year = date.getYear();
        final Month month = date.getMonth();
        final int dayOfYear = date.getDayOfYear();
        final int dayOfMonth = date.getDayOfMonth();
        final DayOfWeek dayOfWeek = date.getDayOfWeek();
        final int lengthOfMonth = date.lengthOfMonth();
        final int lengthOfYear = date.lengthOfYear();
        final boolean leapYear = date.isLeapYear();
        log.info("当前日期所属年份: {},月份:{},位于一年中的第几天:{},位于一个月中的第几天:{},位于一周中的周几:{}",
                year, month, dayOfYear, dayOfMonth, dayOfWeek);
        log.info("当前月份一共多少天:{},当前年一共多少天:{},是否是闰年:{}",
                lengthOfMonth, lengthOfYear, leapYear);

        final int year2 = date.get(ChronoField.YEAR);
        final int month2 = date.get(ChronoField.MONTH_OF_YEAR);
        final int day = date.get(ChronoField.DAY_OF_MONTH);
        log.info("通过TemporalField实现子类获取年月日:{},{},{}",
                year2, month2, day);

        final LocalTime nowLocalTime = LocalTime.now();
        final LocalTime time = LocalTime.of(16, 38, 5);
        final int hour = nowLocalTime.getHour();
        final int minute = nowLocalTime.getMinute();
        final int second = nowLocalTime.getSecond();
        log.info("当前时间:{},hour:{},minute:{},second:{}", nowLocalTime, hour, minute, second);

        // 通过parse解析
        final LocalDate parseLocalDate = LocalDate.parse("2023-04-01");
        final LocalTime parseLocalTime = LocalTime.parse("16:38:05");
        log.info("解析后的日期:{},解析后的时间:{}", parseLocalDate, parseLocalTime);
    }

    @Test
    public void testLocalDateTime() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDate date = LocalDate.now();
        final LocalTime time = LocalTime.now();
        LocalDateTime dt1 = LocalDateTime.of(2013, Month.MARCH, 18, 13, 45, 20);
        LocalDateTime dt2 = LocalDateTime.of(date, time);
        LocalDateTime dt3 = date.atTime(13, 45, 20);
        LocalDateTime dt4 = date.atTime(time);
        LocalDateTime dt5 = time.atDate(date);
        log.info("now: {}\ndt1: {}\ndt2: {}\ndt3: {}\ndt4: {}\ndt5: {}", now, dt1, dt2, dt3, dt4, dt5);
    }

    @Test
    public void testInstant() {
        final Instant instant = Instant.ofEpochSecond(3);
        final Instant instant2 = Instant.ofEpochSecond(3, 0);
        final Instant instant3 = Instant.ofEpochSecond(2, 1_000_000_000);
        final Instant now = Instant.now();
        log.info("now:{}", now);
        log.info("instant1: {},instant2: {},instant3: {}", instant, instant2, instant3);
        // 下面的是错误示例 会抛出 java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: DayOfMonth
        final int i = now.get(ChronoField.DAY_OF_MONTH);
    }

    @Test
    public void testDuration() {

        final Duration durationTime = Duration.between(LocalTime.parse("16:38:05"), LocalTime.parse("20:35:05"));
        final Duration durationInstant = Duration.between(Instant.ofEpochSecond(5), Instant.ofEpochSecond(3));
        log.info("durationTime:{},durationInstant:{}", durationTime, durationInstant);
        Duration threeMinutes = Duration.ofMinutes(3);
        Duration threeMinutes2 = Duration.of(3, ChronoUnit.MINUTES);
        Period tenDays = Period.ofDays(10);
        Period threeWeeks = Period.ofWeeks(3);
        Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);
        log.info("threeMinutes:{},threeMinutes2:{}", threeMinutes, threeMinutes2);
        log.info("tenDays:{},threeWeeks:{},twoYearsSixMonthsOneDay:{}", tenDays, threeWeeks, twoYearsSixMonthsOneDay);
    }


    @Test
    public void userDateTime() {

        // 使用withAttribute方法
        LocalDate date1 = LocalDate.of(2014, 3, 18);
        LocalDate date2 = date1.withYear(2011);
        LocalDate date3 = date2.withDayOfMonth(25);
        LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 9);
        log.info("d1:{},d2:{},d3:{},d4:{}", date1, date2, date3, date4);

        LocalDate date5 = LocalDate.of(2014, 3, 18);
        // 增加一周
        LocalDate date6 = date1.plusWeeks(1);
        // 减去三年
        LocalDate date7 = date2.minusYears(3);
        // 增加三个月
        LocalDate date8 = date3.plus(6, ChronoUnit.MONTHS);
        log.info("d5:{},d6:{},d7:{},d8:{}", date5, date6, date7, date8);
    }

    @Test
    public void testTemporalAdjuster() {
        LocalDate date1 = LocalDate.of(2023, 3, 18);
        LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));
        LocalDate date3 = date2.with(lastDayOfMonth());

        log.info("d1:{},d2:{},d3{}", date1, date2, date3);
    }

    @Test
    public void testDateFormat() {
        LocalDate date = LocalDate.of(2023, 3, 18);
        String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        log.info("s1:{}\ns2:{}", s1, s2);

        LocalDate date1 = LocalDate.parse("20230318",
                DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate date2 = LocalDate.parse("2023-03-18",
                DateTimeFormatter.ISO_LOCAL_DATE);
        log.info("s1:{}\ns2:{}", date1, date2);

        // 创建自定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date3 = LocalDate.of(2023, 3, 18);
        String formattedDate = date1.format(formatter);
        LocalDate date4 = LocalDate.parse(formattedDate, formatter);
        log.info("s1:{}\nformattedDate:{}\ns2:{}", date3, formattedDate, date4);

        // 通过构造器来创建时间格式
        DateTimeFormatter italianFormatter = new DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendText(ChronoField.YEAR)
                .parseCaseInsensitive()
                .toFormatter(Locale.ITALIAN);
    }

    @Test
    public void testZoneId() {
        ZoneId romeZone = ZoneId.of("Asia/Shanghai");
        // 老的API转换为新的API
        ZoneId zoneId = TimeZone.getDefault().toZoneId();

        LocalDate date = LocalDate.of(2023, Month.MARCH, 18);
        ZonedDateTime zdt1 = date.atStartOfDay(romeZone);
        LocalDateTime dateTime = LocalDateTime.of(2023, Month.MARCH, 18, 13, 45);
        ZonedDateTime zdt2 = dateTime.atZone(romeZone);
        Instant instant = Instant.now();
        ZonedDateTime zdt3 = instant.atZone(romeZone);
        log.info("z1:{},z2:{},z3:{}", zdt1, zdt2, zdt3);
    }

}
