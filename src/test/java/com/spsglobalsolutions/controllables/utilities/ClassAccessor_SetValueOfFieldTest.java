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

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Test of {@link ClassAccessor#setValueOfField(Class, String, Object, Object)} and {@link ClassAccessor#setValueOfField(String, Object, Object)}
 *
 * @author stevo58008
 */
public class ClassAccessor_SetValueOfFieldTest {

    private final Random random = new Random();


    @Test
    public void testSetValueOfStringField() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final String expectedValue = UUID.randomUUID().toString();
        ClassAccessor.setValueOfField(ClassWithFields.class, "aPrivateString", toAccess, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, toAccess.getaPrivateString());
    }

    @Test
    public void testSetValueOfPrimitiveLongField() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final long expectedValue = random.nextLong();
        ClassAccessor.setValueOfField(ClassWithFields.class, "aPrivateLongPrimitive", toAccess, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, toAccess.getaPrivateLongPrimitive());
    }

    @Test
    public void testSetValueOfObjectLongField() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final Long expectedValue = random.nextLong();
        ClassAccessor.setValueOfField(ClassWithFields.class, "aPrivateLongObject", toAccess, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, toAccess.getaPrivateLongObject());
    }

    @Test
    public void testSetValueOfObjectField() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final Calendar expectedValue = Calendar.getInstance();
        ClassAccessor.setValueOfField(ClassWithFields.class, "aPrivateObject", toAccess, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, toAccess.getaPrivateObject());
    }

    @Test
    public void testSetValueOfTypedListField() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final List<String> expectedValue = new ArrayList<>();
        ClassAccessor.setValueOfField(ClassWithFields.class, "aPrivateListOfStrings", toAccess, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, toAccess.getaPrivateListOfStrings());
    }

    @Test
    public void testSetValueOfFieldWhenFieldIsAStaticString() throws Exception {
        final String expectedValue = UUID.randomUUID().toString();
        ClassAccessor.setValueOfField(ClassWithFields.class, "aPrivateStaticString", null, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, ClassWithFields.getaPrivateStaticString());
    }

    @Test(expected = NoSuchFieldException.class)
    public void testSetValueOfFieldWhenFieldNameIsIncorrect() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        ClassAccessor.setValueOfField(ClassWithFields.class, "thisFieldDoesNotExist", toAccess, "doesnt really matter here");
    }

    @Test
    public void testSetValueOfFieldShortVersion() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final String expectedValue = UUID.randomUUID().toString();
        ClassAccessor.setValueOfField("aPrivateString", toAccess, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, toAccess.getaPrivateString());
    }

    @Test
    public void testSetValueOfFieldWhenFieldIsPublic() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final String expectedValue = UUID.randomUUID().toString();
        ClassAccessor.setValueOfField(ClassWithFields.class, "aPublicString", toAccess, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, toAccess.getaPublicString());
    }

    @Test
    public void testSetValueOfFieldWhenFieldIsProtected() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final String expectedValue = UUID.randomUUID().toString();
        ClassAccessor.setValueOfField(ClassWithFields.class, "aProtectedString", toAccess, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, toAccess.getaProtectedString());
    }

    @Test
    public void testSetValueOfFieldWhenFieldIsPrivateAndFinal() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final Calendar expectedValue = Calendar.getInstance();
        ClassAccessor.setValueOfField(ClassWithFields.class, "aPrivateFinalCalendar", toAccess, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, toAccess.getaPrivateFinalCalendar());
    }

    @Test
    public void testSetValueOfFieldWhenFieldIsFinalStatic() throws Exception {
        final Calendar expectedValue = Calendar.getInstance();
        ClassAccessor.setValueOfField(ClassWithFields.class, "A_PRIVATE_FINAL_STATIC_OBJECT", null, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, ClassWithFields.getaPrivateFinalStaticObject());
    }

    @Test(expected = NoSuchFieldException.class)
    public void testSetValueOfFieldWhenFieldIsDeclaredOnInheritedClassButUseChildClassAsClassDeclaredOnThenShouldThrowException() throws Exception {
        final ClassWhichInheritsFields toAccess = new ClassWhichInheritsFields();
        ClassAccessor.setValueOfField(ClassWhichInheritsFields.class, "aProtectedString", toAccess, "not relevant");
    }

    @Test(expected = NoSuchFieldException.class)
    public void testSetValueOfFieldShortVersionWhenFieldIsDeclaredOnInheritedClassThenShouldThrowException() throws Exception {
        final ClassWhichInheritsFields toAccess = new ClassWhichInheritsFields();
        ClassAccessor.setValueOfField("aProtectedString", toAccess, "not relevant");
    }

    @Test
    public void testSetValueOfFieldWhenFieldIsDeclaredOnInheritedClassButUseParentClassAsClassDeclaredOn() throws Exception {
        final ClassWhichInheritsFields toAccess = new ClassWhichInheritsFields();
        final String expectedValue = UUID.randomUUID().toString();
        ClassAccessor.setValueOfField(ClassWithFields.class, "aProtectedString", toAccess, expectedValue);
        assertEquals("The value of the field isn't what it was set to", expectedValue, toAccess.getaProtectedString());
    }
}
