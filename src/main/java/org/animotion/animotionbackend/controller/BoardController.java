package org.animotion.animotionbackend.controller;

import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.services.BoardService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

//    @PostMapping("/cards")
//    public ResponseEntity<CardDto> createCard(@RequestBody CreateCardRequest request) {
//        return ResponseEntity.ok(boardService.createCard(request));
//    }

//    @PatchMapping("/cards/{cardId}") // PATCH is suitable for partial updates
//    public ResponseEntity<CardDto> updateCard(@PathVariable String cardId, @RequestBody UpdateCardRequest request) {
//        return ResponseEntity.ok(boardService.updateCard(cardId, request));
//    }

//    @DeleteMapping("/cards/{cardId}")
//    public ResponseEntity<Void> deleteCard(@PathVariable String cardId) {
//        boardService.deleteCard(cardId);
//        return ResponseEntity.ok().build();
//    }

}
