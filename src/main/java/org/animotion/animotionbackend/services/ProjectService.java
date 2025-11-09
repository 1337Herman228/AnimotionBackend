package org.animotion.animotionbackend.services;


import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.dto.*;
import org.animotion.animotionbackend.entity.*;
import org.animotion.animotionbackend.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final TaskPriorityRepository taskPriorityRepository;
    private final ProjectSecurityService projectSecurityService;
    private final MapperService mapperService;

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
                        .members(Optional.ofNullable(project.getMemberIds())
                                .orElseGet(List::of) // если null, то пустой список
                                .stream()
                                .map(userId -> mapperService.mapUserToMemberDto(
                                        userRepository.findById(userId).orElseThrow()
                                ))
                                .toList())
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
    public FullProjectDto getFullProjectById(String projectId, User currentUser) {
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
        List<TaskPriority> priorities = getProjectAndSharedPriorities(projectId);


        // Convert data to DTOs and assemble the final response
        return buildFullProjectDto(project, members, columns, cards, priorities);
    }

    private FullProjectDto buildFullProjectDto(
            Project project,
            List<User> members,
            List<Column> columns,
            List<Card> cards,
            List<TaskPriority> priorities
    ) {
        // Create a map of cards for quick access by ID
        Map<String, Card> cardMap = cards.stream()
                .collect(Collectors.toMap(Card::getId, Function.identity()));

        // Map members to UserDto
        List<UserDto> memberDtos = members.stream().map(mapperService::mapToUserDto).collect(Collectors.toList());

        Map<String, Column> columnMap = columns.stream()
                .collect(Collectors.toMap(Column::getId, Function.identity()));

        List<Column> sortedColumns = project.getColumnOrder().stream()
                .map(columnMap::get)
                .filter(java.util.Objects::nonNull)
                .toList();

        // Map columns to ColumnDto, embedding the cards within them
        List<ColumnDto> columnDtos = sortedColumns.stream()
                .map(column -> {
                    List<CardDto> cardsInColumn = column.getCardOrder().stream()
                            .map(cardMap::get) // Get card object from map by ID
                            .filter(java.util.Objects::nonNull) // Filter out if a card was deleted but order wasn't updated
                            .map(mapperService::mapToCardDto) // Map entity to DTO
                            .collect(Collectors.toList());

                    return ColumnDto.builder()
                            .id(column.getId())
                            .projectId(column.getProjectId())
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
                .columnOrder(project.getColumnOrder())
                .priorities(priorities)
                .build();
    }

    @Transactional
    public void updateColumnOrder(String projectId, UpdateColumnOrderRequest request) {
        // Используем ваш сервис безопасности для проверки прав
        projectSecurityService.checkAccess(projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        project.setColumnOrder(request.getColumnOrder());
        projectRepository.save(project);
    }



    public List<TaskPriority> getProjectAndSharedPriorities(String projectId) {
        List<TaskPriority> sharedPriorities = taskPriorityRepository.findByProjectIdNull();
        List<TaskPriority> projectPriorities = taskPriorityRepository.findByProjectId(projectId);
        List<TaskPriority> unitedPriorities = new ArrayList<>();
        unitedPriorities.addAll(sharedPriorities);
        unitedPriorities.addAll(projectPriorities);
        return unitedPriorities;
    }



    /**
     * Creates a new project for the currently authenticated user.
     * Also initializes the project with default columns.
     *
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

        return mapperService.mapToProjectSummaryDto(savedProject);
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

}
