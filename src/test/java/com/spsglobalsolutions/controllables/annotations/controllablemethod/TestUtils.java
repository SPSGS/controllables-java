/*
 * Copyright (c) 2016 SPS Global Solutions Ltd
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.spsglobalsolutions.controllables.annotations.controllablemethod;

import org.junit.Assert;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author stevo58008
 */
public class TestUtils {

    public static long EXPECTED_MS_CALL_TIME_TOLLERENCE = 10; // its hard to guess this, so better to use higher value.

    public static <T> T castTo(Object toCast) {
        try {
            return (T) toCast;
        }
        catch (ClassCastException e) {
            // wrap the exception as we need this to make the test fail
            throw new ControllableMethodException("One of the method arguments was of the incorrect type", e);
        }
    }

    public static void assertValueWithinTollerance(final String description, final long expectedValue, final long actualValue, final long tollerance) {
        Assert.assertTrue(description + " : The actual value (" + actualValue + ") was not within " + tollerance + " of the expected (" + expectedValue + ")",
                          Math.abs(actualValue - expectedValue) <= tollerance);
    }

    public static void assertExpectedMethodCallWithReturnValue(final MethodCall actual, long expectedCallTimeInMillis, Object expectedExitByReturnValue,
                                                               final Object[] expectedArguments) {
        assertExpectedMethodCall(actual, expectedCallTimeInMillis, expectedArguments);
        assertEquals("We expected to return by a return value, but the return value was not as expected", expectedExitByReturnValue, actual.getExit().getByReturnValue());

    }

    public static void assertExpectedMethodCallWithUncheckedException(final MethodCall actual, long expectedCallTimeInMillis, RuntimeException expectedExitByRuntimeException,
                                                                      final Object[] expectedArguments) {
        assertExpectedMethodCall(actual, expectedCallTimeInMillis, expectedArguments);
        assertEquals("We expected to return by an unchecked exception, but the exception value was not as expected", expectedExitByRuntimeException,
                     actual.getExit().getByUncheckedException());
    }

    public static void assertExpectedMethodCallWithCheckedException(final MethodCall actual, long expectedCallTimeInMillis, Exception expectedExitByException,
                                                                    final Object[] expectedArguments) {
        assertExpectedMethodCall(actual, expectedCallTimeInMillis, expectedArguments);
        assertEquals("We expected to return by a checked exception, but the exception value was not as expected", expectedExitByException,
                     actual.getExit().getByCheckedException());
    }

    private static void assertExpectedMethodCall(final MethodCall actual, long expectedCallTimeInMillis, final Object[] expectedArguments) {
        assertValueWithinTollerance("Method call times", expectedCallTimeInMillis, actual.getCallTimeInMillis(), EXPECTED_MS_CALL_TIME_TOLLERENCE);
        assertArrayEquals("The method arguments weren't as expected", expectedArguments, actual.getArguments());
    }

}
