package com.ing.hubs.service;

import com.ing.hubs.dto.CourseDto;
import com.ing.hubs.dto.CourseResponseDto;
import com.ing.hubs.dto.CourseUpdateDto;
import com.ing.hubs.entity.Course;
import com.ing.hubs.entity.Schedule;
import com.ing.hubs.entity.User;
import com.ing.hubs.exception.CourseNotFoundException;
import com.ing.hubs.exception.MaximumNumberOfAttendeesDecreaseException;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.CourseRepository;
import com.ing.hubs.repository.ScheduleRepository;
import com.ing.hubs.service.validator.ScheduleValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@Service
@AllArgsConstructor
public class CourseService {
    private CourseRepository courseRepository;
    private ScheduleRepository scheduleRepository;
    private Mapper mapper;
    private ScheduleValidator scheduleValidator;

    public CourseResponseDto createCourse(CourseDto courseDto) {
        courseDto.getSchedules().forEach(schedule -> scheduleValidator.validateSchedule(schedule));

        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Course course = mapper.fromDto(courseDto, teacher);

        List<Schedule> newSchedules = courseDto.getSchedules()
                .stream()
                .map(scheduleDto -> mapper.fromDto(scheduleDto, course))
                .toList();

        List<Schedule> currentTeacherSchedules = scheduleRepository.findByTeacherId(teacher.getId());
        validateNewSchedulesWithEachOther(newSchedules);
        scheduleValidator.validateNewSchedulesNotOverlappingCurrentSchedules(
                newSchedules, 
                currentTeacherSchedules);

        var savedCourse = courseRepository.save(course);
        scheduleRepository.saveAll(newSchedules);

        savedCourse.setSchedule(newSchedules);
        return mapper.toResponseDto(savedCourse);
    }

    public CourseResponseDto updateCourse(Long courseId, CourseUpdateDto courseUpdateDto) {
        Course course = getCourseById(courseId);
        ofNullable(courseUpdateDto.getName()).filter(name -> !name.isEmpty()).ifPresent(course::setName);
        ofNullable(courseUpdateDto.getDescription()).filter(desc -> !desc.isEmpty()).ifPresent(course::setDescription);
        ofNullable(courseUpdateDto.getMaxAttendees()).ifPresent(newMaxAttendees -> {
            if (courseUpdateDto.getMaxAttendees() <= course.getMaxAttendees()) {
                throw new MaximumNumberOfAttendeesDecreaseException();
            }
            course.setMaxAttendees(courseUpdateDto.getMaxAttendees());
        });

        return mapper.map(courseRepository.save(course), CourseResponseDto.class);
    }

    public List<CourseResponseDto> getAllCreatedCourses(String courseName) {
        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Course> courses = ofNullable(courseName)
                .map(name -> courseRepository.findByTeacherIdAndNameContaining(teacher.getId(), name))
                .orElse(courseRepository.findByTeacherId(teacher.getId()));

        return courses.stream().map(course -> mapper.map(course, CourseResponseDto.class)).toList();
    }

    public List<CourseResponseDto> getCoursesEnrolledTo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return courseRepository.findCoursesByStudentId(user.getId()).stream()
                .map(course -> mapper.toResponseDto(course))
                .toList();
    }

    public CourseResponseDto getCourseResponseDtoById(Long courseId) {
        return mapper.toResponseDto(getCourseById(courseId));
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(CourseNotFoundException::new);
    }

    public List<CourseResponseDto> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    private void validateNewSchedulesWithEachOther(List<Schedule> newSchedules) {
        newSchedules.forEach(schedule ->
                scheduleValidator.validateNewSchedulesNotOverlappingCurrentSchedules(
                        List.of(schedule),
                        newSchedules
                                .stream()
                                .filter(schedule1 ->
                                        !Objects.equals(schedule, schedule1))
                                .toList()
                ));
    }

}
