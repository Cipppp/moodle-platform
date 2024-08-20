package com.ing.hubs.service;

import com.ing.hubs.dto.CourseDto;
import com.ing.hubs.dto.CourseResponseDto;
import com.ing.hubs.dto.CourseUpdateDto;
import com.ing.hubs.entity.Course;
import com.ing.hubs.entity.User;
import com.ing.hubs.entity.UserType;
import com.ing.hubs.exception.MaximumNumberOfAttendeesDecreaseException;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.CourseRepository;
import com.ing.hubs.repository.ScheduleRepository;
import com.ing.hubs.service.validator.ScheduleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CourseServiceTest {
    private CourseRepository courseRepository;
    private ScheduleRepository scheduleRepository;
    private final Mapper mapper = new Mapper(new ModelMapper());
    private ScheduleValidator scheduleValidator;
    private CourseService courseService;

    @BeforeEach
    public void setup() {
        courseRepository = mock(CourseRepository.class);
        scheduleRepository = mock(ScheduleRepository.class);
        scheduleValidator = mock(ScheduleValidator.class);
        courseService = new CourseService(
                courseRepository,
                scheduleRepository,
                mapper,
                scheduleValidator
        );
    }

    @Test
    void shouldCreateCourseSuccessfully() {
        CourseDto courseDto = CourseDto.builder()
                .name("OOP")
                .description("OOP description")
                .maxAttendees(30)
                .schedules(List.of())
                .build();
        User teacher = User.builder()
                .id(2L)
                .roles(Set.of(UserType.TEACHER))
                .build();
        Course course = Course.builder()
                .id(1L)
                .name("name")
                .description("description")
                .maxAttendees(40)
                .schedule(List.of())
                .build();
        CourseResponseDto responseDto = CourseResponseDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .maxAttendees(40)
                .schedule(List.of())
                .build();
        setLoggedInUser(teacher);

        when(courseRepository.save(any())).thenReturn(course);
        when(scheduleRepository.saveAll(any())).thenReturn(List.of());

        CourseResponseDto result = courseService.createCourse(courseDto);

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        assertEquals(responseDto.getName(), result.getName());
        assertEquals(responseDto.getDescription(), result.getDescription());
        assertEquals(responseDto.getMaxAttendees(), result.getMaxAttendees());
        verify(courseRepository).save(any());
        verify(scheduleRepository).saveAll(any());
    }



    @Test
    void shouldUpdateCourseSuccessfully() {
        Course currentCourse = Course.builder()
                .id(1L)
                .name("MS")
                .description("MS description")
                .maxAttendees(30)
                .schedule(List.of())
                .build();

        CourseUpdateDto courseUpdateDto = new CourseUpdateDto("Updated", "New description", 40);
        Course expectedSavedCourse = Course.builder()
                .id(currentCourse.getId())
                .name(courseUpdateDto.getName())
                .description(courseUpdateDto.getDescription())
                .maxAttendees(courseUpdateDto.getMaxAttendees())
                .build();

        CourseResponseDto expectedResponseDto = CourseResponseDto.builder()
                .id(currentCourse.getId())
                .name(courseUpdateDto.getName())
                .description(courseUpdateDto.getDescription())
                .maxAttendees(courseUpdateDto.getMaxAttendees())
                .schedule(List.of())
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(currentCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(expectedSavedCourse);

        CourseResponseDto result = courseService.updateCourse(currentCourse.getId(), courseUpdateDto);

        assertNotNull(result);
        assertEquals(expectedResponseDto, result);
        verify(courseRepository).findById(currentCourse.getId());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void shouldThrowExceptionWhenMaxAttendeesIsLower() {
        Course currentCourse = Course.builder()
                .id(1L)
                .maxAttendees(30)
                .build();
        CourseUpdateDto courseUpdateDto = CourseUpdateDto.builder()
                .name("Updated")
                .description("New description")
                .maxAttendees(20)
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.ofNullable(currentCourse));

        assertThrows(MaximumNumberOfAttendeesDecreaseException.class, () ->
                courseService.updateCourse(currentCourse.getId(), courseUpdateDto));
    }

    @Test
    void shouldReturnAllCreatedCourses() {
        User teacher = User.builder()
                .id(1L)
                .username("Duca")
                .fullName("Duca Iulian")
                .email("duca@gmail.com")
                .phoneNumber("0774657614")
                .password("duca1234")
                .roles(Set.of(UserType.TEACHER))
                .build();
        List<Course> courses = new ArrayList<>();

        setLoggedInUser(teacher);
        when(courseRepository.findByTeacherId(teacher.getId())).thenReturn(courses);

        List<CourseResponseDto> result = courseService.getAllCreatedCourses(null);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(courseRepository).findByTeacherId(teacher.getId());
    }

    @Test
    void shouldReturnCoursesEnrolledTo() {
        User student = User.builder()
                .id(1L)
                .username("Spike")
                .fullName("Spike cat")
                .email("spike@gmail.com")
                .phoneNumber("0774657614")
                .password("spike1234")
                .roles(Set.of(UserType.STUDENT))
                .build();

        User teacher = User.builder()
                .id(1L)
                .username("Duca")
                .fullName("Duca Iulian")
                .email("duca@gmail.com")
                .phoneNumber("0774657614")
                .password("duca1234")
                .roles(Set.of(UserType.TEACHER))
                .build();

        List<Course> courses = List.of(
                Course.builder()
                        .id(1L)
                        .name("OOP")
                        .description("Something")
                        .maxAttendees(30)
                        .teacher(teacher)
                        .schedule(new ArrayList<>())
                        .build(),
                Course.builder()
                        .id(2L)
                        .name("MS")
                        .description("MS something")
                        .maxAttendees(31)
                        .teacher(teacher)
                        .schedule(new ArrayList<>())
                        .build()
        );

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn(student);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(courseRepository.findCoursesByStudentId(student.getId())).thenReturn(courses);

        List<CourseResponseDto> result = courseService.getCoursesEnrolledTo();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(courseRepository).findCoursesByStudentId(student.getId());
    }

    @Test
    void getCourseResponseDtoById_shouldReturnCourseResponseDto() {
        Course course = Course.builder()
                .id(1L)
                .name("name")
                .description("description")
                .maxAttendees(10)
                .build();
        CourseResponseDto courseResponseDto = CourseResponseDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .maxAttendees(10)
                .schedule(List.of())
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertEquals(courseResponseDto, courseService.getCourseResponseDtoById(course.getId()));
    }

    @Test
    void getAllCourses_shouldReturnCourseResponseDto() {
        Course course1 = Course.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .maxAttendees(10)
                .build();
        Course course2 = Course.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .maxAttendees(20)
                .build();
        CourseResponseDto courseResponseDto1 = CourseResponseDto.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .maxAttendees(10)
                .schedule(List.of())
                .build();
        CourseResponseDto courseResponseDto2 = CourseResponseDto.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .maxAttendees(20)
                .schedule(List.of())
                .build();

        when(courseRepository.findAll()).thenReturn(List.of(course1, course2));

        var result = courseService.getAllCourses();

        assertThat(result, containsInAnyOrder(courseResponseDto1, courseResponseDto2));
    }

    private static void setLoggedInUser(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
