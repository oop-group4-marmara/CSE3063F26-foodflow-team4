package edu.marmara.cse3063.foodflow.junittest;

import org.junit.jupiter.api.Assertions.assertEquals;

import edu.marmara.cse3063.foodflow.javatest.javaTest;

import java.beans.Transient;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;


@Tag("fast")
class junitTest {

  @Test 
  @DisplayName("First JUnit test.")
  void testTheSmokeTest(TestInfo testInfo) {
    javaTest sumTest = new javaTest();
    assertEquals(5, sumTest.sumTest(2,3), "2 + 3 should equal 5");
    assertEquals("first JUnit test - ", testInfo,getDisplayName(), () -> "TestInfo is injected correctly");
  }
}