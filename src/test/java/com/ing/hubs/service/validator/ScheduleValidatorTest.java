package com.ing.hubs.service.validator;

import com.ing.hubs.dto.ScheduleDto;
import com.ing.hubs.entity.Course;
import com.ing.hubs.entity.Schedule;
import com.ing.hubs.entity.WeekDay;
import com.ing.hubs.exception.InvalidScheduleException;
import com.ing.hubs.exception.ScheduleOverlapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduleValidatorTest {
    private ScheduleValidator scheduleValidator;

    private static final LocalTime TIME = LocalTime.of(10, 0);

    @BeforeEach
    public void setup() {
        scheduleValidator = new ScheduleValidator();
    }

    @Nested
    class ValidateScheduleTest {

        @Test
        void validateSchedule_ValidSchedule() {
            ScheduleDto validSchedule = buildScheduleDto(
                    LocalDate.now(),
                    LocalDate.now(),
                    TIME,
                    TIME.plusHours(1));

            assertDoesNotThrow(() -> scheduleValidator.validateSchedule(validSchedule));
        }

        @Test
        void validateSchedule_InvalidStartDate() {
            ScheduleDto invalidSchedule = buildScheduleDto(
                    LocalDate.now().plusDays(1),
                    LocalDate.now(),
                    TIME,
                    TIME.plusHours(1));

            assertThrows(InvalidScheduleException.class, () ->
                    scheduleValidator.validateSchedule(invalidSchedule));
        }

        @Test
        void validateSchedule_v() {
            ScheduleDto invalidSchedule = buildScheduleDto(
                    LocalDate.now(),
                    LocalDate.now(),
                    TIME,
                    TIME.minusHours(1));

            assertThrows(InvalidScheduleException.class, () ->
                    scheduleValidator.validateSchedule(invalidSchedule));
        }

        @Test
        void validateSchedule_InvalidStartTime() {
            ScheduleDto invalidSchedule = buildScheduleDto(LocalDate.now(), LocalDate.now(), TIME.plusHours(1), TIME);

            assertThrows(InvalidScheduleException.class, () ->
                    scheduleValidator.validateSchedule(invalidSchedule));
        }

    }

    @Nested
    class ValidateNewSchedulesNotOverlappingCurrentSchedulesTests {

        @Test
        void noOverlap() {
            Schedule newSchedule = buildSchedule(LocalDate.now(), LocalDate.now(), TIME, TIME.plusHours(1), WeekDay.MON);
            Schedule existingSchedule = buildSchedule(LocalDate.now().plusDays(1), LocalDate.now().plusDays(1), TIME, TIME.plusHours(1), WeekDay.MON);
            List<Schedule> newSchedules = Collections.singletonList(newSchedule);
            List<Schedule> currentSchedules = Collections.singletonList(existingSchedule);

            assertDoesNotThrow(() ->
                    scheduleValidator.validateNewSchedulesNotOverlappingCurrentSchedules(newSchedules, currentSchedules));
        }

        @Test
        void withOverlap() {
            Schedule newSchedule = buildSchedule(LocalDate.now(), LocalDate.now(), TIME, TIME.plusHours(1), WeekDay.MON);
            Schedule existingSchedule = buildSchedule(LocalDate.now(), LocalDate.now(), TIME.plusMinutes(30), TIME.plusHours(2), WeekDay.MON);
            List<Schedule> newSchedules = Collections.singletonList(newSchedule);
            List<Schedule> currentSchedules = Collections.singletonList(existingSchedule);

            assertThrows(ScheduleOverlapException.class, () ->
                    scheduleValidator.validateNewSchedulesNotOverlappingCurrentSchedules(newSchedules, currentSchedules));
        }

        @Test
        void withDifferentWeekDay_noOverlap() {
            Schedule newSchedule = buildSchedule(LocalDate.now(), LocalDate.now(), TIME, TIME.plusHours(1), WeekDay.MON);
            Schedule existingSchedule = buildSchedule(LocalDate.now(), LocalDate.now(), TIME.plusMinutes(30), TIME.plusHours(2), WeekDay.WED);
            List<Schedule> newSchedules = Collections.singletonList(newSchedule);
            List<Schedule> currentSchedules = Collections.singletonList(existingSchedule);

            assertDoesNotThrow(() ->
                    scheduleValidator.validateNewSchedulesNotOverlappingCurrentSchedules(newSchedules, currentSchedules));
        }

    }

    private ScheduleDto buildScheduleDto(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return ScheduleDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private Schedule buildSchedule(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, WeekDay weekDay) {
        return Schedule.builder()
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .weekDay(weekDay)
                .course(new Course())
                .build();
    }
}