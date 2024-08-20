package com.ing.hubs.mapper;

import com.ing.hubs.dto.*;
import com.ing.hubs.entity.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class Mapper {
    private ModelMapper modelMapper;

    public Schedule fromResponseDto(ScheduleResponseDto scheduleResponseDto, Course course) {
        return Schedule.builder()
                .id(scheduleResponseDto.getId())
                .name(scheduleResponseDto.getName())
                .course(course)
                .endDate(scheduleResponseDto.getEndDate())
                .endTime(scheduleResponseDto.getEndTime())
                .startDate(scheduleResponseDto.getStartDate())
                .startTime(scheduleResponseDto.getStartTime())
                .weekDay(scheduleResponseDto.getWeekDay())
                .build();
    }

    public UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .build();
    }

    public EnrollmentRequestResponseDto toResponseDto(EnrollmentRequest request) {
        return EnrollmentRequestResponseDto.builder()
                .id(request.getId())
                .courseId(request.getCourse().getId())
                .studentId(request.getStudent().getId())
                .status(request.getStatus())
                .build();
    }

    public Course fromDto(CourseDto courseDto, User teacher) {
        return Course.builder()
                .name(courseDto.getName())
                .description(courseDto.getDescription())
                .maxAttendees(courseDto.getMaxAttendees())
                .teacher(teacher)
                .build();
    }

    public Schedule fromDto(ScheduleDto scheduleDto, Course course) {
        return Schedule.builder()
                .weekDay(scheduleDto.getWeekDay())
                .startDate(scheduleDto.getStartDate())
                .endDate(scheduleDto.getEndDate())
                .startTime(scheduleDto.getStartTime())
                .endTime(scheduleDto.getEndTime())
                .name(scheduleDto.getName())
                .course(course)
                .build();
    }

    public User fromDto(UserDto userDto, Set<UserType> userTypes) {
        return User.builder()
                .username(userDto.getUsername())
                .fullName(userDto.getFullName())
                .email(userDto.getEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .password(userDto.getPassword())
                .roles(userTypes)
                .build();
    }

    public GradeDto toDto(Grade grade) {
        return GradeDto.builder()
                .courseId(grade.getCourse().getId())
                .studentId(grade.getStudent().getId())
                .score(grade.getScore())
                .build();
    }

    public CourseResponseDto toResponseDto(Course course) {
        return CourseResponseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .maxAttendees(course.getMaxAttendees())
                .schedule(course.getSchedule()
                        .stream()
                        .map(b -> modelMapper.map(b, ScheduleResponseDto.class))
                        .toList())
                .build();
    }

    public <D> D map(Object source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
    
}
