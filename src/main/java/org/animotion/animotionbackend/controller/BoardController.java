package org.animotion.animotionbackend.controller;


import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.dto.*;
import org.animotion.animotionbackend.entity.User;
import org.animotion.animotionbackend.services.BoardService;
import org.animotion.animotionbackend.services.ProjectSecurityService;
import org.animotion.animotionbackend.services.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final ProjectService projectService;
    private final ProjectSecurityService projectSecurityService;
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<ProjectSummaryDto>> getUserProjects() {
        return ResponseEntity.ok(projectService.getProjectsForCurrentUser());
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<FullProjectDto> getProjectById(@PathVariable String boardId, Principal principal) {
        User currentUser = projectSecurityService.getCurrentUser(principal);
        return ResponseEntity.ok(projectService.getFullProjectById(boardId, currentUser));
    }

    @PostMapping
    public ResponseEntity<ProjectSummaryDto> createProject(@RequestBody CreateProjectRequest request) {
        ProjectSummaryDto newProject = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProject);
    }

    @PatchMapping("/{boardId}/column-order")
    public ResponseEntity<Void> updateColumnOrder(
            @PathVariable String boardId,
            @RequestBody UpdateColumnOrderRequest request) {
        projectService.updateColumnOrder(boardId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/move-card")
    public ResponseEntity<Map<String, String>> handleMoveCard(@RequestBody MoveCardMessage message, Principal principal) {
        boardService.moveCardAndBroadcast(message, principal);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Card moved successfully."));
    }

    @PutMapping("/move-column")
    public ResponseEntity<Map<String, String>> handleMoveColumn(@RequestBody MoveColumnMessage message, Principal principal) {
        boardService.moveColumnAndBroadcast(message, principal);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Column moved successfully."));
    }

    @PostMapping("/add-card")
    public ResponseEntity<Map<String, String>> addCard(@RequestBody NewCardMessage message, Principal principal) {
        boardService.addCard(message, principal);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Card added successfully."));
    }

    @DeleteMapping("/delete-card/{id}")
    public ResponseEntity<Map<String, String>> deleteCard(@PathVariable String id, Principal principal) {
        boardService.deleteCard(id, principal);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Card deleted successfully."));
    }

    @PutMapping("/edit-card")
    public ResponseEntity<Map<String, String>> editCard(@RequestBody CardDto message, Principal principal) {
        boardService.editCard(message, principal);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Card edited successfully."));
    }

//    @MessageMapping("/add-priority")
//    public void addPriority(@Payload TaskPriority message, Principal principal) {
//        boardService.addProjectPriority(message, principal);
//    }
//
//    @MessageMapping("/change-priority")
//    public void changePriority(@Payload ChangeCardPriorityDto message, Principal principal) {
//        boardService.changeCardPriority(message, principal);
//    }
//


}
