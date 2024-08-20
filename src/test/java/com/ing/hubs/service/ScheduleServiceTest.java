package com.ing.hubs.service;

import com.ing.hubs.dto.ScheduleDto;
import com.ing.hubs.dto.ScheduleUpdateDto;
import com.ing.hubs.entity.*;
import com.ing.hubs.dto.ScheduleResponseDto;
import com.ing.hubs.exception.MoodleException;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.EnrollmentRequestRepository;
import com.ing.hubs.repository.ScheduleRepository;
import com.ing.hubs.security.JwtProvider;
import com.ing.hubs.service.validator.ScheduleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {
    @Mock
    private EnrollmentRequestRepository enrollmentRequestRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private ScheduleService scheduleService;
    private final Mapper mapper = new Mapper(new ModelMapper());

    ScheduleValidator scheduleValidator = mock(ScheduleValidator.class);

    @BeforeEach
    public void setup() {
        UserService userService = mock(UserService.class);
        JwtProvider jwtProvider = mock(JwtProvider.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

        scheduleService = new ScheduleService(
                enrollmentRequestRepository,
                scheduleRepository,
                courseService,
                mapper,
                scheduleValidator
        );
    }

    @Nested
    class GetAllSchedules {
        @Test
        void whenCalled_ReturnsListOfScheduleResponseDto() {
            List<Schedule> schedules = List.of(
            Schedule.builder()
                    .name("Schedule 1")
                    .startDate(LocalDate.of(2023, 12, 6))
                    .endDate(LocalDate.of(2023, 12, 13))
                    .weekDay(WeekDay.MON)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(17, 0))
                    .build(),
            Schedule.builder()
                    .name("Schedule 2")
                    .startDate(LocalDate.of(2023, 12, 7))
                    .endDate(LocalDate.of(2023, 12, 14))
                    .weekDay(WeekDay.TUE)
                    .startTime(LocalTime.of(10, 0))
                    .endTime(LocalTime.of(18, 0))
                    .build(),
            Schedule.builder()
                    .name("Schedule 3")
                    .startDate(LocalDate.of(2023, 12, 8))
                    .endDate(LocalDate.of(2023, 12, 15))
                    .weekDay(WeekDay.WED)
                    .startTime(LocalTime.of(11, 0))
                    .endTime(LocalTime.of(19, 0))
                    .build()
            );

            when(scheduleRepository.findAll()).thenReturn(schedules);

            List<ScheduleResponseDto> actualDtos = scheduleService.getAllSchedules();

            assertEquals(schedules.size(), actualDtos.size());

            for (int i = 0; i < schedules.size(); i++) {
                ScheduleResponseDto actualDto = actualDtos.get(i);
                Schedule schedule = schedules.get(i);
                assertEquals(schedule.getName(), actualDto.getName());
                assertEquals(schedule.getStartDate(), actualDto.getStartDate());
                assertEquals(schedule.getEndDate(), actualDto.getEndDate());
                assertEquals(schedule.getWeekDay(), actualDto.getWeekDay());
                assertEquals(schedule.getStartTime(), actualDto.getStartTime());
                assertEquals(schedule.getEndTime(), actualDto.getEndTime());
            }

            verify(scheduleRepository).findAll();
        }

        @Test
        void whenNoSchedulesAvailable_ReturnsEmptyList() {
            when(scheduleRepository.findAll()).thenReturn(Collections.emptyList());

            List<ScheduleResponseDto> actualDtos = scheduleService.getAllSchedules();

            assertTrue(actualDtos.isEmpty());
            verify(scheduleRepository).findAll();
        }
    }
    @Nested
    class CreateSchedule {
        @Test
        void whenValidScheduleDtoIsProvided_ReturnsScheduleResponseDto() {
            ScheduleDto scheduleDto = ScheduleDto.builder()
                    .name("Schedule 1")
                    .startDate(LocalDate.of(2023, 12, 6))
                    .endDate(LocalDate.of(2023, 12, 13))
                    .weekDay(WeekDay.MON)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(17, 0))
                    .build();
            Course course = Course.builder()
                    .id(1L)
                    .name("OOP")
                    .description("Something")
                    .maxAttendees(30)
                    .schedule(new ArrayList<>())
                    .build();
            Schedule newSchedule = Schedule.builder()
                    .course(course)
                    .name("New schedule")
                    .startDate(LocalDate.of(2023, 9, 4))
                    .endDate(LocalDate.of(2023, 12, 13))
                    .weekDay(WeekDay.TUE)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(17, 0))
                    .build();

            when(courseService.getCourseById(course.getId())).thenReturn(course);
            when(scheduleRepository.save(any(Schedule.class))).thenReturn(newSchedule);

            ScheduleResponseDto result = scheduleService.createSchedule(scheduleDto, course.getId());

            assertNotNull(result);
            assertEquals(newSchedule.getName(), result.getName());
            assertEquals(newSchedule.getStartDate(), result.getStartDate());
            assertEquals(newSchedule.getEndDate(), result.getEndDate());
            assertEquals(newSchedule.getWeekDay(), result.getWeekDay());
            assertEquals(newSchedule.getStartTime(), result.getStartTime());
            assertEquals(newSchedule.getEndTime(), result.getEndTime());

            verify(courseService).getCourseById(course.getId());
            verify(scheduleRepository).save(any(Schedule.class));
            verify(scheduleValidator).validateSchedule(scheduleDto);
        }
    }

    @Nested
    class GetSchedulesForCourse {
        @Test
        void whenCourseIdProvided_ReturnsListOfScheduleResponseDto() {
            Long courseId = 1L;
            List<Schedule> schedules = List.of(
                    Schedule.builder()
                            .name("Schedule 1")
                            .startDate(LocalDate.of(2023, 12, 6))
                            .endDate(LocalDate.of(2023, 12, 13))
                            .weekDay(WeekDay.MON)
                            .startTime(LocalTime.of(9, 0))
                            .endTime(LocalTime.of(17, 0))
                            .build(),
                    Schedule.builder()
                            .name("Schedule 2")
                            .startDate(LocalDate.of(2023, 12, 7))
                            .endDate(LocalDate.of(2023, 12, 14))
                            .weekDay(WeekDay.TUE)
                            .startTime(LocalTime.of(10, 0))
                            .endTime(LocalTime.of(18, 0))
                            .build(),
                    Schedule.builder()
                            .name("Schedule 3")
                            .startDate(LocalDate.of(2023, 12, 8))
                            .endDate(LocalDate.of(2023, 12, 15))
                            .weekDay(WeekDay.WED)
                            .startTime(LocalTime.of(11, 0))
                            .endTime(LocalTime.of(19, 0))
                            .build()
            );

            when(scheduleRepository.findByCourseId(courseId)).thenReturn(schedules);

            List<ScheduleResponseDto> result = scheduleService.getSchedulesForCourse(courseId);

            assertEquals(schedules.size(), result.size());

            for (int i = 0; i < schedules.size(); i++) {
                ScheduleResponseDto actualDto = result.get(i);
                Schedule schedule = schedules.get(i);
                assertEquals(schedule.getName(), actualDto.getName());
                assertEquals(schedule.getStartDate(), actualDto.getStartDate());
                assertEquals(schedule.getEndDate(), actualDto.getEndDate());
                assertEquals(schedule.getWeekDay(), actualDto.getWeekDay());
                assertEquals(schedule.getStartTime(), actualDto.getStartTime());
                assertEquals(schedule.getEndTime(), actualDto.getEndTime());
            }

            verify(scheduleRepository).findByCourseId(courseId);
        }
    }

    @Nested
    class GetSchedulesForStudent {
        @Test
        void whenCalled_ReturnsListOfScheduleResponseDtoForAuthenticatedStudent() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            setLoggedInUser(student);

            List<Schedule> schedules = List.of(
                    Schedule.builder()
                            .name("Schedule 1")
                            .startDate(LocalDate.of(2023, 12, 6))
                            .endDate(LocalDate.of(2023, 12, 13))
                            .weekDay(WeekDay.MON)
                            .startTime(LocalTime.of(9, 0))
                            .endTime(LocalTime.of(17, 0))
                            .build(),
                    Schedule.builder()
                            .name("Schedule 2")
                            .startDate(LocalDate.of(2023, 12, 7))
                            .endDate(LocalDate.of(2023, 12, 14))
                            .weekDay(WeekDay.TUE)
                            .startTime(LocalTime.of(10, 0))
                            .endTime(LocalTime.of(18, 0))
                            .build()
            );

            when(scheduleRepository.findByStudentId(student.getId())).thenReturn(schedules);

            List<ScheduleResponseDto> result = scheduleService.getSchedulesForStudent(student.getId());

            assertEquals(schedules.size(), result.size());

            for (int i = 0; i < schedules.size(); i++) {
                ScheduleResponseDto actualDto = result.get(i);
                Schedule schedule = schedules.get(i);
                assertEquals(schedule.getName(), actualDto.getName());
                assertEquals(schedule.getStartDate(), actualDto.getStartDate());
                assertEquals(schedule.getEndDate(), actualDto.getEndDate());
                assertEquals(schedule.getWeekDay(), actualDto.getWeekDay());
                assertEquals(schedule.getStartTime(), actualDto.getStartTime());
                assertEquals(schedule.getEndTime(), actualDto.getEndTime());
            }

            verify(scheduleRepository).findByStudentId(student.getId());
        }
    }


    @Nested
    class UpdateSchedule {
        @Test
        void whenValidScheduleUpdateDtoProvided_ReturnsUpdatedScheduleResponseDto() {
            ScheduleUpdateDto scheduleUpdateDto = new ScheduleUpdateDto("Schedule 2");

            Course course = Course.builder()
                    .id(1L)
                    .name("OOP")
                    .description("Something")
                    .maxAttendees(30)
                    .build();

            Schedule currentSchedule = Schedule.builder()
                    .id(1L)
                    .name("Schedule 1")
                    .startDate(LocalDate.of(2023, 12, 6))
                    .endDate(LocalDate.of(2023, 12, 13))
                    .weekDay(WeekDay.MON)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(17, 0))
                    .course(course)
                    .build();

            setLoggedInUser(User.builder().id(1L).roles(Set.of(UserType.TEACHER)).build());

            when(scheduleRepository.findById(currentSchedule.getId())).thenReturn(Optional.of(currentSchedule));
            when(courseService.getCourseById(course.getId())).thenReturn(course);
            when(scheduleRepository.save(any(Schedule.class))).thenReturn(currentSchedule);

            ScheduleResponseDto result = scheduleService.updateSchedule(currentSchedule.getId(), scheduleUpdateDto);

            assertNotNull(result);
            assertEquals(currentSchedule.getName(), result.getName());
            assertEquals(currentSchedule.getStartDate(), result.getStartDate());
            assertEquals(currentSchedule.getEndDate(), result.getEndDate());
            assertEquals(currentSchedule.getWeekDay(), result.getWeekDay());
            assertEquals(currentSchedule.getStartTime(), result.getStartTime());
            assertEquals(currentSchedule.getEndTime(), result.getEndTime());

            verify(scheduleRepository).findById(currentSchedule.getId());
            verify(courseService).getCourseById(course.getId());
            verify(scheduleRepository).save(any(Schedule.class));
        }
    }


    @Nested
    class GetScheduleById {
        @Test
        void whenScheduleExists_ReturnsSchedule() {
            Long scheduleId = 1L;
            Schedule expectedSchedule = new Schedule();
            expectedSchedule.setId(scheduleId);

            when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(expectedSchedule));

            Schedule actualSchedule = scheduleService.getScheduleById(scheduleId);

            assertNotNull(actualSchedule);
            assertEquals(expectedSchedule, actualSchedule);
            verify(scheduleRepository).findById(scheduleId);
        }

        @Test
        void whenScheduleDoesNotExist_ThrowsException() {
            Long scheduleId = 1L;
            when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

            assertThrows(MoodleException.class, () -> scheduleService.getScheduleById(scheduleId));
            verify(scheduleRepository).findById(scheduleId);
        }
    }

    private static void setLoggedInUser(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}