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
import com.spsglobalsolutions.controllables.annotations.controllablemethod.examples.MethodHistoryExamples;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * Tests various setups of the MethodCallHistoryBase and its subclasses.
 *
 * @author stevo58008
 */
public abstract class ControllableMethodTestBase<MethodReturnType, Method extends ControllableMethod<?, MethodReturnType> & Throwing<?, MethodReturnType>> {

    /**
     * The MethodHistoryExamples instance created for this test to use (if it wants).
     */
    protected MethodHistoryExamples exampleInstanceInUse;

    /**
     * The {@link ControllableMethod} instance being tested
     */
    protected Method history;

    /**
     * @return the actual history instance you want to test
     */
    protected abstract Method getHistoryInstance();

    /**
     * Don't return the same instance as it makes testing orderings pointless!!!!
     * <p/>
     * For void methods just use null as the value.
     *
     * @return a unique instance of a suitable return value for the method being tested
     */
    protected abstract MethodReturnType getUniqueReturnValue();

    /**
     * Don't return nulls, use empty arrays for no args.
     * <p/>
     * Don't return the same instance as it makes testing orderings pointless!!!!
     *
     * @return unique method arguments of the correct type and in the correct order for the method being tested.
     */
    protected abstract Object[] getUniqueMethodArguments();

    /**
     * execute the method that the history under test is for.
     * <p/>
     * you'll need to cast the given args to the correct type yourself, and wrap any cast exceptions in a {@link ControllableMethodException}
     *
     * @param withArgs the args for the method being called
     */
    protected abstract void executeMethodUnderTest(Object[] withArgs) throws Exception;

    @Before
    public void getHistoryAndMakeSureItsEmpty() {
        this.exampleInstanceInUse = new MethodHistoryExamples();
        this.history = this.getHistoryInstance();
        assertEquals("history should be empty before any calls to it", 0, this.history.getHistory().size());
    }

    @Test(expected = ControllableMethodException.class)
    public void testCallingTheMethodWithoutAddingAnyExitsThrowsException() {
        this.callMethodWhichHasHistory(this.getUniqueMethodArguments());
        fail("An exception was expected as no exits had been added");
    }

    @Test
    public void testSingleNormalExitCallResultsInSingleHistoryIncrease() {
        final MethodReturnType expectedReturnValue = this.getUniqueReturnValue();
        this.addNormalExit(expectedReturnValue);
        final Object[] expectedArguments = this.getUniqueMethodArguments();
        final long expectedCallTime = this.callMethodWhichHasHistory(expectedArguments);
        final ImmutableList<MethodCall<MethodReturnType>> historySnapshot = this.history.getHistory();
        assertEquals("unexpected history size", 1, historySnapshot.size());
        TestUtils.assertExpectedMethodCallWithReturnValue(historySnapshot.get(0), expectedCallTime, expectedReturnValue, expectedArguments);
    }

    @Test
    public void testSingleUncheckedExceptionExitCallResultsInSingleHistoryIncrease() {
        final IllegalStateException expectedException = new IllegalStateException("junit made me");
        this.history.addUncheckedExceptionExit(expectedException);
        final Object[] expectedArguments = this.getUniqueMethodArguments();
        final long expectedCallTime = this.callMethodWhichHasHistory(expectedArguments);
        final ImmutableList<MethodCall<MethodReturnType>> historySnapshot = this.history.getHistory();
        assertEquals("unexpected history size", 1, historySnapshot.size());
        TestUtils.assertExpectedMethodCallWithUncheckedException(historySnapshot.get(0), expectedCallTime, expectedException, expectedArguments);
    }

    @Test
    public void testMultipleCallsWhenASingleExitAddedResultsInSameExitBeingUsedForAllCalls() {
        // use an exception so can check its the same exit (as we have an object - the exception - to check)
        final IllegalStateException expectedException = new IllegalStateException("junit made me");
        this.history.addUncheckedExceptionExit(expectedException);
        for (int i = 0; i < 5; i++) {
            final Object[] expectedArguments = this.getUniqueMethodArguments();
            final long expectedCallTime = this.callMethodWhichHasHistory(expectedArguments);
            final ImmutableList<MethodCall<MethodReturnType>> historySnapshot = this.history.getHistory();
            TestUtils.assertExpectedMethodCallWithUncheckedException(historySnapshot.get(i), expectedCallTime, expectedException, expectedArguments);
        }
    }

