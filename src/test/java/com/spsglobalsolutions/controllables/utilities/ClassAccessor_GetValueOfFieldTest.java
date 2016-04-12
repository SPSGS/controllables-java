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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ClassAccessor#getValueOfField(Class, String, Object)} and {@link ClassAccessor#getValueOfField(String, Object)}
 *
 * @author stevo58008
 */
public class ClassAccessor_GetValueOfFieldTest {

    @Test
    public void testGetValueOfFieldWhenFieldIsAString() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final String fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "aPrivateString", toAccess);
        assertEquals("value of field incorrect", toAccess.getaPrivateString(), fieldValue);
    }

    @Test
    public void testGetValueOfFieldWhenFieldIsAPrimitiveLong() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final long fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "aPrivateLongPrimitive", toAccess);
        assertEquals("value of field incorrect", toAccess.getaPrivateLongPrimitive(), fieldValue);
    }

    @Test
    public void testGetValueOfFieldWhenFieldIsAnObjectLong() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final Long fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "aPrivateLongObject", toAccess);
        assertEquals("value of field incorrect", toAccess.getaPrivateLongObject(), fieldValue);
    }

    @Test
    public void testGetValueOfFieldWhenFieldIsAnObject() throws Exception {
        ClassWithFields toAccess = new ClassWithFields();
        Calendar expected = Calendar.getInstance();
        toAccess.setaPrivateObject(expected);
        final Object fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "aPrivateObject", toAccess);
        assertEquals("value of field incorrect", expected, fieldValue);
    }

    @Test
    public void testGetValueOfFieldWhenFieldIsAStaticString() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final String fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "aPrivateStaticString", null);
        assertEquals("value of field incorrect", toAccess.getaPrivateStaticString(), fieldValue);
    }

    @Test(expected = NoSuchFieldException.class)
    public void testGetValueOfFieldWhenFieldNameIsIncorrect() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        ClassAccessor.getValueOfField(ClassWithFields.class, "thisFieldDoesNotExist", toAccess);
    }

    @Test
    public void testGetValueOfFieldShortVersion() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final String fieldValue = ClassAccessor.getValueOfField("aPrivateString", toAccess);
        assertEquals("value of field incorrect", toAccess.getaPrivateString(), fieldValue);
    }

    @Test
    public void testGetValueOfFieldWhenFieldIsPublic() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final String fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "aPublicString", toAccess);
        assertEquals("value of field incorrect", toAccess.getaPublicString(), fieldValue);
    }

    @Test
    public void testGetValueOfFieldWhenFieldIsProtected() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final String fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "aProtectedString", toAccess);
        assertEquals("value of field incorrect", toAccess.getaProtectedString(), fieldValue);
    }

    @Test
    public void testGetValueOfFieldWhenFieldIsFinal() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final Calendar fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "aPrivateFinalCalendar", toAccess);
        assertEquals("value of field incorrect", toAccess.getaPrivateFinalCalendar(), fieldValue);
    }

    @Test
    public void testGetValueOfFieldWhenFieldIsFinalStatic() throws Exception {
        final ClassWithFields toAccess = new ClassWithFields();
        final String fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "A_PRIVATE_FINAL_STATIC_STRING", toAccess);
        assertEquals("value of field incorrect", ClassWithFields.getaPrivateFinalStaticString(), fieldValue);
    }

    @Test
    public void testGetValueOfFieldWhenFieldIsATypedList() throws Exception {
        ClassWithFields toAccess = new ClassWithFields();
        List<String> expected = new ArrayList<>();
        toAccess.setaPrivateListOfStrings(expected);
        final Object fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "aPrivateListOfStrings", toAccess);
        assertEquals("value of field incorrect", expected, fieldValue);
    }

    @Test(expected = NoSuchFieldException.class)
    public void testGetValueOfFieldWhenFieldIsDeclaredOnInheritedClassButUseChildClassAsClassDeclaredOnThenShouldThrowException() throws Exception {
        final ClassWhichInheritsFields toAccess = new ClassWhichInheritsFields();
        final String fieldValue = ClassAccessor.getValueOfField(ClassWhichInheritsFields.class, "aProtectedString", new ClassWhichInheritsFields());
    }

    @Test(expected = NoSuchFieldException.class)
    public void testGetValueOfFieldShortVersionWhenFieldIsDeclaredOnInheritedClassThenShouldThrowException() throws Exception {
        final ClassWhichInheritsFields toAccess = new ClassWhichInheritsFields();
        final String fieldValue = ClassAccessor.getValueOfField("aProtectedString", new ClassWhichInheritsFields());
    }

    @Test
    public void testGetValueOfFieldWhenFieldIsDeclaredOnInheritedClassButUseParentClassAsClassDeclaredOn() throws Exception {
        final ClassWhichInheritsFields toAccess = new ClassWhichInheritsFields();
        final String fieldValue = ClassAccessor.getValueOfField(ClassWithFields.class, "aProtectedString", new ClassWhichInheritsFields());
        assertEquals("value of field incorrect", toAccess.getaProtectedString(), fieldValue);
    }


}
