package me.whizvox.skyislands.test;

import me.whizvox.skyislands.util.StringHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringHelperTests {

    @Test
    void snakeCaseToLowerCamelCase() {
        assertEquals("helloWorld", StringHelper.snakeCaseToLowerCamelCase("HELLO_WORLD"));
        assertEquals("helloWorld123", StringHelper.snakeCaseToLowerCamelCase("HELLO_WORLD_123"));
        assertEquals("aReallySuperLongStringWithLotsOfCharacters", StringHelper.snakeCaseToLowerCamelCase("A_REALLY_SUPER_LONG_STRING_WITH_LOTS_OF_CHARACTERS"));
    }

    @Test
    void wordWrap() {
        assertEquals(List.of("Lorem ipsum"), StringHelper.wordWrap("Lorem ipsum", 80));
        assertEquals(List.of("Lorem", "ipsum"), StringHelper.wordWrap("Lorem ipsum", 8));
        assertEquals(List.of("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna", "aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis", "aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint", "occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."), StringHelper.wordWrap("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", 120));
    }

}
