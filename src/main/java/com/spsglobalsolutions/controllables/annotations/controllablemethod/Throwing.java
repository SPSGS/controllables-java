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

/**
 * @author stevo58008
 */
public interface Throwing<Itself extends ControllableMethod<Itself, ReturnType>, ReturnType>
{

    /**
     * Add a method exit, which happens by throwing the given exception.
     *
     * @param toThrow the exception which will be thrown
     * @return itself
     */
    Itself addUncheckedExceptionExit(RuntimeException toThrow);

    interface OneCheckedException<Itself extends OneCheckedException<Itself, E1, ReturnType> & ControllableMethod<Itself, ReturnType>, E1 extends Exception, ReturnType>
            extends Throwing<Itself, ReturnType>
    {

        /**
         * Add a method exit, which happens by throwing the given exception.
         *
         * @param toThrow the exception which will be thrown
         * @return itself
         */
        Itself addCheckedException1Exit(E1 toThrow);
    }

    interface TwoCheckedExceptions<Itself extends TwoCheckedExceptions<Itself, E1, E2, ReturnType> & ControllableMethod<Itself, ReturnType>, E1 extends Exception, E2 extends Exception,
            ReturnType>
            extends OneCheckedException<Itself, E1, ReturnType>
    {

        /**
         * Add a method exit, which happens by throwing the given exception.
         *
         * @param toThrow the exception which will be thrown
         * @return itself
         */
        Itself addCheckedException2Exit(E2 toThrow);
    }

    interface ThreeCheckedExceptions<Itself extends ThreeCheckedExceptions<Itself, E1, E2, E3, ReturnType> & ControllableMethod<Itself, ReturnType>, E1 extends Exception, E2 extends
            Exception, E3 extends
            Exception, ReturnType>
            extends TwoCheckedExceptions<Itself, E1, E2, ReturnType>
    {

        /**
         * Add a method exit, which happens by throwing the given exception.
         *
         * @param toThrow the exception which will be thrown
         * @return itself
         */
        Itself addCheckedException3Exit(E3 toThrow);
    }

    interface FourCheckedExceptions<Itself extends FourCheckedExceptions<Itself, E1, E2, E3, E4, ReturnType> & ControllableMethod<Itself, ReturnType>, E1 extends Exception, E2 extends
            Exception, E3 extends
            Exception, E4 extends Exception, ReturnType>
            extends ThreeCheckedExceptions<Itself, E1, E2, E3, ReturnType>
    {

        /**
         * Add a method exit, which happens by throwing the given exception.
         *
         * @param toThrow the exception which will be thrown
         * @return itself
         */
        Itself addCheckedException4Exit(E4 toThrow);
    }

    interface FiveCheckedExceptions<Itself extends FiveCheckedExceptions<Itself, E1, E2, E3, E4, E5, ReturnType> & ControllableMethod<Itself, ReturnType>, E1 extends Exception, E2
            extends Exception, E3
            extends Exception, E4 extends Exception, E5 extends Exception, ReturnType>
            extends FourCheckedExceptions<Itself, E1, E2, E3, E4, ReturnType>
    {

        /**
         * Add a method exit, which happens by throwing the given exception.
         *
         * @param toThrow the exception which will be thrown
         * @return itself
         */
        Itself addCheckedException5Exit(E5 toThrow);
    }
}
