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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Can test methods which throw 1 checked exception
 *
 * @author stevo58008
 */
public abstract class CheckedExceptionThrowingControllableMethodTestBase<MethodReturnType,
        E1 extends Exception,
        E2 extends Exception,
        Method extends ControllableMethod<?, MethodReturnType> & Throwing.TwoCheckedExceptions<?, E1, E2, MethodReturnType>>
        extends ControllableMethodTestBase<MethodReturnType, Method> {

    /**
     * @return a unique instance of an appropriate checked exception that the method being tested can throw
     */
    protected abstract E1 getUniqueException1Instance();

    /**
     * @return a unique instance of an appropriate checked exception that the method being tested can throw
     */
    protected abstract E2 getUniqueException2Instance();

    @Test
    public void testAddingCheckedExceptionExitResultsInExceptionBeingThrownWhenMethodExecuted() throws Exception {
        E1 expectedChecked1 = getUniqueException1Instance();
        E2 expectedChecked2 = getUniqueException2Instance();
        this.history.addCheckedException1Exit(expectedChecked1);
        this.history.addCheckedException2Exit(expectedChecked2);
        final Object[] expectedArgs = getUniqueMethodArguments();
        // call 1
        final long expectedCallTime1 = callMethodWhichHasHistory(expectedArgs);
        final ImmutableList<MethodCall<MethodReturnType>> historySnapshot1 = this.history.getHistory();
        TestUtils.assertExpectedMethodCallWithCheckedException(historySnapshot1.get(0), expectedCallTime1, expectedChecked1, expectedArgs);
        // call 2
        final long expectedCallTime2 = callMethodWhichHasHistory(expectedArgs);
        final ImmutableList<MethodCall<MethodReturnType>> historySnapshot2 = this.history.getHistory();
        TestUtils.assertExpectedMethodCallWithCheckedException(historySnapshot2.get(1), expectedCallTime2, expectedChecked2, expectedArgs);
    }

    @Test
    public void testACallThatThrowsCheckedExceptionHasAnExitWithExitedValueOfByCheckedException() throws Exception {
        this.history.clearExits();
        this.history.addCheckedException2Exit(getUniqueException2Instance());
        this.callMethodWhichHasHistory(this.getUniqueMethodArguments());
        assertEquals("It should have exited ByCcheckedException", MethodExit.Exited.ByCheckedException, history.getHistory().get(0).getExit().getHowItExited());
    }
}
