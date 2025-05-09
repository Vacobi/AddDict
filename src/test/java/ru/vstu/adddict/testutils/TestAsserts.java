package ru.vstu.adddict.testutils;

import ru.vstu.adddict.dto.DictionaryDto;

import java.time.LocalDateTime;

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
}
