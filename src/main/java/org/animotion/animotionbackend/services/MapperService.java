package org.animotion.animotionbackend.services;

import lombok.RequiredArgsConstructor;
import org.animotion.animotionbackend.dto.CardDto;
import org.animotion.animotionbackend.dto.MemberDto;
import org.animotion.animotionbackend.dto.ProjectSummaryDto;
import org.animotion.animotionbackend.dto.UserDto;
import org.animotion.animotionbackend.entity.Card;
import org.animotion.animotionbackend.entity.Project;
import org.animotion.animotionbackend.entity.User;
import org.animotion.animotionbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MapperService {
    private final UserRepository userRepository;

    public UserDto mapToUserDto(User user) {
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
                .appointedMembers(
                        Optional.ofNullable(card.getAppointedMembersId())
                                .orElseGet(List::of) // если null, то пустой список
                                .stream()
                                .map(userId -> mapUserToMemberDto(
                                        userRepository.findById(userId).orElseThrow()
                                ))
                                .toList()
                ).priority(card.getPriority())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }

    public Card mapCardDtoToCard(CardDto cardDto) {
        return Card.builder()
                .id(cardDto.getId())
                .projectId(cardDto.getProjectId())
                .columnId(cardDto.getColumnId())
                .title(cardDto.getTitle())
                .description(cardDto.getDescription())
                .appointedMembersId(
                        Optional.ofNullable(cardDto.getAppointedMembers())
                                .orElseGet(List::of) // если null, то пустой список
                                .stream()
                                .map(MemberDto::getId)
                                .toList()
                ).priority(cardDto.getPriority())
                .createdAt(cardDto.getCreatedAt())
                .updatedAt(cardDto.getUpdatedAt())
                .build();
    }

    public MemberDto mapUserToMemberDto(User user) {
        return MemberDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .id(user.getId())
                .image(user.getImage())
                .build();
    }

    public ProjectSummaryDto mapToProjectSummaryDto(Project project) {
        return ProjectSummaryDto.builder()
                .id(project.getId())
                .name(project.getName())
                .ownerId(project.getOwnerId())
                .build();
    }
}
