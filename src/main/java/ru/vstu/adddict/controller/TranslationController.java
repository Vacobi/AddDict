package ru.vstu.adddict.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vstu.adddict.dto.*;
import ru.vstu.adddict.dto.translation.GetDictionaryTranslationsRequestDto;
import ru.vstu.adddict.dto.translation.GetDictionaryTranslationsResponseDto;
import ru.vstu.adddict.dto.translation.*;
import ru.vstu.adddict.mapper.TranslationMapper;
import ru.vstu.adddict.service.TranslationService;

import java.util.List;

@Controller
@RestController
@RequestMapping("api/v1/dictionaries")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    private final TranslationMapper translationMapper;

    @GetMapping("/{dictionaryId}/words/{translationId}")
    public TranslationResponseDto getTranslation(
            @PathVariable Long dictionaryId,
            @PathVariable Long translationId,
            @RequestAttribute(value = "x-user-id", required = false) Long userId
    ) {
        GetTranslationRequestDto requestDto = GetTranslationRequestDto.builder()
                .dictionaryId(dictionaryId)
                .translationId(translationId)
                .build();

        TranslationDto translationDto = translationService.getTranslation(requestDto, userId);

        return translationMapper.toTranslationResponseDto(translationDto);
    }

    @PostMapping("/{dictionaryId}/words")
    public TranslationResponseDto createTranslation(
            @RequestBody CreateTranslationRequestDto requestDto,
            @PathVariable Long dictionaryId,
            @RequestAttribute(value = "x-user-id") Long userId
    ) {
        requestDto.setDictionaryId(dictionaryId);
        requestDto.setRequestSenderId(userId);

        TranslationDto translationDto = translationService.createTranslation(requestDto);

        return translationMapper.toTranslationResponseDto(translationDto);
    }

    @GetMapping("/{dictionaryId}/words")
    public GetDictionaryTranslationsResponseDto<TranslationResponseDto> getTranslations(
            @PathVariable Long dictionaryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestAttribute(value = "x-user-id", required = false) Long userId
    ) {
        GetDictionaryTranslationsRequestDto requestDto = GetDictionaryTranslationsRequestDto.builder()
                .dictionaryId(dictionaryId)
                .userId(userId)
                .page(page)
                .build();

        GetDictionaryTranslationsResponseDto<TranslationDto> translationsInDictionaryDto
                = translationService.getTranslations(requestDto);

        PageResponseDto<TranslationResponseDto> pageResponse = translationMapper.fromPageResponseDto(
                translationsInDictionaryDto.getPage(),
                translationMapper::toTranslationResponseDto
        );

        return GetDictionaryTranslationsResponseDto.<TranslationResponseDto>builder()
                .dictionaryId(translationsInDictionaryDto.getDictionaryId())
                .page(pageResponse)
                .build();
    }

    @PutMapping("/{dictionaryId}/words/{translationId}")
    public TranslationResponseDto updateTranslation(
            @PathVariable Long dictionaryId,
            @RequestBody UpdateTranslationRequestDto requestDto,
            @RequestAttribute(value = "x-user-id") Long userId
    ) {
        requestDto.setRequestSenderId(userId);
        TranslationDto translationDto = translationService.updateTranslation(requestDto, dictionaryId, userId);

        return translationMapper.toTranslationResponseDto(translationDto);
    }

    @DeleteMapping("/{dictionaryId}/words/{translationId}")
    public ResponseEntity<Object> deleteDictionary(
            @PathVariable Long dictionaryId,
            @PathVariable Long translationId,
            @RequestAttribute("x-user-id") Long userId
    ) {
        translationService.deleteTranslation(dictionaryId, translationId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/words/shuffle")
    public ShuffleResponseDto<TranslationResponseDto> getShuffle(
            @RequestParam List<Long> dictionaryIds,
            @RequestParam(defaultValue = "") String seed,
            @RequestParam(defaultValue = "0") int page,
            @RequestAttribute(value = "x-user-id") Long userId
    ) {
        ShuffleRequestDto shuffleRequestDto = ShuffleRequestDto.builder()
                .dictionaryIds(dictionaryIds)
                .seed(seed)
                .page(page)
                .userId(userId)
                .build();

        ShuffleResponseDto<TranslationDto> translationsInDictionaryDto
                = translationService.getShuffledTranslations(shuffleRequestDto);

        PageResponseDto<TranslationResponseDto> pageResponse = translationMapper.fromPageResponseDto(
                translationsInDictionaryDto.getPage(),
                translationMapper::toTranslationResponseDto
        );

        return ShuffleResponseDto.<TranslationResponseDto>builder()
                .userId(translationsInDictionaryDto.getUserId())
                .seed(translationsInDictionaryDto.getSeed())
                .page(pageResponse)
                .build();
    }
}
