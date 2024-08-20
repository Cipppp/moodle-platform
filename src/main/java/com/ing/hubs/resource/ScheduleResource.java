package com.ing.hubs.resource;

import com.ing.hubs.dto.ScheduleDto;
import com.ing.hubs.dto.ScheduleResponseDto;
import com.ing.hubs.dto.ScheduleUpdateDto;
import com.ing.hubs.entity.User;
import com.ing.hubs.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class ScheduleResource {
    private ScheduleService scheduleService;

    @PostMapping("/courses/{courseId}/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_TEACHER")
    @PreAuthorize("@permissionService.courseBelongsToUser(#courseId)")
    public ScheduleResponseDto createSchedule(@Valid @RequestBody ScheduleDto scheduleDto, @PathVariable Long courseId) {
        return scheduleService.createSchedule(scheduleDto, courseId);
    }

    @GetMapping("/schedules")
    public List<ScheduleResponseDto> getSchedules() {
        return scheduleService.getAllSchedules();
    }

    @PatchMapping("/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Secured("ROLE_TEACHER")
    @PreAuthorize("@permissionService.canUpdateSchedule(#scheduleId)")
    public ScheduleResponseDto updateSchedule(@PathVariable Long scheduleId, @Valid @RequestBody ScheduleUpdateDto scheduleUpdateDto) {
        return scheduleService.updateSchedule(scheduleId, scheduleUpdateDto);
    }

    @GetMapping("/courses/{courseId}/schedules")
    public List<ScheduleResponseDto> getSchedulesForCourse(@PathVariable Long courseId) {
        return scheduleService.getSchedulesForCourse(courseId);
    }

    @GetMapping("/students/me/schedules")
    @Secured("ROLE_STUDENT")
    public List<ScheduleResponseDto> getSchedulesForStudent() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return scheduleService.getSchedulesForStudent(user.getId());
    }
}
