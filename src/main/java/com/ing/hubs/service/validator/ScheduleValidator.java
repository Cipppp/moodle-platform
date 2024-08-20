package com.ing.hubs.service.validator;

import com.ing.hubs.dto.ScheduleDto;
import com.ing.hubs.entity.Schedule;
import com.ing.hubs.exception.InvalidScheduleException;
import com.ing.hubs.exception.ScheduleOverlapException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleValidator {

    public void validateSchedule(ScheduleDto scheduleDto) {
        if ((scheduleDto.getEndDate().isAfter(scheduleDto.getStartDate()) ||
                scheduleDto.getEndDate().isEqual(scheduleDto.getStartDate())) &&
                scheduleDto.getEndTime().isAfter(scheduleDto.getStartTime())) {
            return;
        }
        throw new InvalidScheduleException();
    }

    public void validateNewSchedulesNotOverlappingCurrentSchedules(List<Schedule> newSchedules, List<Schedule> currentSchedules) {
        var anyOverlap = newSchedules.stream().anyMatch(newSchedule ->
                currentSchedules.stream().anyMatch(currentSchedule ->
                        isScheduleOverlapped(newSchedule, currentSchedule)
                )
        );

        if (anyOverlap) {
            throw new ScheduleOverlapException();
        }
    }

    private boolean isScheduleOverlapped(Schedule newSchedule, Schedule currentSchedule) {
        if (newSchedule.getEndDate().isBefore(currentSchedule.getStartDate()) ||
                newSchedule.getStartDate().isAfter(currentSchedule.getEndDate())) {
            return false;
        }

        if (newSchedule.getWeekDay() != currentSchedule.getWeekDay()) {
            return false;
        }

        LocalTime currentStart = currentSchedule.getStartTime();
        LocalTime currentEnd = currentSchedule.getEndTime();
        LocalTime newStart = newSchedule.getStartTime();
        LocalTime newEnd = newSchedule.getEndTime();

        return !(currentStart.isAfter(newEnd) || currentEnd.isBefore(newStart));
    }

}
