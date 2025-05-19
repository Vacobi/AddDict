package ru.vstu.adddict.testutils;

import ru.vstu.adddict.dto.dictionary.DictionaryDto;
import ru.vstu.adddict.dto.subscribedictionary.SubscribeDictionaryDto;
import ru.vstu.adddict.dto.translation.TranslationDto;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.vstu.adddict.util.TimeUtil.localDateTimeAreEquals;

public class TestAsserts {

    public static void assertDictionariesDtoEquals(DictionaryDto expected, DictionaryDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getIsPublic(), actual.getIsPublic());
        assertLocalDateTimeEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getAuthorId(), actual.getAuthorId());
    }

    private static void assertLocalDateTimeEquals(LocalDateTime expected, LocalDateTime actual) {
        if (expected != null && actual != null) {
            assertTrue(localDateTimeAreEquals(expected, actual));
        } else if (expected == null && actual == null) {
            assertTrue(true);
        } else {
            fail();
        }
    }

    public static void assertTranslationsDtoEquals(TranslationDto expected, TranslationDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTranslationText(), actual.getTranslationText());
        assertEquals(expected.getOriginText(), actual.getOriginText());
        assertEquals(expected.getDictionaryId(), actual.getDictionaryId());
    }

    public static void assertSubscribeDictionaryDtoEquals(SubscribeDictionaryDto expected, SubscribeDictionaryDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getDictionaryId(), actual.getDictionaryId());
    }

    public static void assertListOfTranslationsContainsSameTranslations(List<TranslationDto> expected, List<TranslationDto> actual) {
        assertEquals(expected.size(), actual.size());
        List<TranslationDto> actualCopy = new LinkedList<>(actual);
        for (int i = 0; i < expected.size(); i++) {
            TranslationDto expectedTranslation = expected.get(i);
            boolean match = false;
            for (int j = 0; j < actualCopy.size() && !match; j++) {
                TranslationDto actualTranslation = actualCopy.get(j);
                if (actualTranslation.getId() == expectedTranslation.getId()) {
                    assertTranslationsDtoEquals(expectedTranslation, actualTranslation);
                    actualCopy.remove(j);
                }
                match = true;
            }
            if (!match) {
                fail();
            }
        }
    }
}
