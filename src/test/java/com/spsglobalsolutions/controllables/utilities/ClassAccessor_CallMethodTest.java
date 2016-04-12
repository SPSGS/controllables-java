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

package com.spsglobalsolutions.controllables.utilities;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test {@link ClassAccessor#callMethod(Class, String, Class[], Object, Object[])} and {@link ClassAccessor#callMethod(String, Class[], Object, Object[])}
 *
 * @author stevo58008
 */
public class ClassAccessor_CallMethodTest {

    @Test
    public void testPrivateStaticMethodVoidReturnNoArgs() throws Exception {
        assertNull("The return type of a void method should be null", ClassAccessor.callMethod(ClassWithMethods.class, "privateStaticMethodVoidReturnNoArgs", null, null, null));
    }

    @Test
    public void testPublicVoidMethodWithNoArgs() throws Exception {
        assertNull("The return type of a void method should be null", ClassAccessor.callMethod("publicMethodVoidReturnNoArgs", null, new ClassWithMethods(), null));
    }

    @Test
    public void testPackageMethodVoidReturnNoArgs() throws Exception {
        assertNull("The return type of a void method should be null", ClassAccessor.callMethod("packageMethodVoidReturnNoArgs", null, new ClassWithMethods(), null));
    }

    @Test
    public void testProtectedMethodVoidReturnNoArgs() throws Exception {
        assertNull("The return type of a void method should be null", ClassAccessor.callMethod("protectedMethodVoidReturnNoArgs", null, new ClassWithMethods(), null));
    }

    @Test
    public void testPrivateMethodVoidReturnNoArgs() throws Exception {
        assertNull("The return type of a void method should be null", ClassAccessor.callMethod("privateMethodVoidReturnNoArgs", null, new ClassWithMethods(), null));
    }

    @Test
    public void testPrivateMethodPrimitiveLongReturnNoArgs() throws Exception {
        final long expected = 1;
        assertEquals(expected, ClassAccessor.callMethod("privateMethodPrimitiveLongReturnNoArgs", null, new ClassWithMethods(), null));
    }

    @Test
    public void testPrivateMethodObjectLongReturnNoArgs() throws Exception {
        final Long expected = Long.valueOf(1);
        assertEquals(expected, ClassAccessor.callMethod("privateMethodObjectLongReturnNoArgs", null, new ClassWithMethods(), null));
    }

    @Test
    public void testPrivateMethodPrimitiveLongArrayReturnNoArgs() throws Exception {
        final long[] expected = new long[]{1, 2, 3};
        final long[] actual = ClassAccessor.callMethod("privateMethodPrimitiveLongArrayReturnNoArgs", null, new ClassWithMethods(), null);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testPrivateMethodObjectArrayReturnNoArgs() throws Exception {
        final Object[] expected = new Object[]{"1", "2", "3"};
        final Object[] actual = ClassAccessor.callMethod("privateMethodObjectArrayReturnNoArgs", null, new ClassWithMethods(), null);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testPrivateMethodObjectArrayReturnObjectPrimitiveLongArgs() throws Exception {
        Object[] expected = new Object[]{"aString", 1l};
        Object[] actual = ClassAccessor.callMethod("privateMethodObjectArrayReturnObjectPrimitiveLongArgs",
                                                   new Class[]{Object.class, long.class},
                                                   new ClassWithMethods(),
                                                   expected);
        assertArrayEquals(expected, actual);
    }

    @Test(expected = ClassCastException.class)
    public void testExpectingWrongReturnValueThrowsException() throws Exception {
        final String notAString = ClassAccessor.callMethod("privateMethodPrimitiveLongReturnNoArgs", null, new ClassWithMethods(), null);
    }

    @Test(expected = NoSuchMethodException.class)
    public void testIncorrectMethodNameThrowsException() throws Exception {
        ClassAccessor.callMethod("thisMethodDoesNotExist", null, new ClassWithMethods(), null);
    }

    @Test(expected = NoSuchMethodException.class)
    public void testIncorrectMethodArgumentTypesThrowsException() throws Exception {
        ClassAccessor.callMethod("publicMethodVoidReturnNoArgs", new Class[]{String.class}, new ClassWithMethods(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectMethodArgValuesThrowsException() throws Exception {
        ClassAccessor.callMethod("privateMethodObjectArrayReturnObjectPrimitiveLongArgs", new Class[]{Object.class, long.class}, new ClassWithMethods(), new Object[]{"notAnObject", "notALong"});
    }
}
