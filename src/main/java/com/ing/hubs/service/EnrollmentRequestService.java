package com.ing.hubs.service;

import com.ing.hubs.dto.EnrollmentRequestResponseDto;
import com.ing.hubs.dto.EnrollmentRequestUpdateDto;
import com.ing.hubs.entity.*;
import com.ing.hubs.exception.*;
import com.ing.hubs.mapper.Mapper;
import com.ing.hubs.repository.CourseRepository;
import com.ing.hubs.repository.EnrollmentRequestRepository;
import com.ing.hubs.repository.StudentCourseRepository;
import com.ing.hubs.service.validator.ScheduleValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Transactional
public class EnrollmentRequestService {
    private EnrollmentRequestRepository enrollmentRequestRepository;
    private CourseService courseService;
    private StudentCourseRepository studentCourseRepository;
    private CourseRepository courseRepository;
    private StudentCourseService studentCourseService;
    private ScheduleValidator scheduleValidator;
    private Mapper mapper;

    public EnrollmentRequestResponseDto createEnrollmentRequest(Long courseId) {
        User student = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Course course = courseService.getCourseById(courseId);

        if (enrollmentToCourseAlreadyPresent(courseId, student)) {
            throw new AlreadyEnrolledException();
        }

        List<Schedule> allStudentSchedules = getSchedulesForStudent(student.getId(), null);
        scheduleValidator.validateNewSchedulesNotOverlappingCurrentSchedules(course.getSchedule(), allStudentSchedules);

        EnrollmentRequest enrollmentRequest = EnrollmentRequest.builder()
                .course(course)
                .student(student)
                .status(EnrollmentRequestStatus.PENDING)
                .build();
        return mapper.toResponseDto(enrollmentRequestRepository.save(enrollmentRequest));
    }

    public List<EnrollmentRequestResponseDto> getEnrollmentRequestsForStudentByStatus(EnrollmentRequestStatus status) {
        User student = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<EnrollmentRequest> requests;
        if (status == null) {
            requests = enrollmentRequestRepository.findByStudentId(student.getId());
        } else {
            requests = enrollmentRequestRepository.findByStudentIdAndStatus(student.getId(), status);
        }
        return requests.stream().map(r -> mapper.toResponseDto(r)).collect(Collectors.toList());
    }

    public List<EnrollmentRequestResponseDto> getEnrollmentRequestsForCourseByStatus(Long courseId, EnrollmentRequestStatus status) {
        List<EnrollmentRequest> requests;
        if (status == null) {
            requests = enrollmentRequestRepository.findByCourseId(courseId);
        } else {
            requests = enrollmentRequestRepository.findByCourseIdAndStatus(courseId, status);
        }
        return requests.stream().map(r -> mapper.toResponseDto(r)).collect(Collectors.toList());
    }

    public EnrollmentRequestResponseDto updateEnrollmentRequestStatus(EnrollmentRequestUpdateDto enrollmentRequestUpdateDto, Long enrollmentRequestId) {
        EnrollmentRequest enrollmentRequest = getEnrollmentRequestById(enrollmentRequestId);

        if (enrollmentRequest.getStatus() == enrollmentRequestUpdateDto.getStatus()) {
            return mapper.toResponseDto(enrollmentRequest);
        }
        validateForStatusUpdate(enrollmentRequestUpdateDto, enrollmentRequest);

        Course course = enrollmentRequest.getCourse();
        validateCanAccept(enrollmentRequestUpdateDto, enrollmentRequest, course);
        enrollmentRequest.setStatus(enrollmentRequestUpdateDto.getStatus());
        if (enrollmentRequest.getStatus().equals(EnrollmentRequestStatus.ACCEPTED)) {
            studentCourseService.enrollStudent(enrollmentRequest);
        }
        return mapper.toResponseDto(enrollmentRequestRepository.save(enrollmentRequest));
    }

    private static void validateForStatusUpdate(EnrollmentRequestUpdateDto enrollmentRequestUpdateDto, EnrollmentRequest enrollmentRequest) {
        if (enrollmentRequest.getStatus() == EnrollmentRequestStatus.ACCEPTED) {
            throw new InvalidStatusUpdateException();
        } else if (enrollmentRequest.getStatus() == EnrollmentRequestStatus.DECLINED &&
                enrollmentRequestUpdateDto.getStatus() == EnrollmentRequestStatus.PENDING) {
            throw new InvalidStatusUpdateException();
        }
    }

    public void deleteEnrollmentRequest(Long enrollmentRequestId) {
        EnrollmentRequest enrollmentRequest = enrollmentRequestRepository.findById(enrollmentRequestId)
                .orElseThrow(EnrollmentRequestNotFoundException::new);

        if (enrollmentRequest.getStatus() != EnrollmentRequestStatus.PENDING) {
            throw new BadEnrollmentRequestStatusException();
        }
        enrollmentRequestRepository.delete(enrollmentRequest);
    }

    public EnrollmentRequest getEnrollmentRequestById(Long id) {
        return enrollmentRequestRepository.findById(id)
                .orElseThrow(EnrollmentRequestNotFoundException::new);
    }

    private void validateCanAccept(EnrollmentRequestUpdateDto enrollmentRequestUpdateDto, EnrollmentRequest enrollmentRequest, Course course) {
        if ((enrollmentRequest.getStatus().equals(EnrollmentRequestStatus.PENDING) ||
                enrollmentRequest.getStatus().equals(EnrollmentRequestStatus.DECLINED)) &&
                enrollmentRequestUpdateDto.getStatus().equals(EnrollmentRequestStatus.ACCEPTED)) {

            if (course.getMaxAttendees() <= studentCourseRepository.countByCourse(course)) {
                throw new MaximumNumberOfAttendeesReachedException();
            }

            List<Schedule> allOtherStudentSchedules = getSchedulesForStudent(enrollmentRequest.getStudent().getId(), enrollmentRequest.getId());
            scheduleValidator.validateNewSchedulesNotOverlappingCurrentSchedules(course.getSchedule(), allOtherStudentSchedules);
        }
    }

    private boolean enrollmentToCourseAlreadyPresent(Long courseId, User student) {
        return enrollmentRequestRepository
                .findByStudentIdAndCourseId(student.getId(), courseId)
                .isPresent();
    }

    private List<Schedule> getSchedulesForStudent(Long studentId, Long requestIdToIgnore) {
        List<Course> enrolledCourses = courseRepository.findCoursesByStudentId(studentId);
        List<EnrollmentRequest> pendingEnrollingRequests = enrollmentRequestRepository.findPendingByStudentId(studentId);

        List<Course> pendingCourses = pendingEnrollingRequests.stream()
                .filter(request -> !Objects.equals(request.getId(), requestIdToIgnore))
                .map(EnrollmentRequest::getCourse)
                .toList();

        return Stream.concat(enrolledCourses.stream(), pendingCourses.stream())
                .flatMap(c -> c.getSchedule().stream())
                .toList();
    }

}
