package cn.holelin.common.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilTest {

    @Test
    void dateToLocalDateTime() {
        System.out.println(TimeUtil.dateToLocalDateTime(new Date()));
    }

    @Test
    void localDateTimeToDate() {
        System.out.println(TimeUtil.localDateTimeToDate(LocalDateTime.now()));
    }

    @Test
    void localDateToDate() {
        System.out.println(TimeUtil.localDateToDate(LocalDate.now()));
    }

    @Test
    void dateToLocalDate() {
        System.out.println(TimeUtil.dateToLocalDate(new Date()));
    }

    @Test
    void test(){
        LocalDateTime ldt = LocalDateTime.now(); //Local date time
        ZoneId zoneId = ZoneId.of( "Asia/Kolkata" );  //Zone information
        ZonedDateTime zdtAtAsia = ldt.atZone( zoneId );	//Local time in Asia timezone
        ZonedDateTime zdtAtET = zdtAtAsia
                .withZoneSameInstant( ZoneId.of( "America/New_York" ) ); //Sama time in ET timezone
    }
}