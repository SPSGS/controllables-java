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

/**
 * @author stevo58008
 */
public class ClassWithMethods {

    private static void privateStaticMethodVoidReturnNoArgs() {
        System.out.println("privateStaticMethodVoidReturnNoArgs - called");
    }

    public void publicMethodVoidReturnNoArgs() {
        System.out.println("publicMethodVoidReturnNoArgs - called");
    }

    void packageMethodVoidReturnNoArgs() {
        System.out.println("packageMethodVoidReturnNoArgs - called");
    }

    protected void protectedMethodVoidReturnNoArgs() {
        System.out.println("protectedMethodVoidReturnNoArgs - called");
    }

    private void privateMethodVoidReturnNoArgs() {
        System.out.println("privateMethodVoidReturnNoArgs - called");
    }

    private long privateMethodPrimitiveLongReturnNoArgs() {
        System.out.println("privateMethodPrimitiveLongReturnNoArgs - called");
        return 1;
    }

    private Long privateMethodObjectLongReturnNoArgs() {
        System.out.println("privateMethodObjectLongReturnNoArgs - called");
        return new Long(1);
    }

    private long[] privateMethodPrimitiveLongArrayReturnNoArgs() {
        System.out.println("privateMethodObjectLongReturnNoArgs - called");
        return new long[]{1, 2, 3};
    }

    private Object[] privateMethodObjectArrayReturnNoArgs() {
        System.out.println("privateMethodObjectArrayReturnNoArgs - called");
        return new Object[]{"1", "2", "3"};
    }

    private Object[] privateMethodObjectArrayReturnObjectPrimitiveLongArgs(Object arg1, long arg2) {
        System.out.println("privateMethodObjectArrayReturnObjectPrimitiveLongArgs - called");
        return new Object[]{arg1, arg2};
    }
}
