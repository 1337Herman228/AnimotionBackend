package org.animotion.animotionbackend.services;


import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.dto.*;
import org.animotion.animotionbackend.entity.Card;
import org.animotion.animotionbackend.entity.Column;
import org.animotion.animotionbackend.entity.Project;
import org.animotion.animotionbackend.entity.User;
import org.animotion.animotionbackend.repository.CardRepository;
import org.animotion.animotionbackend.repository.ColumnRepository;
import org.animotion.animotionbackend.repository.ProjectRepository;
import org.animotion.animotionbackend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ColumnRepository columnRepository;
    private final CardRepository cardRepository;
    private final UserService userService;

    /**
     * Gets a list of project summaries for the currently authenticated user.
     *
     * @return a list of ProjectSummaryDto.
     */
    public List<ProjectSummaryDto> getProjectsForCurrentUser() {
        User currentUser = userService.getCurrentUser();

        // Find all projects where the current user's ID is in the memberIds list
        List<Project> projects = projectRepository.findByMemberIdsContains(currentUser.getId());

        return projects.stream()
                .map(project -> ProjectSummaryDto.builder()
                        .id(project.getId())
                        .name(project.getName())
                        .ownerId(project.getOwnerId())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the full details of a single project, including its columns and cards.
     * Throws AccessDeniedException if the current user is not a member of the project.
     *
     * @param projectId The ID of the project to retrieve.
     * @return A FullProjectDto containing all project data.
     */
    public FullProjectDto getFullProjectById(String projectId) {
        User currentUser = userService.getCurrentUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));

        // Verify that the current user is a member of the project.
        if (!project.getMemberIds().contains(currentUser.getId())) {
            throw new AccessDeniedException("User does not have permission to access this project");
        }

        // Fetch all related data in batches to be efficient
        List<User> members = userRepository.findAllById(project.getMemberIds());
        List<Column> columns = columnRepository.findAllByProjectId(projectId);
        List<Card> cards = cardRepository.findAllByProjectId(projectId);

        // Convert data to DTOs and assemble the final response
        return buildFullProjectDto(project, members, columns, cards);
    }

    private FullProjectDto buildFullProjectDto(Project project, List<User> members, List<Column> columns, List<Card> cards) {
        // Create a map of cards for quick access by ID
        Map<String, Card> cardMap = cards.stream()
                .collect(Collectors.toMap(Card::getId, Function.identity()));

        // Map members to UserDto
        List<UserDto> memberDtos = members.stream().map(this::mapToUserDto).collect(Collectors.toList());

        // Map columns to ColumnDto, embedding the cards within them
        List<ColumnDto> columnDtos = columns.stream()
                .map(column -> {
                    List<CardDto> cardsInColumn = column.getCardOrder().stream()
                            .map(cardMap::get) // Get card object from map by ID
                            .filter(java.util.Objects::nonNull) // Filter out if a card was deleted but order wasn't updated
                            .map(this::mapToCardDto) // Map entity to DTO
                            .collect(Collectors.toList());

                    return ColumnDto.builder()
                            .id(column.getId())
                            .title(column.getTitle())
                            .cardOrder(column.getCardOrder())
                            .cards(cardsInColumn)
                            .build();
                })
                .collect(Collectors.toList());

        // Assemble the final DTO
        return FullProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .ownerId(project.getOwnerId())
                .members(memberDtos)
                .columns(columnDtos)
                .build();
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .image(user.getImage())
                .build();
    }

    public CardDto mapToCardDto(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .projectId(card.getProjectId())
                .columnId(card.getColumnId())
                .title(card.getTitle())
                .description(card.getDescription())
                .assigneeId(card.getAssigneeId())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }

    /**
     * Creates a new project for the currently authenticated user.
     * Also initializes the project with default columns.
     * @param request The request containing the project's name.
     * @return A summary DTO of the newly created project.
     */
    @Transactional
    public ProjectSummaryDto createProject(CreateProjectRequest request) {
        User currentUser = userService.getCurrentUser();

        // --- Create and save the new project ---
        Project newProject = new Project();
        newProject.setName(request.getName());
        newProject.setOwnerId(currentUser.getId());
        newProject.setMemberIds(List.of(currentUser.getId())); // The creator is the first member

        Project savedProject = projectRepository.save(newProject);

        // --- Create default columns for the new project ---
        Column colTodo = createDefaultColumn("To Do", savedProject.getId());
        Column colInProgress = createDefaultColumn("In Progress", savedProject.getId());
        Column colDone = createDefaultColumn("Done", savedProject.getId());

        columnRepository.saveAll(List.of(colTodo, colInProgress, colDone));

        return mapToProjectSummaryDto(savedProject);
    }

    // --- Helper method to create a default column ---
    private Column createDefaultColumn(String title, String projectId) {
        Column column = new Column();
        column.setTitle(title);
        column.setProjectId(projectId);
        column.setCardOrder(new ArrayList<>());
        return column;
    }

    // --- Helper method to map a Project to its summary DTO ---
    private ProjectSummaryDto mapToProjectSummaryDto(Project project) {
        return ProjectSummaryDto.builder()
                .id(project.getId())
                .name(project.getName())
                .ownerId(project.getOwnerId())
                .build();
    }
}