    @Test
    public void testMultipleCallsStoresMultipleHistoryElements() throws InterruptedException {
        final MethodReturnType expectedReturnValue = this.getUniqueReturnValue();
        this.addNormalExit(expectedReturnValue);
        for (int i = 0; i < 10; i++) {
            final Object[] expectedArguments = this.getUniqueMethodArguments();
            final long expectedCallTime = this.callMethodWhichHasHistory(expectedArguments);
            final ImmutableList<MethodCall<MethodReturnType>> historySnapshot = this.history.getHistory();
            TestUtils.assertExpectedMethodCallWithReturnValue(historySnapshot.get(i), expectedCallTime, expectedReturnValue, expectedArguments);
            // sleep a little, so can get some differentiation in expected call time
            Thread.sleep(10);
        }
    }

    @Test
    public void testUsingDifferentControllablesToCheckHistoryOfMultipleCallsShouldReturnAllHistoriesInEachControllable() throws InterruptedException {
        final MethodReturnType expectedReturnValue = this.getUniqueReturnValue();
        this.addNormalExit(expectedReturnValue);
        for (int i = 0; i < 10; i++) {
            final Object[] expectedArguments = this.getUniqueMethodArguments();
            final long expectedCallTime = this.callMethodWhichHasHistory(expectedArguments);
            final ImmutableList<MethodCall<MethodReturnType>> historySnapshot = this.getHistoryInstance().getHistory();
            assertEquals("The history size is not correct", (i + 1), historySnapshot.size());
            TestUtils.assertExpectedMethodCallWithReturnValue(historySnapshot.get(i), expectedCallTime, expectedReturnValue, expectedArguments);
            // sleep a little, so can get some differentiation in expected call time
            Thread.sleep(10);
        }
    }

    @Test
    public void testThatMultipleExitsAreUsedInTheCorrectOrder() {
        // cycle between different exit types (return value, unchecked exception, checked exception).
        final RuntimeException unchecked1 = new RuntimeException("should be 1st");
        final RuntimeException unchecked2 = new RuntimeException("should be 2nd");
        final MethodReturnType expectedReturnValue = this.getUniqueReturnValue(); // just use one for all, as not fussed about checking different values, that's done elsewhere.
        this.history.addUncheckedExceptionExit(unchecked1);
        this.addNormalExit(expectedReturnValue);
        this.history.addUncheckedExceptionExit(unchecked2);
        this.addNormalExit(expectedReturnValue);

        // now run through the calls
        final Object[] expectedArguments = this.getUniqueMethodArguments(); // im not fussed about checking different arg values here, that's been done in other tests.
        final long expectedTimeOfCall0 = this.callMethodWhichHasHistory(expectedArguments);
        final long expectedTimeOfCall1 = this.callMethodWhichHasHistory(expectedArguments);
        final long expectedTimeOfCall2 = this.callMethodWhichHasHistory(expectedArguments);
        final long expectedTimeOfCall3 = this.callMethodWhichHasHistory(expectedArguments);
        final ImmutableList<MethodCall<MethodReturnType>> historySnapshot = this.history.getHistory();
        assertEquals("The number of entries in the call history was not right", 4, historySnapshot.size());
        TestUtils.assertExpectedMethodCallWithUncheckedException(historySnapshot.get(0), expectedTimeOfCall0, unchecked1, expectedArguments);
        TestUtils.assertExpectedMethodCallWithReturnValue(historySnapshot.get(1), expectedTimeOfCall1, expectedReturnValue, expectedArguments);
        TestUtils.assertExpectedMethodCallWithUncheckedException(historySnapshot.get(2), expectedTimeOfCall2, unchecked2, expectedArguments);
        TestUtils.assertExpectedMethodCallWithReturnValue(historySnapshot.get(3), expectedTimeOfCall3, expectedReturnValue, expectedArguments);
    }

