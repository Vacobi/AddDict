package ru.vstu.adddict.controller;

import lombok.RequiredArgsConstructor;
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
}
