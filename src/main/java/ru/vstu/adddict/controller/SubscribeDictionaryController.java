package ru.vstu.adddict.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vstu.adddict.dto.subscribedictionary.CreateSubscribeDictionaryRequestDto;
import ru.vstu.adddict.dto.subscribedictionary.SubscribeDictionaryResponseDto;
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
}
