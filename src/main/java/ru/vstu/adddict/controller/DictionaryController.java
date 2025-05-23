package ru.vstu.adddict.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vstu.adddict.dto.PageResponseDto;
import ru.vstu.adddict.dto.dictionary.*;
import ru.vstu.adddict.mapper.DictionaryMapper;
import ru.vstu.adddict.service.DictionaryService;

@Controller
@RestController
@RequestMapping("api/v1/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {

    private final DictionaryMapper dictionaryMapper;

    private final DictionaryService dictionaryService;

    @GetMapping("/{id}")
    public DictionaryResponseDto getDictionary(
            @PathVariable Long id,
            @RequestAttribute(value = "x-user-id", required = false) Long userId
    ) {
        DictionaryDto dictionaryDto = dictionaryService.getDictionary(new GetDictionaryRequestDto(id, userId));

        return dictionaryMapper.toDictionaryResponseDto(dictionaryDto);
    }

    @PostMapping
    public DictionaryResponseDto createDictionary(
            @RequestBody CreateDictionaryRequestDto requestDto,
            @RequestAttribute(value = "x-user-id") Long userId
    ) {
        requestDto.setAuthorId(userId);
        DictionaryDto noteDto = dictionaryService.createDictionary(requestDto);

        return dictionaryMapper.toDictionaryResponseDto(noteDto);
    }

    @PutMapping("/{id}")
    public DictionaryResponseDto updateDictionary(
            @PathVariable Long id,
            @RequestBody UpdateDictionaryRequestDto requestDto,
            @RequestAttribute("x-user-id") Long userId
    ) {
        requestDto.setRequestSenderId(userId);
        DictionaryDto dictionaryDto = dictionaryService.updateDictionary(id, requestDto);

        return dictionaryMapper.toDictionaryResponseDto(dictionaryDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDictionary(
            @PathVariable Long id,
            @RequestAttribute("x-user-id") Long userId
    ) {
        dictionaryService.deleteDictionary(id, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list/me")
    public GetUserDictionariesResponseDto<DictionaryResponseDto> getMyDictionaries(
            @RequestAttribute("x-user-id") Long userId,
            @RequestParam(defaultValue = "0") int page
    ) {
        GetUserDictionariesResponseDto<DictionaryDto> dictionariesDto = dictionaryService.getUserDictionaries(
                new GetUserDictionariesRequestDto(userId, userId, page)
        );

        PageResponseDto<DictionaryResponseDto> pageResponse = dictionaryMapper.fromPageResponseDto(
                dictionariesDto.getPage(),
                dictionaryMapper::toDictionaryResponseDto
        );

        return GetUserDictionariesResponseDto.<DictionaryResponseDto>builder()
                .userId(dictionariesDto.getUserId())
                .page(pageResponse)
                .build();
    }

    @GetMapping("/list/{userId}")
    public GetUserDictionariesResponseDto<DictionaryResponseDto> getUserDictionaries(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page
    ) {
        GetUserDictionariesResponseDto<DictionaryDto> dictionariesDto = dictionaryService.getUserDictionaries(
                new GetUserDictionariesRequestDto(userId, null, page)
        );

        PageResponseDto<DictionaryResponseDto> pageResponse = dictionaryMapper.fromPageResponseDto(
                dictionariesDto.getPage(),
                dictionaryMapper::toDictionaryResponseDto
        );

        return GetUserDictionariesResponseDto.<DictionaryResponseDto>builder()
                .userId(dictionariesDto.getUserId())
                .page(pageResponse)
                .build();
    }

    @GetMapping("/list/subscribed")
    public GetUserDictionariesResponseDto<DictionaryResponseDto> getUserSubscribedDictionaries(
            @RequestAttribute("x-user-id") Long userId,
            @RequestParam(defaultValue = "0") int page
    ) {
        GetUserDictionariesResponseDto<DictionaryDto> dictionariesDto = dictionaryService.getUserSubscribedDictionaries(
                new GetUserSubscribedDictionariesRequestDto(userId, page)
        );

        PageResponseDto<DictionaryResponseDto> pageResponse = dictionaryMapper.fromPageResponseDto(
                dictionariesDto.getPage(),
                dictionaryMapper::toDictionaryResponseDto
        );

        return GetUserDictionariesResponseDto.<DictionaryResponseDto>builder()
                .userId(dictionariesDto.getUserId())
                .page(pageResponse)
                .build();
    }

    @GetMapping("/list/feed")
    public GetUserDictionariesResponseDto<DictionaryResponseDto> getDictFeedDictionaries(
            @RequestParam(defaultValue = "0") int page
    ) {
        GetUserDictionariesResponseDto<DictionaryDto> dictionariesDto = dictionaryService.getFeedDictionaries(page);

        PageResponseDto<DictionaryResponseDto> pageResponse = dictionaryMapper.fromPageResponseDto(
                dictionariesDto.getPage(),
                dictionaryMapper::toDictionaryResponseDto
        );

        return GetUserDictionariesResponseDto.<DictionaryResponseDto>builder()
                .userId(dictionariesDto.getUserId())
                .page(pageResponse)
                .build();
    }
}
