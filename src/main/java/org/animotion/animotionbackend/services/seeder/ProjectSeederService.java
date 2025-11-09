package org.animotion.animotionbackend.services.seeder;

import org.animotion.animotionbackend.entity.*;
import org.animotion.animotionbackend.repository.CardRepository;
import org.animotion.animotionbackend.repository.ColumnRepository;
import org.animotion.animotionbackend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.repository.TaskPriorityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectSeederService {

    private final ProjectRepository projectRepository;
    private final ColumnRepository columnRepository;
    private final CardRepository cardRepository;
    private final TaskPriorityRepository taskPriorityRepository;

    /**
     * Creates a test project with columns and cards for the given users.
     *
     * @param users The list of users to be involved in the project.
     */
    public void seedProjectData(List<User> users) {
        User localUser = users.stream().filter(u -> "user@user.com".equals(u.getEmail())).findFirst().orElseThrow();
        User googleUser = users.stream().filter(u -> "google@user.com".equals(u.getEmail())).findFirst().orElseThrow();

        // 0.1. Create a Priorities
        TaskPriority priority1 = createTaskPriority("MINOR", "Minor", null, "#5a5d63");
        TaskPriority priority2 = createTaskPriority("NORMAL", "Normal", null, "#b8e7bc");
        TaskPriority priority3 = createTaskPriority("MAJOR", "Major", null, "#f5d273");
        TaskPriority priority4 = createTaskPriority("CRITICAL", "Critical", null, "#ee4ba7");

        taskPriorityRepository.saveAll(Arrays.asList(priority1, priority2, priority3, priority4));

        // 1. Create a Project
        Project mainProject = new Project();
        mainProject.setName("Animotion v1.0 Release");
        mainProject.setOwnerId(localUser.getId());
        mainProject.setMemberIds(Arrays.asList(localUser.getId(), googleUser.getId()));
        projectRepository.save(mainProject);

        // 2. Create Columns
        Column colTodo = createColumn("To Do", mainProject.getId());
        Column colInProgress = createColumn("In Progress", mainProject.getId());
        Column colDone = createColumn("Done", mainProject.getId());
        columnRepository.saveAll(Arrays.asList(colTodo, colInProgress, colDone));

        // 3. Create Cards
        Card card1 = createCard("Configure Spring Security", "Add JWT and basic security config", mainProject.getId(), colTodo.getId(), List.of(localUser.getId()) , priority1);
        Card card2 = createCard("Implement OAuth2 Client", "Connect Google authentication", mainProject.getId(), colTodo.getId(), List.of(googleUser.getId()) , priority2);
        Card card3 = createCard("Build UI in React", "Use Tailwind CSS for styling", mainProject.getId(), colInProgress.getId(), List.of(localUser.getId()), priority3);
        Card card4 = createCard("Write API documentation", "", mainProject.getId(), colDone.getId(), null, priority4);

        List<Card> savedCards = cardRepository.saveAll(Arrays.asList(card1, card2, card3, card4));

        // 4. Update card order in columns
        updateCardOrderInColumn(colTodo, savedCards);
        updateCardOrderInColumn(colInProgress, savedCards);
        updateCardOrderInColumn(colDone, savedCards);
        columnRepository.saveAll(Arrays.asList(colTodo, colInProgress, colDone));

        mainProject.setColumnOrder(List.of(colTodo.getId(), colInProgress.getId(), colDone.getId()));
        projectRepository.save(mainProject);
    }

    // --- Helper Methods ---
    private Column createColumn(String title, String projectId) {
        Column column = new Column();
        column.setTitle(title);
        column.setProjectId(projectId);
        column.setCardOrder(new ArrayList<>());
        return column;
    }

    private Card createCard(String title, String description, String projectId, String columnId, List<String> assigneeId, TaskPriority priority) {
        Card card = Card.builder().build();
        card.setTitle(title);
        card.setDescription(description);
        card.setProjectId(projectId);
        card.setColumnId(columnId);
        card.setAppointedMembersId(assigneeId);
        card.setPriority(priority);
        return card;
    }

    private TaskPriority createTaskPriority(String value, String label, String projectId, String color) {
        TaskPriority priority = new TaskPriority();
        priority.setValue(value);
        priority.setLabel(label);
        priority.setProjectId(projectId);
        priority.setColor(color);
        return priority;
    }

    private void updateCardOrderInColumn(Column column, List<Card> cards) {
        List<String> cardIds = cards.stream()
                .filter(c -> c.getColumnId().equals(column.getId()))
                .map(Card::getId)
                .collect(Collectors.toList());
        column.setCardOrder(cardIds);
    }
}
