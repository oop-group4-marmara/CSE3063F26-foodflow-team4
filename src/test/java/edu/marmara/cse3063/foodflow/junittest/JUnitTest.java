package edu.marmara.cse3063.foodflow.junittest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Tag("fast")
class JUnitTest {

  @Test 
  @DisplayName("First JUnit test.")
  void testTheSmokeTest(TestInfo testInfo) {
    assertEquals(5, 2 + 3, "2 + 3 should equal 5");
    assertEquals("First JUnit test.", testInfo.getDisplayName(), "JUnit correctly works.");
  }
}