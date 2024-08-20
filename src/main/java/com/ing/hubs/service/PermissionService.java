package com.ing.hubs.service;

import com.ing.hubs.entity.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class PermissionService {
    private EnrollmentRequestService enrollmentRequestService;
    private CourseService courseService;
    private GradeService gradeService;
    private ScheduleService scheduleService;

    public boolean canDeleteEnrollmentRequest(Long enrollmentRequestId) {
        User user = getLoggedInUser();
        EnrollmentRequest enrollmentRequest = enrollmentRequestService.getEnrollmentRequestById(enrollmentRequestId);

        return Objects.equals(enrollmentRequest.getStudent().getId(), user.getId());
    }

    public boolean canUpdateEnrollmentRequest(Long enrollmentRequestId) {
        User user = getLoggedInUser();
        EnrollmentRequest enrollmentRequest = enrollmentRequestService.getEnrollmentRequestById(enrollmentRequestId);

        return Objects.equals(enrollmentRequest.getCourse().getTeacher().getId(), user.getId());
    }

    public boolean courseBelongsToUser(Long courseId) {
        User user = getLoggedInUser();
        Course course = courseService.getCourseById(courseId);

        return Objects.equals(course.getTeacher().getId(), user.getId());
    }

    public boolean canUpdateGrade(Long gradeId) {
        User user = getLoggedInUser();
        Grade grade = gradeService.getGradeById(gradeId);
        return Objects.equals(grade.getCourse().getTeacher().getId(), user.getId());
    }

    public boolean canViewGrade(Long gradeId) {
        User user = getLoggedInUser();
        Grade grade = gradeService.getGradeById(gradeId);
        if (user.getRoles().contains(UserType.STUDENT)) {
            return Objects.equals(grade.getStudent().getId(), user.getId());
        } else if (user.getRoles().contains(UserType.TEACHER)) {
            return Objects.equals(grade.getCourse().getTeacher().getId(), user.getId());
        }
        return false;
    }

    public boolean canUpdateSchedule(Long scheduleId) {
        User user = getLoggedInUser();
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return Objects.equals(schedule.getCourse().getTeacher().getId(), user.getId());
    }

    private static User getLoggedInUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
