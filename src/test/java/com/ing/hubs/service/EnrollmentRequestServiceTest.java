package com.ing.hubs.service;

import com.ing.hubs.dto.EnrollmentRequestResponseDto;
import com.ing.hubs.dto.EnrollmentRequestUpdateDto;
import com.ing.hubs.entity.*;
import com.ing.hubs.exception.AlreadyEnrolledException;
import com.ing.hubs.exception.BadEnrollmentRequestStatusException;
import com.ing.hubs.exception.EnrollmentRequestNotFoundException;
import com.ing.hubs.exception.MaximumNumberOfAttendeesReachedException;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.CourseRepository;
import com.ing.hubs.repository.EnrollmentRequestRepository;
import com.ing.hubs.repository.StudentCourseRepository;
import com.ing.hubs.service.validator.ScheduleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnrollmentRequestServiceTest {
    private EnrollmentRequestRepository enrollmentRequestRepository;
    private CourseService courseService;
    private StudentCourseRepository studentCourseRepository;
    private StudentCourseService studentCourseService;
    private ScheduleValidator scheduleValidator;
    private final Mapper mapper = new Mapper(new ModelMapper());

    private EnrollmentRequestService enrollmentRequestService;

    Authentication authentication;

    @BeforeEach
    public void setup() {
        studentCourseRepository = mock(StudentCourseRepository.class);
        courseService = mock(CourseService.class);
        studentCourseService = mock(StudentCourseService.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        enrollmentRequestService = mock(EnrollmentRequestService.class);
        scheduleValidator = mock(ScheduleValidator.class);
        enrollmentRequestRepository = mock(EnrollmentRequestRepository.class);
        enrollmentRequestService = new EnrollmentRequestService(
                enrollmentRequestRepository,
                courseService,
                studentCourseRepository,
                courseRepository,
                studentCourseService,
                scheduleValidator,
                mapper
        );

        authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    class CreateEnrollmentRequest {
        @Test
        void successfulEnrollment() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            Course course = Course.builder()
                    .id(1L)
                    .teacher(new User())
                    .schedule(List.of())
                    .build();
            EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                    .id(1L)
                    .course(course)
                    .student(student)
                    .status(EnrollmentRequestStatus.PENDING)
                    .build();

            setLoggedInUser(student);
            when(courseService.getCourseById(1L)).thenReturn(course);
            when(enrollmentRequestRepository.save(any(EnrollmentRequest.class))).thenReturn(enrollmentRequest);
            EnrollmentRequestResponseDto result = enrollmentRequestService.createEnrollmentRequest(1L);

            assertNotNull(result);
            verify(courseService).getCourseById(1L);
            verify(scheduleValidator).validateNewSchedulesNotOverlappingCurrentSchedules(any(), any());
            verify(enrollmentRequestRepository).save(any(EnrollmentRequest.class));
        }

        @Test
        void alreadyEnrolled_ThrowsAlreadyEnrolledException() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            Course course = Course.builder()
                    .id(1L)
                    .maxAttendees(30)
                    .teacher(new User())
                    .schedule(List.of())
                    .build();

            setLoggedInUser(student);
            when(courseService.getCourseById(1L)).thenReturn(course);
            when(enrollmentRequestRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(EnrollmentRequest.builder().build()));

            assertThrows(AlreadyEnrolledException.class, () ->
                    enrollmentRequestService.createEnrollmentRequest(1L));
            verify(enrollmentRequestRepository, never()).save(any(EnrollmentRequest.class));
        }

    }

    @Nested
    class GetEnrollmentRequestsForStudentByStatus {
        @Test
        void noStatusSpecified_ReturnsAllRequests() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            List<EnrollmentRequest> enrollmentRequests = List.of(
                    EnrollmentRequest
                            .builder()
                            .id(1L)
                            .course(new Course())
                            .student(student)
                            .status(EnrollmentRequestStatus.PENDING)
                            .build(),
                    EnrollmentRequest.builder()
                            .id(2L)
                            .course(new Course())
                            .student(student)
                            .status(EnrollmentRequestStatus.ACCEPTED)
                            .build()
            );

            setLoggedInUser(student);
            when(enrollmentRequestRepository.findByStudentId(1L)).thenReturn(enrollmentRequests);
            List<EnrollmentRequestResponseDto> result = enrollmentRequestService.getEnrollmentRequestsForStudentByStatus(null);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(enrollmentRequestRepository).findByStudentId(1L);
        }

        @Test
        void withStatus_ReturnsMatchingRequests() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                    .id(1L)
                    .course(new Course())
                    .student(student)
                    .status(EnrollmentRequestStatus.PENDING)
                    .build();


            setLoggedInUser(student);
            when(enrollmentRequestRepository.findByStudentIdAndStatus(1L, EnrollmentRequestStatus.PENDING)).thenReturn(List.of(enrollmentRequest));
            List<EnrollmentRequestResponseDto> result = enrollmentRequestService.getEnrollmentRequestsForStudentByStatus(EnrollmentRequestStatus.PENDING);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(enrollmentRequestRepository).findByStudentIdAndStatus(1L, EnrollmentRequestStatus.PENDING);
        }
    }

    @Nested
    class GetEnrollmentRequestsForCourseByStatus {
        @Test
        void noStatusSpecified_ReturnsAllRequests() {
            User firstStudent = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User secondStudent = User.builder()
                    .id(2L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User teacher = User.builder()
                    .id(3L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(1L)
                    .teacher(teacher)
                    .build();
            List<EnrollmentRequest> enrollmentRequests = List.of(
                    EnrollmentRequest
                            .builder()
                            .id(1L)
                            .course(course)
                            .student(firstStudent)
                            .status(EnrollmentRequestStatus.PENDING)
                            .build(),
                    EnrollmentRequest.builder()
                            .id(2L)
                            .course(course)
                            .student(secondStudent)
                            .status(EnrollmentRequestStatus.ACCEPTED)
                            .build()
            );

            setLoggedInUser(teacher);
            when(enrollmentRequestRepository.findByCourseId(1L)).thenReturn(enrollmentRequests);
            List<EnrollmentRequestResponseDto> result =
                    enrollmentRequestService.getEnrollmentRequestsForCourseByStatus(1L, null);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(enrollmentRequestRepository).findByCourseId(1L);
        }

        @Test
        void withStatus_ReturnsMatchingRequests() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            User teacher = User.builder()
                    .id(3L)
                    .roles(Set.of(UserType.TEACHER))
                    .build();
            Course course = Course.builder()
                    .id(1L)
                    .teacher(teacher)
                    .build();
            EnrollmentRequest enrollmentRequest = EnrollmentRequest
                    .builder()
                    .id(1L)
                    .course(course)
                    .student(student)
                    .status(EnrollmentRequestStatus.PENDING)
                    .build();

            setLoggedInUser(teacher);
            when(enrollmentRequestRepository.findByCourseIdAndStatus(1L, EnrollmentRequestStatus.PENDING)).thenReturn(List.of(enrollmentRequest));
            List<EnrollmentRequestResponseDto> result = enrollmentRequestService.getEnrollmentRequestsForCourseByStatus(1L, EnrollmentRequestStatus.PENDING);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(enrollmentRequestRepository).findByCourseIdAndStatus(1L, EnrollmentRequestStatus.PENDING);
        }
    }

    @Nested
    class UpdateEnrollmentRequestStatus {
        @Test
        void statusNotChanged_ReturnsOriginalRequest() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            Course course = Course.builder()
                    .id(1L)
                    .maxAttendees(30)
                    .teacher(new User())
                    .schedule(List.of())
                    .build();
            EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                    .id(1L)
                    .course(course)
                    .student(student)
                    .status(EnrollmentRequestStatus.PENDING)
                    .build();
            EnrollmentRequestUpdateDto updateDto =
                    new EnrollmentRequestUpdateDto(EnrollmentRequestStatus.PENDING);

            setLoggedInUser(student);
            when(enrollmentRequestRepository.findById(1L))
                    .thenReturn(java.util.Optional.of(enrollmentRequest));
            EnrollmentRequestResponseDto result =
                    enrollmentRequestService.updateEnrollmentRequestStatus(updateDto,1L);

            assertNotNull(result);
            assertEquals(EnrollmentRequestStatus.PENDING, enrollmentRequest.getStatus());
            verify(enrollmentRequestRepository, never()).save(any(EnrollmentRequest.class));
            verify(studentCourseService, never()).enrollStudent(any(EnrollmentRequest.class));
        }
        @Test
        void statusChangedToAccepted_EnrollsStudent() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            Course course = Course.builder()
                    .id(1L)
                    .maxAttendees(30)
                    .teacher(new User())
                    .schedule(List.of())
                    .build();
            EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                    .id(1L)
                    .course(course)
                    .student(student)
                    .status(EnrollmentRequestStatus.PENDING)
                    .build();
            EnrollmentRequestUpdateDto updateDto =
                    new EnrollmentRequestUpdateDto(EnrollmentRequestStatus.ACCEPTED);

            setLoggedInUser(student);
            when(enrollmentRequestRepository.findById(1L)).thenReturn(java.util.Optional.of(enrollmentRequest));
            when(studentCourseRepository.countByCourse(course)).thenReturn(0);
            when(enrollmentRequestRepository.save(any())).thenReturn(enrollmentRequest);
            EnrollmentRequestResponseDto result =
                    enrollmentRequestService.updateEnrollmentRequestStatus(updateDto, 1L);

            assertNotNull(result);
            assertEquals(EnrollmentRequestStatus.ACCEPTED, enrollmentRequest.getStatus());
            verify(scheduleValidator).validateNewSchedulesNotOverlappingCurrentSchedules(any(), any());
            verify(enrollmentRequestRepository).save(any(EnrollmentRequest.class));
            verify(studentCourseService).enrollStudent(any(EnrollmentRequest.class));
        }

        @Test
        void invalidAcceptance_ThrowsLimitExceededException() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            Course course = Course.builder()
                    .id(1L)
                    .maxAttendees(30)
                    .teacher(new User())
                    .schedule(List.of())
                    .build();
            EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                    .id(1L)
                    .course(course)
                    .student(student)
                    .status(EnrollmentRequestStatus.PENDING)
                    .build();
            EnrollmentRequestUpdateDto updateDto =
                    new EnrollmentRequestUpdateDto(EnrollmentRequestStatus.ACCEPTED);

            setLoggedInUser(student);
            when(enrollmentRequestRepository.findById(1L)).thenReturn(java.util.Optional.of(enrollmentRequest));
            when(studentCourseRepository.countByCourse(course)).thenReturn(30);

            assertThrows(MaximumNumberOfAttendeesReachedException.class, () ->
                    enrollmentRequestService.updateEnrollmentRequestStatus(updateDto, 1L));
            assertEquals(EnrollmentRequestStatus.PENDING, enrollmentRequest.getStatus());
            verify(enrollmentRequestRepository, never()).save(any(EnrollmentRequest.class));
            verify(studentCourseService, never()).enrollStudent(any(EnrollmentRequest.class));
        }

        @Test
        void statusChangedToAccepted_WithScheduleOverlap_ThrowsLimitExceededException() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            Course course = Course.builder()
                    .id(1L)
                    .maxAttendees(30)
                    .teacher(new User())
                    .schedule(List.of())
                    .build();
            EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                    .id(1L)
                    .course(course)
                    .student(student)
                    .status(EnrollmentRequestStatus.PENDING)
                    .build();
            EnrollmentRequestUpdateDto updateDto =
                    new EnrollmentRequestUpdateDto(EnrollmentRequestStatus.ACCEPTED);

            setLoggedInUser(student);
            when(enrollmentRequestRepository.findById(1L)).thenReturn(java.util.Optional.of(enrollmentRequest));
            when(studentCourseRepository.countByCourse(course)).thenReturn(30);

            assertThrows(MaximumNumberOfAttendeesReachedException.class, () -> enrollmentRequestService.updateEnrollmentRequestStatus(updateDto, 1L));
            assertEquals(EnrollmentRequestStatus.PENDING, enrollmentRequest.getStatus());
            verify(enrollmentRequestRepository, never()).save(any(EnrollmentRequest.class));
            verify(studentCourseService, never()).enrollStudent(any(EnrollmentRequest.class));
        }

        @Test
        void invalidStatus_ThrowsEnrollmentRequestNotFoundException() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            EnrollmentRequestUpdateDto updateDto = new EnrollmentRequestUpdateDto(EnrollmentRequestStatus.ACCEPTED);

            setLoggedInUser(student);
            when(enrollmentRequestRepository.findById(1L)).thenReturn(java.util.Optional.empty());

            assertThrows(EnrollmentRequestNotFoundException.class, () ->
                    enrollmentRequestService.updateEnrollmentRequestStatus(updateDto, 1L));
            verify(enrollmentRequestRepository, never()).save(any(EnrollmentRequest.class));
            verify(studentCourseService, never()).enrollStudent(any(EnrollmentRequest.class));
        }
    }

    @Nested
    class DeleteEnrollmentRequest {
        @Test
        void validEnrollmentRequest_DeletesEnrollmentRequest() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                    .id(1L)
                    .student(student)
                    .status(EnrollmentRequestStatus.PENDING)
                    .build();

            setLoggedInUser(student);
            when(enrollmentRequestRepository.findById(1L)).thenReturn(Optional.of(enrollmentRequest));
            enrollmentRequestService.deleteEnrollmentRequest(1L);

            verify(enrollmentRequestRepository).delete(enrollmentRequest);
        }

        @Test
        void enrollmentRequestNotFound_ThrowsEnrollmentRequestNotFoundException() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();

            setLoggedInUser(student);
            when(enrollmentRequestRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(EnrollmentRequestNotFoundException.class, () ->
                    enrollmentRequestService.deleteEnrollmentRequest(1L));

            verify(enrollmentRequestRepository, never()).delete(any(EnrollmentRequest.class));
        }

        @Test
        void enrollmentRequestNotPending_ThrowsEnrollmentRequestNotFoundException() {
            User student = User.builder()
                    .id(1L)
                    .roles(Set.of(UserType.STUDENT))
                    .build();
            EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                    .id(1L).student(student)
                    .status(EnrollmentRequestStatus.ACCEPTED)
                    .build();

            setLoggedInUser(student);
            when(enrollmentRequestRepository.findById(1L)).thenReturn(Optional.of(enrollmentRequest));

            assertThrows(BadEnrollmentRequestStatusException.class, () ->
                    enrollmentRequestService.deleteEnrollmentRequest(1L));
            verify(enrollmentRequestRepository, never()).delete(any(EnrollmentRequest.class));
        }
    }

    private void setLoggedInUser(User user) {
        when(authentication.getPrincipal()).thenReturn(user);
    }

}
