package ru.vstu.adddict.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
}
