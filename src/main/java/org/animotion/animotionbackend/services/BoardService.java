package org.animotion.animotionbackend.services;

import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.dto.CardDto;
import org.animotion.animotionbackend.dto.CreateCardRequest;
import org.animotion.animotionbackend.dto.MoveCardRequest;
import org.animotion.animotionbackend.dto.UpdateCardRequest;
import org.animotion.animotionbackend.entity.Card;
import org.animotion.animotionbackend.entity.Column;
import org.animotion.animotionbackend.repository.CardRepository;
import org.animotion.animotionbackend.repository.ColumnRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final ProjectService projectService;
    private final ProjectSecurityService projectSecurityService;

    @Transactional
    public void moveCard(MoveCardRequest request) {
        Card card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        projectSecurityService.checkAccess(card.getProjectId());

        // --- Same column ---
        if (request.getSourceColumnId().equals(request.getDestinationColumnId())) {
            Column column = columnRepository.findById(request.getSourceColumnId()).orElseThrow(() -> new IllegalArgumentException("Column not found"));

            List<String> cardOrder = column.getCardOrder();
            cardOrder.remove(request.getCardId());
            cardOrder.add(request.getDestinationIndex(), request.getCardId());

            columnRepository.save(column);
        }
        // --- Different columns ---
        else {
            Column sourceColumn = columnRepository.findById(request.getSourceColumnId()).orElseThrow(() -> new IllegalArgumentException("Source column not found"));
            Column destColumn = columnRepository.findById(request.getDestinationColumnId()).orElseThrow(() -> new IllegalArgumentException("Destination column not found"));

            sourceColumn.getCardOrder().remove(request.getCardId());
            destColumn.getCardOrder().add(request.getDestinationIndex(), request.getCardId());

            card.setColumnId(request.getDestinationColumnId());

            cardRepository.save(card);
            columnRepository.saveAll(List.of(sourceColumn, destColumn));
        }
    }

    /**
     * Creates a new card and adds it to the end of the specified column.
     * @param request The request containing card details.
     * @return A DTO of the newly created card.
     */
    @Transactional
    public CardDto createCard(CreateCardRequest request) {
        // --- SECURITY CHECK ---
        projectSecurityService.checkAccess(request.getProjectId());

        Column column = columnRepository.findById(request.getColumnId())
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        // --- BUSINESS LOGIC ---
        Card newCard = new Card();
        newCard.setTitle(request.getTitle());
        newCard.setColumnId(request.getColumnId());
        newCard.setProjectId(request.getProjectId());
        // You can set other defaults here if needed (e.g., assignee)

        Card savedCard = cardRepository.save(newCard);

        // Add the new card's ID to the column's order list
        column.getCardOrder().add(savedCard.getId());
        columnRepository.save(column);

        return projectService.mapToCardDto(savedCard); // Reuse the mapper from ProjectService
    }

    /**
     * Updates a card's title and/or description.
     * @param cardId The ID of the card to update.
     * @param request The request with the new details.
     * @return The updated card DTO.
     */
    @Transactional
    public CardDto updateCard(String cardId, UpdateCardRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        // --- SECURITY CHECK ---
        projectSecurityService.checkAccess(card.getProjectId());

        // --- BUSINESS LOGIC ---
        if (request.getTitle() != null) {
            card.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            card.setDescription(request.getDescription());
        }

        Card updatedCard = cardRepository.save(card);
        return projectService.mapToCardDto(updatedCard);
    }

    /**
     * Deletes a card and removes it from its column's order list.
     * @param cardId The ID of the card to delete.
     */
    @Transactional
    public void deleteCard(String cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        // --- SECURITY CHECK ---
        projectSecurityService.checkAccess(card.getProjectId());

        // --- BUSINESS LOGIC ---
        Column column = columnRepository.findById(card.getColumnId()).orElse(null);

        // If the column exists, remove the card's ID from its order list
        if (column != null) {
            column.getCardOrder().remove(cardId);
            columnRepository.save(column);
        }

        // Finally, delete the card itself
        cardRepository.deleteById(cardId);
    }
}