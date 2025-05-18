package ru.vstu.adddict.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vstu.adddict.dto.PageResponseDto;
import ru.vstu.adddict.dto.subscribedictionary.*;
import ru.vstu.adddict.mapper.SubscribeDictionaryMapper;
import ru.vstu.adddict.service.SubscribeDictionaryService;

@Controller
@RestController
@RequestMapping("api/v1/subscribe/dictionary")
@RequiredArgsConstructor
public class SubscribeDictionaryController {

    private final SubscribeDictionaryService dictionarySubscribesService;

    private final SubscribeDictionaryMapper subscribeDictionaryMapper;

    @PostMapping
    public SubscribeDictionaryResponseDto subscribeDictionary(
            @RequestBody CreateSubscribeDictionaryRequestDto requestDto,
            @RequestAttribute(value = "x-user-id") Long userId
    ) {
        requestDto.setUserId(userId);

        return subscribeDictionaryMapper.toSubscribeDictionaryResponseDto(dictionarySubscribesService.subscribeDictionary(requestDto));
    }

    @DeleteMapping("/subscribe/{id}")
    public ResponseEntity<Object> unsubscribeBySubscribeId(
            @PathVariable Long id,
            @RequestAttribute(value = "x-user-id") Long userId
    ) {
        dictionarySubscribesService.unsubscribeToDictionary(id, userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{dictionaryId}")
    public ResponseEntity<Object> unsubscribeByDictionaryId(
            @PathVariable Long dictionaryId,
            @RequestAttribute(value = "x-user-id") Long userId
    ) {
        dictionarySubscribesService.unsubscribeToDictionaryByDictionaryId(dictionaryId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list/me")
    public GetUserSubscribesDictionariesResponseDto<SubscribeDictionaryResponseDto> getMySubscribesForDictionaries(
            @RequestAttribute("x-user-id") Long userId,
            @RequestParam(defaultValue = "0") int page
    ) {
        GetUserSubscribesDictionariesResponseDto<SubscribeDictionaryDto> subscribes =
                dictionarySubscribesService.getUserSubscribes(
                        new GetUserSubscribesDictionariesRequestDto(userId, userId, page)
                );

        PageResponseDto<SubscribeDictionaryResponseDto> pageResponse = subscribeDictionaryMapper.fromPageResponseDto(
                subscribes.getPage(),
                subscribeDictionaryMapper::toSubscribeDictionaryResponseDto
        );

        return GetUserSubscribesDictionariesResponseDto.<SubscribeDictionaryResponseDto>builder()
                .userId(subscribes.getUserId())
                .page(pageResponse)
                .build();
    }
}
