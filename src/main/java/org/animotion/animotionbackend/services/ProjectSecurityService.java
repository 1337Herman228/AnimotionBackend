package org.animotion.animotionbackend.services;

import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.entity.Project;
import org.animotion.animotionbackend.entity.User;
import org.animotion.animotionbackend.repository.ProjectRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectSecurityService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    /**
     * Checks if the currently authenticated user is a member of the given project.
     * Throws AccessDeniedException if they are not.
     * @param projectId The ID of the project to check.
     */
    public void checkAccess(String projectId) {
        User currentUser = userService.getCurrentUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        if (!project.getMemberIds().contains(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to access this project");
        }
    }
}

