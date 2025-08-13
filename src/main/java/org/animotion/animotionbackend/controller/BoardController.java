package org.animotion.animotionbackend.controller;

import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.dto.CardDto;
import org.animotion.animotionbackend.dto.CreateCardRequest;
import org.animotion.animotionbackend.dto.MoveCardRequest;
import org.animotion.animotionbackend.dto.UpdateCardRequest;
import org.animotion.animotionbackend.services.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/cards")
    public ResponseEntity<CardDto> createCard(@RequestBody CreateCardRequest request) {
        return ResponseEntity.ok(boardService.createCard(request));
    }

    @PostMapping("/cards/move")
    public ResponseEntity<Void> moveCard(@RequestBody MoveCardRequest request) {
        boardService.moveCard(request);
        return ResponseEntity.ok().build(); // return 200 OK without body
    }

    @PatchMapping("/cards/{cardId}") // PATCH is suitable for partial updates
    public ResponseEntity<CardDto> updateCard(@PathVariable String cardId, @RequestBody UpdateCardRequest request) {
        return ResponseEntity.ok(boardService.updateCard(cardId, request));
    }

    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable String cardId) {
        boardService.deleteCard(cardId);
        return ResponseEntity.ok().build();
    }

}
