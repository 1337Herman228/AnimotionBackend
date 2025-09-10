package org.animotion.animotionbackend.controller;


import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.dto.CreateProjectRequest;
import org.animotion.animotionbackend.dto.FullProjectDto;
import org.animotion.animotionbackend.dto.ProjectSummaryDto;
import org.animotion.animotionbackend.dto.UpdateColumnOrderRequest;
import org.animotion.animotionbackend.entity.User;
import org.animotion.animotionbackend.services.ProjectSecurityService;
import org.animotion.animotionbackend.services.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectSecurityService projectSecurityService;

    @GetMapping
    public ResponseEntity<List<ProjectSummaryDto>> getUserProjects() {
        return ResponseEntity.ok(projectService.getProjectsForCurrentUser());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<FullProjectDto> getProjectById(@PathVariable String projectId, Principal principal) {
        User currentUser = projectSecurityService.getCurrentUser(principal);
        return ResponseEntity.ok(projectService.getFullProjectById(projectId, currentUser));
    }

    @PostMapping
    public ResponseEntity<ProjectSummaryDto> createProject(@RequestBody CreateProjectRequest request) {
        ProjectSummaryDto newProject = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProject);
    }

    @PatchMapping("/{projectId}/column-order")
    public ResponseEntity<Void> updateColumnOrder(
            @PathVariable String projectId,
            @RequestBody UpdateColumnOrderRequest request) {
        projectService.updateColumnOrder(projectId, request);
        return ResponseEntity.ok().build();
    }

}
