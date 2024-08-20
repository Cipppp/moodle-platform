package com.ing.hubs.resource;

import com.ing.hubs.dto.EnrollmentRequestResponseDto;
import com.ing.hubs.dto.EnrollmentRequestUpdateDto;
import com.ing.hubs.entity.EnrollmentRequestStatus;
import com.ing.hubs.service.EnrollmentRequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class EnrollmentRequestResource {
    private EnrollmentRequestService enrollmentRequestService;

    @PostMapping("/enrollment-requests/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_STUDENT")
    public EnrollmentRequestResponseDto createEnrollmentRequest(@PathVariable Long courseId) {
        return enrollmentRequestService.createEnrollmentRequest(courseId);
    }

    @GetMapping("/students/me/enrollment-requests")
    @Secured("ROLE_STUDENT")
    public List<EnrollmentRequestResponseDto> getEnrollmentRequestsForStudent(@RequestParam(value = "status", required = false) EnrollmentRequestStatus status) {
        return enrollmentRequestService.getEnrollmentRequestsForStudentByStatus(status);
    }

    @GetMapping("/courses/{courseId}/enrollment-requests")
    @Secured("ROLE_TEACHER")
    @PreAuthorize("@permissionService.courseBelongsToUser(#courseId)")
    public List<EnrollmentRequestResponseDto> getEnrollmentRequestsForCourse(@PathVariable Long courseId, @RequestParam(value = "status", required = false) EnrollmentRequestStatus status) {
        return enrollmentRequestService.getEnrollmentRequestsForCourseByStatus(courseId, status);
    }

    @PatchMapping("/enrollment-requests/{enrollmentRequestId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Secured("ROLE_TEACHER")
    @PreAuthorize("@permissionService.canUpdateEnrollmentRequest(#enrollmentRequestId)")
    public EnrollmentRequestResponseDto updateEnrollmentRequestStatus(@RequestBody @Valid EnrollmentRequestUpdateDto enrollmentRequestUpdateDto, @PathVariable Long enrollmentRequestId) {
        return enrollmentRequestService.updateEnrollmentRequestStatus(enrollmentRequestUpdateDto, enrollmentRequestId);
    }

    @DeleteMapping("/enrollment-requests/{enrollmentRequestId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Secured("ROLE_STUDENT")
    @PreAuthorize("@permissionService.canDeleteEnrollmentRequest(#enrollmentRequestId)")
    public void deleteEnrollmentRequest(@PathVariable Long enrollmentRequestId) {
        enrollmentRequestService.deleteEnrollmentRequest(enrollmentRequestId);
    }

}
