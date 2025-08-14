package org.animotion.animotionbackend.services.seeder;


import org.animotion.animotionbackend.entity.Card;
import org.animotion.animotionbackend.entity.Column;
import org.animotion.animotionbackend.entity.Project;
import org.animotion.animotionbackend.entity.User;
import org.animotion.animotionbackend.repository.CardRepository;
import org.animotion.animotionbackend.repository.ColumnRepository;
import org.animotion.animotionbackend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
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

    /**
     * Creates a test project with columns and cards for the given users.
     *
     * @param users The list of users to be involved in the project.
     */
    public void seedProjectData(List<User> users) {
        User localUser = users.stream().filter(u -> "user@user.com".equals(u.getEmail())).findFirst().orElseThrow();
        User googleUser = users.stream().filter(u -> "google@user.com".equals(u.getEmail())).findFirst().orElseThrow();

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
        Card card1 = createCard("Configure Spring Security", "Add JWT and basic security config", mainProject.getId(), colTodo.getId(), localUser.getId());
        Card card2 = createCard("Implement OAuth2 Client", "Connect Google authentication", mainProject.getId(), colTodo.getId(), googleUser.getId());
        Card card3 = createCard("Build UI in React", "Use Tailwind CSS for styling", mainProject.getId(), colInProgress.getId(), localUser.getId());
        Card card4 = createCard("Write API documentation", "", mainProject.getId(), colDone.getId(), null);

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

    private Card createCard(String title, String description, String projectId, String columnId, String assigneeId) {
        Card card = new Card();
        card.setTitle(title);
        card.setDescription(description);
        card.setProjectId(projectId);
        card.setColumnId(columnId);
        card.setAssigneeId(assigneeId);
        return card;
    }

    private void updateCardOrderInColumn(Column column, List<Card> cards) {
        List<String> cardIds = cards.stream()
                .filter(c -> c.getColumnId().equals(column.getId()))
                .map(Card::getId)
                .collect(Collectors.toList());
        column.setCardOrder(cardIds);
    }
}
