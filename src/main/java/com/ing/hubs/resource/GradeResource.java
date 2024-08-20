package com.ing.hubs.resource;

import com.ing.hubs.dto.GradeDto;
import com.ing.hubs.dto.GradeUpdateRequestDto;
import com.ing.hubs.service.GradeService;
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
public class GradeResource {
    private GradeService gradeService;
    @PostMapping("/grades")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_TEACHER")
    @PreAuthorize("@permissionService.courseBelongsToUser(#gradeDto.getCourseId())")
    public GradeDto createGrade(@RequestBody @Valid GradeDto gradeDto) {
        return gradeService.createGrade(gradeDto);
    }

    @GetMapping("/students/me/grades")
    @Secured("ROLE_STUDENT")
    public List<GradeDto> getGradesForStudent() {
        return gradeService.findGradesForCurrentUser();
    }

    @PatchMapping("/grades/{gradeId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Secured("ROLE_TEACHER")
    @PreAuthorize("@permissionService.canUpdateGrade(#gradeId)")
    public GradeDto updateGrade(@PathVariable Long gradeId, @Valid @RequestBody GradeUpdateRequestDto gradeUpdateRequestDto) {
        return gradeService.updateGrade(
                gradeUpdateRequestDto.getScore(),
                gradeId
        );
    }

    @GetMapping("/courses/{courseId}/grades")
    @Secured("ROLE_TEACHER")
    @PreAuthorize("@permissionService.courseBelongsToUser(#courseId)")
    public List<GradeDto> getGradesForCourse(@PathVariable Long courseId,
                                             @RequestParam(required = false) Float lowerBound,
                                             @RequestParam(required = false) Float upperBound) {
        return gradeService.findGradesByCourseId(courseId, lowerBound, upperBound);
    }

    @GetMapping("/grades/{gradeId}")
    @PreAuthorize("@permissionService.canViewGrade(#gradeId)")
    public GradeDto getGradeById(@PathVariable Long gradeId) {
        return gradeService.getGradeDtoById(gradeId);
    }

}
