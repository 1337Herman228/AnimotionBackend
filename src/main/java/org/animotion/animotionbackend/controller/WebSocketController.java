package org.animotion.animotionbackend.controller;


import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.config.WebSocketReply;
import org.animotion.animotionbackend.dto.*;
import org.animotion.animotionbackend.entity.TaskPriority;
import org.animotion.animotionbackend.services.BoardService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import java.security.Principal;


@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final BoardService boardService;

    // "/app/board/move-card"
    @MessageMapping("/board/move-card")
    @SendToUser("/queue/replies")
    public WebSocketReply handleMoveCard(@Payload MoveCardMessage message, Principal principal) {
        try {
            boardService.moveCardAndBroadcast(message, principal);
            return WebSocketReply.success(message.getCorrelationId(), "Card moved successfully");

        } catch (Exception e) {
            return WebSocketReply.error(message.getCorrelationId(), e.getMessage());
        }
    }

    @MessageMapping("/board/move-column")
    public void handleMoveColumn(@Payload MoveColumnMessage message, Principal principal) {
        boardService.moveColumnAndBroadcast(message, principal);
    }

    @MessageMapping("/board/add-card")
    public void addCard(@Payload NewCardMessage message, Principal principal) {
        boardService.addCard(message, principal);
    }

    @MessageMapping("/board/delete-card")
    public void deleteCard(@Payload DeleteCardMessage message, Principal principal) {
        boardService.deleteCard(message, principal);
    }

    @MessageMapping("/board/add-priority")
    public void addPriority(@Payload TaskPriority message, Principal principal) {
        boardService.addProjectPriority(message, principal);
    }

    @MessageMapping("/board/change-priority")
    public void changePriority(@Payload ChangeCardPriorityDto message, Principal principal) {
        boardService.changeCardPriority(message, principal);
    }

    @MessageMapping("/board/edit-card")
    public void editPriority(@Payload CardDto message, Principal principal) {
        boardService.changeTaskCard(message, principal);
    }

}
