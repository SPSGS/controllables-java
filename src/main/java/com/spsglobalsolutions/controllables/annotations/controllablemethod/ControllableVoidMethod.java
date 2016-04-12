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
public class ControllableVoidMethod<Itself extends ControllableVoidMethod<Itself>>
        implements ControllableMethod<Itself, Void>, Throwing<Itself, Void>
{

    protected final ControlledMethodHandler<Void> controllableHandler;

    public ControllableVoidMethod(final ControlledMethodHandler<Void> controllableHandler)
    {
        this.controllableHandler = controllableHandler;
    }

    /**
     * Add a normal exit for this method (so in this case, nothing)
     *
     * @return {@code returnValue} itself
     */
    public final Itself addNormalExit()
    {
        this.controllableHandler.addNormalExit(null, 0);
        return (Itself) this;
    }

    /**
     * Add a normal exit for this method (so in this case, nothing), but also add a delay before the method will return.
     *
     * @param delayBeforeExitInMillis a value (in milliseconds) which will be used for this method to sleep before returning.
     * @return {@code returnValue} itself
     */
    public final Itself addNormalExit(final long delayBeforeExitInMillis)
    {
        this.controllableHandler.addNormalExit(null, delayBeforeExitInMillis);
        return (Itself) this;
    }

    @Override
    public final ImmutableList<MethodCall<Void>> getHistory()
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


    public static class Throwing1CheckedException<Itself extends Throwing1CheckedException<Itself, E1>, E1 extends Exception>
            extends ControllableVoidMethod<Itself>
            implements Throwing.OneCheckedException<Itself, E1, Void>
    {

        public Throwing1CheckedException(
                final ControlledMethodHandler<Void> controllableHandler)
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

    public static class Throwing2CheckedExceptions<Itself extends Throwing2CheckedExceptions<Itself, E1, E2>, E1 extends Exception, E2 extends Exception>
            extends Throwing1CheckedException<Itself, E1>
            implements TwoCheckedExceptions<Itself, E1, E2, Void>
    {

        public Throwing2CheckedExceptions(
                final ControlledMethodHandler<Void> controllableHandler)
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

    public static class Throwing3CheckedExceptions<Itself extends Throwing3CheckedExceptions<Itself, E1, E2, E3>, E1 extends Exception, E2 extends Exception, E3 extends Exception>
            extends Throwing2CheckedExceptions<Itself, E1, E2>
            implements ThreeCheckedExceptions<Itself, E1, E2, E3, Void>
    {

        public Throwing3CheckedExceptions(
                final ControlledMethodHandler<Void> controllableHandler)
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

    public static class Throwing4CheckedExceptions<Itself extends Throwing4CheckedExceptions<Itself, E1, E2, E3, E4>, E1 extends Exception, E2 extends Exception, E3 extends
            Exception, E4 extends Exception>
            extends Throwing3CheckedExceptions<Itself, E1, E2, E3>
            implements FourCheckedExceptions<Itself, E1, E2, E3, E4, Void>
    {

        public Throwing4CheckedExceptions(
                final ControlledMethodHandler<Void> controllableHandler)
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

    public static class Throwing5CheckedExceptions<Itself extends Throwing5CheckedExceptions<Itself, E1, E2, E3, E4, E5>, E1 extends Exception, E2 extends Exception, E3 extends
            Exception, E4 extends Exception, E5 extends Exception>
            extends Throwing4CheckedExceptions<Itself, E1, E2, E3, E4>
            implements FiveCheckedExceptions<Itself, E1, E2, E3, E4, E5, Void>
    {

        public Throwing5CheckedExceptions(
                final ControlledMethodHandler<Void> controllableHandler)
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
