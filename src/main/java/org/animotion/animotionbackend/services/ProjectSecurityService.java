package org.animotion.animotionbackend.services;

import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.entity.Project;
import org.animotion.animotionbackend.entity.User;
import org.animotion.animotionbackend.repository.ProjectRepository;
import org.animotion.animotionbackend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ProjectSecurityService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public User getCurrentUser(Principal principal) {
        String userEmail = principal.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    public void checkWebsocketAccess(String projectId, Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("User is not authenticated");
        }
        // Из Principal мы можем получить имя пользователя (в нашем случае, email)
        String userEmail = principal.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AccessDeniedException("User not found"));

        checkProjectMembership(projectId, currentUser);
    }

    public void checkAccess(String projectId) {
        User currentUser = userService.getCurrentUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        if (!project.getMemberIds().contains(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to access this project");
        }
    }

    private void checkProjectMembership(String projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        if (!project.getMemberIds().contains(user.getId())) {
            throw new AccessDeniedException("User does not have permission to access this project");
        }
    }
}

