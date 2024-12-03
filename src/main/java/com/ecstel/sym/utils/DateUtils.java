package com.ecstel.sym.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
    public static String convertUTCDate(String YYYYMMDD) {
        try {
            // Convert YYYYMMDD string to LocalDate
            LocalDate date = convertDateYMD(YYYYMMDD);

            // Assume the time is at the start of the day (00:00:00)
            LocalDateTime localDateTime = date.atStartOfDay();

            // Convert from Seoul time (Asia/Seoul) to UTC
            ZonedDateTime seoulTime = localDateTime.atZone(ZoneId.of("Asia/Seoul"));
            ZonedDateTime utcTime = seoulTime.withZoneSameInstant(ZoneId.of("UTC"));

            // Return the formatted UTC time with milliseconds and 'Z' at the end
            return utcTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }
    }



    public static LocalDate convertDateYMD(String dateString) {
        // Check if the input matches the YYYYMMDD format
        if (!dateString.matches("\\d{8}")) {
            throw new IllegalArgumentException("Invalid date format. Please use YYYYMMDD format.");
        }

        try {
            // Define the formatter for YYYYMMDD
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            // Parse the date string into a LocalDate
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error parsing the date.", e);
        }
    }

    public static long calculateDaysDifference(String ymd) {
        // yyyyMMdd 형식의 입력 날짜를 LocalDate로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate inputDate = LocalDate.parse(ymd, formatter);

        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        // 날짜 차이를 계산 (days 단위)
        return ChronoUnit.DAYS.between(today, inputDate);
    }

    public static String CurrentDateYMD(int nextDay) {
        // Get the current date
        LocalDate currentDate = LocalDate.now().plusDays(nextDay);

        // Define the formatter for YYYYMMDD
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // Format the current date
        return currentDate.format(formatter);
    }

    public static String formatToTimestamp(String isoDateStr) {
        // Parse the ISO 8601 date string
        LocalDateTime dateTime = LocalDateTime.parse(isoDateStr, DateTimeFormatter.ISO_DATE_TIME);

        // Add 9 hours
        dateTime = dateTime.plusHours(9);

        // Format to "yyyy-MM-dd HH:mm:ss.SSSSSS"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        return dateTime.format(formatter);
    }

    public static String formatDate(String dateStr) {
        try {
            // 문자열을 LocalDateTime 객체로 파싱 (기본적으로 00:00:00.000을 설정)
            LocalDateTime date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);

            // 원하는 형식으로 포맷 (YYYY-MM-DD 00:00:00.000)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            return date.format(formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }
    }

    public static int getDayOfWeek(String yyyyMMdd) {
        // 날짜 형식 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 날짜 문자열을 LocalDate로 변환
        LocalDate date = LocalDate.parse(yyyyMMdd, formatter);

        // 요일을 숫자로 반환 (1 = 월요일, 7 = 일요일)
        return date.getDayOfWeek().getValue();
    }

    public static String formatToTimestamp6Plus9Hours(String isoDateStr) {
        try {
            // 문자열을 LocalDateTime 객체로 파싱 (ISO 형식)
            LocalDateTime date = LocalDateTime.parse(isoDateStr, DateTimeFormatter.ISO_DATE_TIME);

            // UTC 시간에 9시간을 더해 서울 시간으로 변환
            LocalDateTime seoulTime = date.plusHours(9);

            // 포맷터를 이용하여 원하는 형식으로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            return seoulTime.format(formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }
    }

    public static LocalDateTime formatToTimestampInterval(String dateTimeStr) {
        // ISO 8601 문자열을 LocalDateTime으로 변환
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

        // 9시간 추가
        //dateTime = dateTime.plusHours(9);

        // 15분 단위로 내림
        int minutes = dateTime.getMinute();
        int roundedMinutes = (minutes / 15) * 15;

        // 분, 초, 나노초 설정
        dateTime = dateTime.withMinute(roundedMinutes).withSecond(0).withNano(0);

        return dateTime;
    }

    public static int ConverToSecondFromPTS(String isoDuration) {
        int totalSeconds = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        // 정규식을 사용하여 각 시간 단위 추출
        Pattern hoursPattern = Pattern.compile("(\\d+)H");
        Pattern minutesPattern = Pattern.compile("(\\d+)M");
        Pattern secondsPattern = Pattern.compile("(\\d+)(\\.\\d+)?S"); // 초와 소수점 이하도 추출

        // 시간 추출
        Matcher hoursMatcher = hoursPattern.matcher(isoDuration);
        if (hoursMatcher.find()) {
            hours = Integer.parseInt(hoursMatcher.group(1));
        }

        // 분 추출
        Matcher minutesMatcher = minutesPattern.matcher(isoDuration);
        if (minutesMatcher.find()) {
            minutes = Integer.parseInt(minutesMatcher.group(1));
        }

        // 초와 소수점 이하 추출
        Matcher secondsMatcher = secondsPattern.matcher(isoDuration);
        if (secondsMatcher.find()) {
            seconds = Integer.parseInt(secondsMatcher.group(1)); // 소수점 이하는 무시하고 정수만 처리
        }

        // 총 초 단위로 변환
        totalSeconds = hours * 3600 + minutes * 60 + seconds;

        return totalSeconds;

    }

    public static long calculateTimeDifferenceInSeconds(String stateChangedTime) {
        // Define the formatter for parsing the input timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        // Parse the input timestamp into a LocalDateTime object
        LocalDateTime stateChangedDateTime = LocalDateTime.parse(stateChangedTime, formatter);

        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        // Calculate the difference in seconds
        Duration duration = Duration.between(stateChangedDateTime, now);
        return duration.getSeconds();
    }

    public static List<Integer> calculateTimeInIntervals(String dateTime, LocalDateTime currentStartDateTime2, int duration, int extraInserts) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime startDateTime = LocalDateTime.parse(dateTime, formatter);

        LocalDateTime intervalStartDateTime = currentStartDateTime2;

        List<Integer> intervals = new ArrayList<>();
        int remainingDuration = duration;

        // 첫 번째 구간의 시간 차이를 정확히 계산
        if (startDateTime.isBefore(intervalStartDateTime)) {
            // 첫 15분 구간의 끝 시간을 계산
            LocalDateTime firstIntervalEnd = intervalStartDateTime.withSecond(0).withNano(0);
            long firstDuration = Math.min(Duration.between(startDateTime, firstIntervalEnd).getSeconds(), remainingDuration);

            // 첫 구간 초 계산
            intervals.add((int) firstDuration);
            remainingDuration -= firstDuration;

            // 다음 구간의 시작 시간 설정
            intervalStartDateTime = intervalStartDateTime.plusMinutes(15);
        }

        // 남은 구간을 15분 단위로 초를 계산하여 배열에 추가
        while (remainingDuration > 0) {
            // 15분 간격으로 구간 끝 시간 계산
            LocalDateTime nextIntervalEnd = intervalStartDateTime.plusMinutes(15);
            long intervalDuration = Math.min(Duration.between(intervalStartDateTime, nextIntervalEnd).getSeconds(), remainingDuration);
            intervals.add((int) intervalDuration);

            // 구간을 15분 뒤로 설정
            intervalStartDateTime = intervalStartDateTime.plusMinutes(15);
            remainingDuration -= intervalDuration;
        }

        return intervals;
    }
}
