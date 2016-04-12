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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Utility class to allow you to access hidden fields or methods of a class
 *
 * @author stevo58008
 */
public final class ClassAccessor
{

    /**
     * Get the value of a field, which may be not visible to the caller.
     *
     * @param classFieldDeclaredOn The class the field is actually on (e.g. if the field is inherited, then this must be the inherited class).
     * @param fieldName            The name of the field (case sensitive)
     * @param toAccess             The particular object instance that you want to access and get the value from (use null for static fields).
     * @param <FieldType>          The Type of the field (and what it should be cast to for return)
     * @return The current value of the field, cast to type T
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static <FieldType> FieldType getValueOfField(final Class classFieldDeclaredOn, final String fieldName,
                                                        final Object toAccess)
            throws NoSuchFieldException, IllegalAccessException
    {
        final Field field = classFieldDeclaredOn.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (FieldType) field.get(toAccess);
    }

    /**
     * A convenience method call to {@link #getValueOfField(Class, String, Object)} when the field you want to access is declared on the Class of {@code toAccess} (and therefore
     * is
     * what you would have passed as the {@code classFieldDeclaredOn} value).
     * <p/>
     * This can't be used for static fields; you need to use the full version.
     */
    public static <FieldType> FieldType getValueOfField(final String fieldName, final Object toAccess)
            throws NoSuchFieldException, IllegalAccessException
    {
        return getValueOfField(toAccess.getClass(), fieldName, toAccess);
    }

    /**
     * Set the value of a field (which may or may not be visible, or accessible, to the caller).
     * <p/>
     * This method also lets you set a value on a "final" field, so be careful; {@code setValueOfField(Boolean.class, "FALSE", null, true)} would make things very unpredictable.
     * You've been warned!
     * <p/>
     * Another warning for final fields, is that even though this will work, the new value may not be visible to others depending on JVM caching.  This is particulary true
     * for Final Strings, as the compiler tends to replace these with compile time constants, so even though we change the value, others don't see it.
     *
     * @param classFieldDeclaredOn The class the field is actually on (e.g. if the field is inherited, then this must be the inherited class).
     * @param fieldName            The name of the field (case sensitive)
     * @param toAccess             The particular object instance that you want to access and set the value on (use null for static fields).
     * @param valueToSet           The value that you want the field to have
     * @param <FieldType>          The Type of the field (and therefore the type that the new value should be)
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static <FieldType> void setValueOfField(final Class classFieldDeclaredOn, final String fieldName,
                                                   final Object toAccess, final FieldType valueToSet)
            throws NoSuchFieldException, IllegalAccessException
    {
        final Field field = classFieldDeclaredOn.getDeclaredField(fieldName);
        final int originalModifiers = field.getModifiers();
        field.setAccessible(true);

        // remove any final modifier - if needed
        if(Modifier.isFinal(field.getModifiers()))
        {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, originalModifiers & ~Modifier.FINAL);
        }

        // set the value
        field.set(toAccess, valueToSet);
    }

    /**
     * A convenience method to call to {@link #setValueOfField(Class, String, Object, Object)} when the field you want to access is declared on the class of {@code toAccess} (and
     * therefore is what you would have passed as the {@code classFieldDeclaredOn} value.
     * <p/>
     * This can't be used for static fields; you need to use the full version.
     */
    public static <FieldType> void setValueOfField(final String fieldName, final Object toAccess,
                                                   final FieldType valueToSet)
            throws NoSuchFieldException, IllegalAccessException
    {
        setValueOfField(toAccess.getClass(), fieldName, toAccess, valueToSet);
    }


    /**
     * Call a method on a class (which could, and probably will be, non public accessible).
     * <p/>
     * It is done via reflection, so the usual rules and guidelines should be observed (e.g. it is not an efficient way to call a method, so only use as last option).
     *
     * @param classMethodDeclaredOn The class that the method is declared on (e.g. if the field is inherited, then this must be the inherited class).
     * @param methodName            The name of the method (case sensitive)
     * @param methodParamTypes      The classes of each param that the method takes (in order).
     * @param toAccess              The particular object instance that you want to access and set the value on (use null for static fields).
     * @param methodParamValues     The values to pass to the method call.
     * @param <ReturnType>          The expected return type (for void methods you should use/expect null).
     * @return whatever the method call would normally return (see {@link Method#invoke(Object, Object...)} - void methods return null)
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <ReturnType> ReturnType callMethod(final Class classMethodDeclaredOn, final String methodName,
                                                     final Class[] methodParamTypes, final Object toAccess,
                                                     final Object[] methodParamValues)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method toCall = classMethodDeclaredOn.getDeclaredMethod(methodName, methodParamTypes);
        toCall.setAccessible(true);
        return (ReturnType) toCall.invoke(toAccess, methodParamValues);
    }

    /**
     * A Convenience method call to {@link #callMethod(Class, String, Class[], Object, Object[])}, using the value of {@code toAccess.getClass()} as the value for {@code
     * classMethodDeclaredOn}.
     * <p/>
     * This can't be used for static fields; you need to use the full version.
     */
    public static <ReturnType> ReturnType callMethod(final String methodName, final Class[] methodParamTypes,
                                                     final Object toAccess, final Object[] methodParamValues)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method toCall = toAccess.getClass().getDeclaredMethod(methodName, methodParamTypes);
        toCall.setAccessible(true);
        return (ReturnType) toCall.invoke(toAccess, methodParamValues);
    }
}
