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
 * Represents how a controlled method exits, which could be by a return value, a checked exception, or by an unchecked exception.
 * <p/>
 * When working out how the method exited, it is advisable to check for non-null values of the exceptions first (@link #getByUncheckedException()} and {@link
 * #getByCheckedException()}).  If both of these return a null value, then you can assume that the method exited "normally", and whatever value is returned from {@link
 * #getByReturnValue()} (including null) was what was returned.
 *
 * @param <ReturnType> The type of the return value of the method (for void methods it will use Void class)
 * @author stevo58008
 */
public class MethodExit<ReturnType>
{

    // Only one of these will contain a value
    private final ReturnType byReturnValue;
    private final RuntimeException byUncheckedException;
    private final Exception byCheckedException;
    private final ControlledMethodHandler historyToStoreMeIn;
    private final Exited howItExited;
    private final long msExitDelay;

    private MethodExit(final ControlledMethodHandler historyToStoreThisIn, final ReturnType byReturnValue,
                       final RuntimeException byUncheckedException,
                       final Exception byCheckedException, final long msExitDelay, final Exited howItExited)
    {
        this.historyToStoreMeIn = historyToStoreThisIn;
        this.byReturnValue = byReturnValue;
        this.byUncheckedException = byUncheckedException;
        this.byCheckedException = byCheckedException;
        this.howItExited = howItExited;
        this.msExitDelay = msExitDelay;
    }

    /**
     * Create a {@link MethodExit} that exited "normally" by returning the given value (which should be null for a void method).
     *
     * @param historyToStoreThisIn the history that this should be stored in
     * @param byReturnValue        the value returned from the method.
     */
    MethodExit(final ControlledMethodHandler historyToStoreThisIn, final ReturnType byReturnValue,
               final long msExitDelay)
    {
        this(historyToStoreThisIn, byReturnValue, null, null, msExitDelay, Exited.Normally);
    }

    /**
     * Create a {@link MethodExit} that exited by throwing the given Unchecked exception.
     *
     * @param historyToStoreThisIn the history that this should be stored in
     * @param byUncheckedException the exception that was thrown
     */
    MethodExit(final ControlledMethodHandler historyToStoreThisIn, final RuntimeException byUncheckedException,
               final long msExitDelay)
    {
        this(historyToStoreThisIn, null, byUncheckedException, null, msExitDelay, Exited.ByUncheckedException);
    }

    /**
     * Create a {@link MethodExit} that exited by throwing the given Checked exception.
     *
     * @param historyToStoreThisIn the history that this should be stored in
     * @param byCheckedException   the exception that was thrown
     */
    MethodExit(final ControlledMethodHandler historyToStoreThisIn, final Exception byCheckedException,
               final long msExitDelay)
    {
        this(historyToStoreThisIn, null, null, byCheckedException, msExitDelay, Exited.ByCheckedException);
    }

    static <T extends Exception> void throwIfExceptionIsOfExpectedType(Exception toThrowIfExpectedType) throws T
    {
        try
        {
            final T correctType = (T) toThrowIfExpectedType;
            throw correctType;
        }
        catch(final ClassCastException e)
        {
            // it wasn't the right type, so move on
        }
    }

    public Exited getHowItExited()
    {
        return this.howItExited;
    }

    /**
     * @return the checked Exception that was thrown, or null if a checked Exception wasn't thrown.  If the controlled method doesn't throw any checked exceptions then this will
     * always return null.
     */
    public Exception getByCheckedException()
    {
        return this.byCheckedException;
    }

    /**
     * The value returned will match the ReturnType of the controlled method.  A void method will always return a null value.
     *
     * @return the "normal" value returned from the controlled method.
     */
    public ReturnType getByReturnValue()
    {
        return this.byReturnValue;
    }

    /**
     * @return the unchecked Exception that was thrown, or null if a unchecked Exception wasn't thrown.
     */
    public RuntimeException getByUncheckedException()
    {
        return this.byUncheckedException;
    }

    /**
     * Either returns the value that would normally be returned by the method (Void in case of void methods) or throws any byUncheckedException that has been set.
     *
     * @return the value returned if this exits in a "normal" fashion.
     */
    ReturnType exitByUncheckedExceptionOrReturningValue(Object... methodArgumentValues)
    {
        exitByUncheckedException(methodArgumentValues);
        return this.byReturnValue;
    }

    <E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception> ReturnType exitByCheckedOrUncheckedExceptionOrReturnValue(
            Object... methodArgumentValues)
            throws E1, E2, E3, E4, E5
    {
        exitByUncheckedException(methodArgumentValues);
        if(this.byCheckedException != null)
        {
            MethodExit.<E1>throwIfExceptionIsOfExpectedType(this.byCheckedException);
            MethodExit.<E2>throwIfExceptionIsOfExpectedType(this.byCheckedException);
            MethodExit.<E3>throwIfExceptionIsOfExpectedType(this.byCheckedException);
            MethodExit.<E4>throwIfExceptionIsOfExpectedType(this.byCheckedException);
            MethodExit.<E5>throwIfExceptionIsOfExpectedType(this.byCheckedException);
        }
        return this.byReturnValue;
    }

    private void exitByUncheckedException(Object... methodArgumentValues)
    {
        this.storeMe(methodArgumentValues);
        try
        {
            Thread.sleep(this.msExitDelay);
        }
        catch(InterruptedException e)
        {
            // i don't care
        }
        if(this.byUncheckedException != null)
        {
            throw byUncheckedException;
        }
    }


    /**
     * Store this exit in the controlled methods history.  This should be called as close to the controlled methods execution start as possible.
     *
     * @param methodArgumentValues the arguments that were passed to the controlled method.
     * @return itself, so fluid calls can be made.
     */
    private MethodExit<ReturnType> storeMe(Object... methodArgumentValues)
    {
        this.historyToStoreMeIn.addCall(new MethodCall<>(this, methodArgumentValues));
        return this;
    }

    public enum Exited
    {
        Normally, ByUncheckedException, ByCheckedException
    }
}
