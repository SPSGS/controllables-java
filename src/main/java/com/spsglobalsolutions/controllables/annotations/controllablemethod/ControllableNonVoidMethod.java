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

import com.google.common.collect.ImmutableList;

/**
 * @author stevo58008
 */
public class ControllableNonVoidMethod<Itself extends ControllableNonVoidMethod<Itself, ReturnType>, ReturnType>
        implements ControllableMethod<Itself, ReturnType>, Throwing<Itself, ReturnType>
{

    protected final ControlledMethodHandler<ReturnType> controllableHandler;

    public ControllableNonVoidMethod(final ControlledMethodHandler<ReturnType> controllableHandler)
    {
        this.controllableHandler = controllableHandler;
    }

    /**
     * Add a normal exit for this method (so in this case, a return value, which will be returned).
     *
     * @param returnValue the value to return by this method
     * @return {@code returnValue}
     */
    public final Itself addNormalExit(final ReturnType returnValue)
    {
        this.controllableHandler.addNormalExit(returnValue, 0);
        return (Itself) this;
    }

    /**
     * Add a normal exit for this method (so in this case, a return value, which will be returned), but also add a delay before the method will return.
     *
     * @param returnValue             the value to return by this method
     * @param delayBeforeExitInMillis a value (in milliseconds) which will be used for this method to sleep before returning.
     * @return {@code returnValue} itself
     */
    public final Itself addNormalExit(final ReturnType returnValue, final long delayBeforeExitInMillis)
    {
        this.controllableHandler.addNormalExit(returnValue, delayBeforeExitInMillis);
        return (Itself) this;
    }

    @Override
    public final ImmutableList<MethodCall<ReturnType>> getHistory()
    {
        return this.controllableHandler.getHistorySnapshot();
    }

    @Override
    public final Itself clearExits()
    {
        this.controllableHandler.clearAllExits();
        return (Itself) this;
    }

    @Override
    public final Itself addUncheckedExceptionExit(final RuntimeException toThrow)
    {
        this.controllableHandler.addUncheckedExceptionExit(toThrow);
        return (Itself) this;
    }


    public static class Throwing1CheckedException<Itself extends Throwing1CheckedException<Itself, ReturnType, E1>, ReturnType, E1 extends Exception>
            extends ControllableNonVoidMethod<Itself, ReturnType>
            implements Throwing.OneCheckedException<Itself, E1, ReturnType>
    {

        public Throwing1CheckedException(final ControlledMethodHandler<ReturnType> controllableHandler)
        {
            super(controllableHandler);
        }

        @Override
        public final Itself addCheckedException1Exit(final E1 toThrow)
        {
            this.controllableHandler.addExceptionExit(toThrow);
            return (Itself) this;
        }
    }

    public static class Throwing2CheckedExceptions<Itself extends Throwing2CheckedExceptions<Itself, ReturnType, E1, E2>, ReturnType, E1 extends Exception, E2 extends Exception>
            extends Throwing1CheckedException<Itself, ReturnType, E1>
            implements TwoCheckedExceptions<Itself, E1, E2, ReturnType>
    {

        public Throwing2CheckedExceptions(final ControlledMethodHandler<ReturnType> controllableHandler)
        {
            super(controllableHandler);
        }

        @Override
        public final Itself addCheckedException2Exit(final E2 toThrow)
        {
            this.controllableHandler.addExceptionExit(toThrow);
            return (Itself) this;
        }
    }

    public static class Throwing3CheckedExceptions<Itself extends Throwing3CheckedExceptions<Itself, ReturnType, E1, E2, E3>, ReturnType, E1 extends Exception, E2 extends
            Exception, E3 extends Exception>
            extends Throwing2CheckedExceptions<Itself, ReturnType, E1, E2>
            implements ThreeCheckedExceptions<Itself, E1, E2, E3, ReturnType>
    {

        public Throwing3CheckedExceptions(final ControlledMethodHandler<ReturnType> controllableHandler)
        {
            super(controllableHandler);
        }

        @Override
        public final Itself addCheckedException3Exit(final E3 toThrow)
        {
            this.controllableHandler.addExceptionExit(toThrow);
            return (Itself) this;
        }
    }

    public static class Throwing4CheckedExceptions<Itself extends Throwing4CheckedExceptions<Itself, ReturnType, E1, E2, E3, E4>, ReturnType, E1 extends Exception, E2 extends
            Exception, E3 extends Exception, E4 extends Exception>
            extends Throwing3CheckedExceptions<Itself, ReturnType, E1, E2, E3>
            implements FourCheckedExceptions<Itself, E1, E2, E3, E4, ReturnType>
    {


        public Throwing4CheckedExceptions(final ControlledMethodHandler<ReturnType> controllableHandler)
        {
            super(controllableHandler);
        }

        @Override
        public final Itself addCheckedException4Exit(final E4 toThrow)
        {
            this.controllableHandler.addExceptionExit(toThrow);
            return (Itself) this;
        }
    }

    public static class Throwing5CheckedExceptions<Itself extends Throwing5CheckedExceptions<Itself, ReturnType, E1, E2, E3, E4, E5>, ReturnType, E1 extends Exception, E2
            extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception>
            extends Throwing4CheckedExceptions<Itself, ReturnType, E1, E2, E3, E4>
            implements FiveCheckedExceptions<Itself, E1, E2, E3, E4, E5, ReturnType>
    {

        public Throwing5CheckedExceptions(final ControlledMethodHandler<ReturnType> controllableHandler)
        {
            super(controllableHandler);
        }

        @Override
        public final Itself addCheckedException5Exit(final E5 toThrow)
        {
            this.controllableHandler.addExceptionExit(toThrow);
            return (Itself) this;
        }
    }
}
