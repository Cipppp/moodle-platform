package com.ing.hubs.service;

import com.ing.hubs.dto.ScheduleDto;
import com.ing.hubs.dto.ScheduleResponseDto;
import com.ing.hubs.dto.ScheduleUpdateDto;
import com.ing.hubs.entity.Course;
import com.ing.hubs.entity.Schedule;
import com.ing.hubs.exception.EnrollmentRequestAlreadySentException;
import com.ing.hubs.exception.ScheduleNotFoundException;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.EnrollmentRequestRepository;
import com.ing.hubs.repository.ScheduleRepository;
import com.ing.hubs.service.validator.ScheduleValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.List.of;
import static java.util.Optional.ofNullable;

@Service
@AllArgsConstructor
public class ScheduleService {
    private EnrollmentRequestRepository enrollmentRequestRepository;
    private ScheduleRepository scheduleRepository;
    private CourseService courseService;
    private Mapper mapper;
    private ScheduleValidator scheduleValidator;

    public List<ScheduleResponseDto> getAllSchedules() {
        return scheduleRepository.findAll()
                .stream()
                .map(schedule -> mapper.map(schedule, ScheduleResponseDto.class))
                .toList();
    }

    public ScheduleResponseDto createSchedule(ScheduleDto scheduleDto, Long courseId) {
        validateNoEnrollmentRequests(courseId);

        scheduleValidator.validateSchedule(scheduleDto);

        Course course = courseService.getCourseById(courseId);

        Schedule newSchedule = mapper.fromDto(scheduleDto, course);

        List<Schedule> currentSchedules = scheduleRepository.findByCourseId(newSchedule.getCourse().getId());
        scheduleValidator.validateNewSchedulesNotOverlappingCurrentSchedules(of(newSchedule), currentSchedules);

        return mapper.map(scheduleRepository.save(newSchedule), ScheduleResponseDto.class);
    }

    public List<ScheduleResponseDto> getSchedulesForCourse(Long courseId) {
        return scheduleRepository.findByCourseId(courseId)
                .stream()
                .map(schedule -> mapper.map(schedule, ScheduleResponseDto.class))
                .toList();
    }

    public List<ScheduleResponseDto> getSchedulesForStudent(Long studentId) {
        return scheduleRepository.findByStudentId(studentId)
                .stream()
                .map(schedule -> mapper.map(schedule, ScheduleResponseDto.class))
                .toList();
    }

    public ScheduleResponseDto updateSchedule(Long scheduleId, ScheduleUpdateDto scheduleUpdateDto) {
        Schedule schedule = getScheduleById(scheduleId);
        ofNullable(scheduleUpdateDto.getName()).filter(name -> !name.isEmpty()).ifPresent(schedule::setName);

        Course course = courseService.getCourseById(schedule.getCourse().getId());
        schedule.setCourse(course);

        return mapper.map(scheduleRepository.save(schedule), ScheduleResponseDto.class);
    }

    public Schedule getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(ScheduleNotFoundException::new);
    }

    private void validateNoEnrollmentRequests(Long courseId) {
        if (enrollmentRequestRepository.existsByCourseId(courseId)) {
            throw new EnrollmentRequestAlreadySentException();
        }
    }
}
