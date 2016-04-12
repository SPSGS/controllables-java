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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A class which handles the details of a controllable method (whether void or not).  Has functionality to do the controllable stuff (getting history, adding exits).
 *
 * @author stevo58008
 */
class ControlledMethodHandler<MethodReturnType>
{

    private final List<MethodExit<MethodReturnType>> exitQueue = Collections.synchronizedList(new ArrayList<MethodExit<MethodReturnType>>());
    private final List<MethodCall<MethodReturnType>> callHistory = Collections.synchronizedList(new ArrayList<MethodCall<MethodReturnType>>());


    /**
     * @return a snapshot of the method's call history up to this point in time.
     */
    public ImmutableList<MethodCall<MethodReturnType>> getHistorySnapshot()
    {
        synchronized(this.callHistory)
        { // i imagine internally it is iterating over the collection - and maybe/maybe not syncs - so a little belt and braces
            return ImmutableList.copyOf(this.callHistory);
        }
    }

    /**
     * Clear the exit queue
     */
    public void clearAllExits()
    {
        this.exitQueue.clear();
    }

    /**
     * Add a normal exit (so a void or return type value) to the end of the exit queue.
     *
     * @param value       the type that is should be added
     * @param msExitDelay how long to sleep for (in ms) before exiting
     */
    public void addNormalExit(final MethodReturnType value, final long msExitDelay)
    {
        this.exitQueue.add(new MethodExit<>(this, value, msExitDelay));
    }

    /**
     * Add a Runtime exception to be thrown, to the end of the exit queue
     *
     * @param exceptionToThrow
     */
    public <T extends RuntimeException> void addUncheckedExceptionExit(final T exceptionToThrow)
    {
        this.exitQueue.add(new MethodExit<MethodReturnType>(this, exceptionToThrow, 0));
    }

    /**
     * Store this call to the history
     *
     * @param callInfo the details of the method call
     */
    void addCall(final MethodCall<MethodReturnType> callInfo)
    {
        this.callHistory.add(callInfo);
    }

    /**
     * Only some subclasses should be adding checked exceptions, to the end of the exit queue
     *
     * @param exceptionToThrow the exception to throw
     */
    protected <T extends Exception> void addExceptionExit(final T exceptionToThrow)
    {
        this.exitQueue.add(new MethodExit<MethodReturnType>(this, exceptionToThrow, 0));
    }


    /**
     * Get the next MethodExit that should be used.  This will be the one that has been on the queue longest (i.e. at the front of the queue).
     * <p/>
     * This method will keep removing elements from the queue, until it get's down to just one left, at which point it will return it, but won't remove it, leaving it in place as
     * the "default" return value, until another one is added.
     * <p/>
     * If there are no elements in the queue, and have never been, then a ControllableMethodException is thrown.
     *
     * @return
     * @throws ControllableMethodException is thrown if there are (and have never been) any MethodExit elements in the exit queue.
     */
    protected MethodExit<MethodReturnType> getNextExit()
    {
        try
        {
            // add the next one off the stack (end of list), but if it is the last one, then leave it on the stack (so there is always something for this method to return)
            synchronized(this.exitQueue) // sync so can't add to it while i see if it is empty
            {
                final int stackSize = this.exitQueue.size();
                if(stackSize == 0)
                {
                    throw new NoSuchElementException("There have been no MethodExit elements added.  " +
                                                     "At least one must be added (used as default exit), otherwise the method cannot be controlled");
                }
                else if(stackSize == 1)
                {
                    return this.exitQueue.get(0); // don't remove the last element
                }
                else
                {
                    return this.exitQueue.remove(0); // remove the next (first/oldest) element
                }
            }
        }
        catch(final Exception e)
        {
            throw new ControllableMethodException(e);
        }
    }
}
