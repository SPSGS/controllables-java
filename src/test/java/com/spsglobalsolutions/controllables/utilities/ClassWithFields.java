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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author stevo58008
 */
public class ClassWithFields {

    private final static String A_PRIVATE_FINAL_STATIC_STRING = "A_PRIVATE_FINAL_STATIC_STRING";
    private final static Object A_PRIVATE_FINAL_STATIC_OBJECT = new Object();
    private static String aPrivateStaticString = "aPrivateStaticString";
    private final Calendar aPrivateFinalCalendar = new GregorianCalendar(1982, Calendar.JULY, 24);
    public String aPublicString = "aPublicString";
    protected String aProtectedString = "aProtectedString";
    private String aPrivateString = "aPrivateString";
    private long aPrivateLongPrimitive = 1;
    private long aPrivateLongObject = 2;
    private Object aPrivateObject = new Object();
    private List<String> aPrivateListOfStrings = new ArrayList<>();

    public static String getaPrivateFinalStaticString() {
        return A_PRIVATE_FINAL_STATIC_STRING;
    }

    public static Object getaPrivateFinalStaticObject() {
        return A_PRIVATE_FINAL_STATIC_OBJECT;
    }

    public static String getaPrivateStaticString() {
        return aPrivateStaticString;
    }

    public String getaPublicString() {
        return this.aPublicString;
    }

    public String getaProtectedString() {
        return this.aProtectedString;
    }

    public String getaPrivateString() {
        return this.aPrivateString;
    }

    public Calendar getaPrivateFinalCalendar() {
        return this.aPrivateFinalCalendar;
    }

    public long getaPrivateLongPrimitive() {
        return this.aPrivateLongPrimitive;
    }

    public Long getaPrivateLongObject() {
        return this.aPrivateLongObject;
    }

    public Object getaPrivateObject() {
        return this.aPrivateObject;
    }

    public void setaPrivateObject(final Object aPrivateObject) {
        this.aPrivateObject = aPrivateObject;
    }

    public List<String> getaPrivateListOfStrings() {
        return this.aPrivateListOfStrings;
    }

    public void setaPrivateListOfStrings(final List<String> aPrivateListOfStrings) {
        this.aPrivateListOfStrings = aPrivateListOfStrings;


    }
}
