package org.animotion.animotionbackend.services;

import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.dto.*;
import org.animotion.animotionbackend.entity.Card;
import org.animotion.animotionbackend.entity.Column;
import org.animotion.animotionbackend.entity.Project;
import org.animotion.animotionbackend.entity.TaskPriority;
import org.animotion.animotionbackend.repository.CardRepository;
import org.animotion.animotionbackend.repository.ColumnRepository;
import org.animotion.animotionbackend.repository.ProjectRepository;
import org.animotion.animotionbackend.repository.TaskPriorityRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final TaskPriorityRepository taskPriorityRepository;
    private final ProjectSecurityService projectSecurityService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void deleteCard(DeleteCardMessage message, Principal principal) {
        // --- 1. ПРОВЕРКА БЕЗОПАСНОСТИ ---
        projectSecurityService.checkWebsocketAccess(message.getProjectId(), principal);

        // --- 2. БИЗНЕС-ЛОГИКА ---
        cardRepository.deleteById(message.getDeletedCardId());

        Column column = columnRepository.findById(message.getColumnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        List<String> updatedOrder = column.getCardOrder().stream()
                .filter(cardId -> !cardId.equals(message.getDeletedCardId()))
                .toList();
        column.setCardOrder(updatedOrder);
        columnRepository.save(column);

        sendProjectUpdates(message.getProjectId(), principal);
    }

    @Transactional
    public void addCard(NewCardMessage message, Principal principal) {
        // --- 1. ПРОВЕРКА БЕЗОПАСНОСТИ ---
        projectSecurityService.checkWebsocketAccess(message.getProjectId(), principal);

        // --- 2. БИЗНЕС-ЛОГИКА ---
        Card card = Card.builder()
                .title(message.getTitle())
                .description(message.getDescription())
                .columnId(message.getColumnId())
                .projectId(message.getProjectId())
                .appointedMembersId(message.getAssigneeId())
                .build();
        if (message.getPriority() == null)
            card.setPriority(taskPriorityRepository.findByValue("MINOR").orElseThrow());
        else
            card.setPriority(message.getPriority());

        Column column = columnRepository.findById(message.getColumnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        Card savedCard = cardRepository.save(card);

        // Add the new card's ID to the column's order list
        column.getCardOrder().add(savedCard.getId());
        columnRepository.save(column);

        sendProjectUpdates(message.getProjectId(), principal);
    }

    @Transactional
    public void addProjectPriority(TaskPriority newPriority, Principal principal) {
        taskPriorityRepository.save(newPriority);
        sendProjectUpdates(newPriority.getProjectId(), principal);
    }

    @Transactional
    public void changeTaskCard(CardDto cardDto, Principal principal) {
        Card card = cardRepository.findById(cardDto.getId()).get();
        card.setPriority(cardDto.getPriority());
        card.setTitle(cardDto.getTitle());
        card.setDescription(cardDto.getDescription());
        card.setAppointedMembersId(Optional.ofNullable(cardDto.getAppointedMembers())
                .orElseGet(List::of) // если null, то пустой список
                .stream()
                .map(MemberDto::getId)
                .toList());
        sendProjectUpdates(card.getProjectId(), principal);
        card.setProjectId(cardDto.getProjectId());
        card.setColumnId(cardDto.getColumnId());
        cardRepository.save(card);

        sendProjectUpdates(cardDto.getProjectId(), principal);
    }

    @Transactional
    public void changeCardPriority(ChangeCardPriorityDto changeCardPriorityDto, Principal principal) {
        TaskPriority priority = taskPriorityRepository.findById(changeCardPriorityDto.getPriorityId()).get();
        Card card = cardRepository.findById(changeCardPriorityDto.getCardId()).get();
        card.setPriority(priority);
        cardRepository.save(card);
        sendProjectUpdates(card.getProjectId(), principal);
    }

    @Transactional
    public void moveColumnAndBroadcast(MoveColumnMessage message, Principal principal) {
        // --- 1. ПРОВЕРКА БЕЗОПАСНОСТИ ---
        projectSecurityService.checkWebsocketAccess(message.getProjectId(), principal);

        // --- 2. БИЗНЕС-ЛОГИКА ---
        Project project = projectRepository.findById(message.getProjectId()).orElseThrow();
        project.setColumnOrder(message.getColumnOrder());
        projectRepository.save(project);

        sendProjectUpdates(message.getProjectId(), principal);
    }

    @Transactional
    public void moveCardAndBroadcast(MoveCardMessage message, Principal principal) {

        // --- 1. ПРОВЕРКА БЕЗОПАСНОСТИ ---
        projectSecurityService.checkWebsocketAccess(message.getProjectId(), principal);

        // --- 2. БИЗНЕС-ЛОГИКА ---
        Card card = cardRepository.findById(message.getCardId())
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        card.setColumnId(message.getDestinationColumn().getId());
        cardRepository.save(card);

        // Изменяем cardOrder в колонках, между которыми переместили карточку
        Column sourceColumn = columnRepository.findById(message.getSourceColumn().getId()).orElseThrow(() -> new IllegalArgumentException("Column not found"));
        Column destinationColumn = columnRepository.findById(message.getDestinationColumn().getId()).orElseThrow(() -> new IllegalArgumentException("Column not found"));
        sourceColumn.setCardOrder(message.getSourceColumn().getCardOrder());
        destinationColumn.setCardOrder(message.getDestinationColumn().getCardOrder());
        columnRepository.save(sourceColumn);
        columnRepository.save(destinationColumn);

        sendProjectUpdates(message.getProjectId(), principal);
    }

    private void sendProjectUpdates(String projectId, Principal principal) {
        FullProjectDto updatedProjectState = projectService.getFullProjectById(projectId, projectSecurityService.getCurrentUser(principal));
        String destination = "/topic/project/" + projectId;
        messagingTemplate.convertAndSend(destination, updatedProjectState);
    }

}