    @Test
    public void testClearAllExitsResultsInExitsLeftAndThereforeExceptionOnNextExecution() {
        this.addNormalExit(this.getUniqueReturnValue());
        this.callMethodWhichHasHistory(this.getUniqueMethodArguments());
        this.history.clearExits();
        try {
            this.callMethodWhichHasHistory(this.getUniqueMethodArguments());
            fail("A ControllableMethodException - due to no exits being set - was expected from the previous line");
        } catch (ControllableMethodException e) {
            assertEquals(
                    "I expected the ControllableMethodException to have a cause of NoSuchElementException, because of having no exits set",
                    NoSuchElementException.class, e.getCause().getClass());
            Assert.assertTrue(
                    "I expected the NoSuchElementsException to have a message pointing to having no exits as the problem",
                    e.getCause().getMessage().startsWith("There have been no MethodExit elements added."));
        }
    }

    @Test
    public void testACallThatReturnsNormallyHasAnExitWithExitedValueOfNormally() throws Exception {
        this.history.clearExits();
        this.addNormalExit(this.getUniqueReturnValue());
        this.callMethodWhichHasHistory(this.getUniqueMethodArguments());
        assertEquals("It should have exited Normally", MethodExit.Exited.Normally, history.getHistory().get(0).getExit().getHowItExited());
    }

    @Test
    public void testACallThatThrowsUncheckedExceptionHasAnExitWithExitedValueOfByUncheckedException() throws Exception {
        this.history.clearExits();
        this.history.addUncheckedExceptionExit(new RuntimeException("junit made me"));
        this.callMethodWhichHasHistory(this.getUniqueMethodArguments());
        assertEquals("It should have exited ByUnecheckedException", MethodExit.Exited.ByUncheckedException, history.getHistory().get(0).getExit().getHowItExited());
    }

    @Test
    public void testAddingAnExitWithDelayDoesDelayTheExit() throws Exception {
        this.history.clearExits();
        final long expectedDelay = 500;
        this.addNormalExit(this.getUniqueReturnValue(), expectedDelay);
        this.callMethodWhichHasHistory(this.getUniqueMethodArguments());
        long callEndTime = System.currentTimeMillis();
        long callStartTime = this.history.getHistory().get(0).getCallTimeInMillis();
        final long callLength = (callEndTime - callStartTime);
        assertTrue("The method call length (" + callLength + ") should have been at least as long as the delay set (" + expectedDelay + ")", callLength >= expectedDelay);
    }

    protected long callMethodWhichHasHistory(final Object[] withArgs) {
        final long callTime = System.currentTimeMillis();
        try {
            this.executeMethodUnderTest(withArgs);
        } catch (final ControllableMethodException e) {
            // this means somethings wrong internally so we need to throw this up
            throw e;
        } catch (final Exception e) {
            // stop it coming out, otherwise we wouldn't get the time returned
            System.out.println("FYI, the method under test threw an exception when executed : " + e);
        }
        return callTime;
    }

    /**
     * Add a normal exit to the ControllableMethod.  it works out what type of method we are (ControllableVoidMethod or ReturnMethodHistory) and calls appropriate addNormalExit()
     * method on it.  The withReturnValue arg is ignored for ControllableVoidMethod types.
     *
     * @param withReturnValue the return value to use (this is ignored for Void methods).
     */
    private void addNormalExit(final MethodReturnType withReturnValue, final long exitDelay) {
        // check if a void or return method and cast and call appropriate method.
        if (this.history instanceof ControllableVoidMethod) {
            ((ControllableVoidMethod) this.history).addNormalExit(exitDelay);
        } else if (this.history instanceof ControllableNonVoidMethod) {
            ((ControllableNonVoidMethod<?, MethodReturnType>) this.history).addNormalExit(withReturnValue, exitDelay);
        } else {
            throw new IllegalStateException("God knows whats happened here! The history under test doesn't appear to be a ControllableVoidMethod or ReturnMethodHistory type");
        }
    }

    private void addNormalExit(final MethodReturnType withReturnValue) {
        this.addNormalExit(withReturnValue, 0);
    }
}
