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
public abstract class ControlledNonVoidMethod<ReturnType>
{

    protected ControlledMethodHandler<ReturnType> controlledHandler = new ControlledMethodHandler<>();

    public static class Throwing0CheckedExceptions<ReturnType> extends ControlledNonVoidMethod<ReturnType>
    {

        public ReturnType exit(Object... methodArgumentValues)
        {
            return this.controlledHandler.getNextExit().exitByUncheckedExceptionOrReturningValue(methodArgumentValues);
        }

        public ControllableNonVoidMethod<?, ReturnType> createControllable()
        {
            return new ControllableNonVoidMethod<>(this.controlledHandler);
        }
    }

    public static class Throwing1CheckedException<ReturnType, E1 extends Exception>
            extends ControlledNonVoidMethod<ReturnType>
    {

        public ReturnType exit(Object... methodArgumentValues) throws E1
        {
            return this.controlledHandler
                    .getNextExit().<E1, E1, E1, E1, E1>exitByCheckedOrUncheckedExceptionOrReturnValue(
                            methodArgumentValues);
        }

        public ControllableNonVoidMethod.Throwing1CheckedException<?, ReturnType, E1> createControllable()
        {
            return new ControllableNonVoidMethod.Throwing1CheckedException<>(this.controlledHandler);
        }
    }

    public static class Throwing2CheckedExceptions<ReturnType, E1 extends Exception, E2 extends Exception>
            extends ControlledNonVoidMethod<ReturnType>
    {

        public ReturnType exit(Object... methodArgumentValues) throws E1, E2
        {
            return this.controlledHandler
                    .getNextExit().<E1, E2, E1, E1, E1>exitByCheckedOrUncheckedExceptionOrReturnValue(
                            methodArgumentValues);
        }

        public ControllableNonVoidMethod.Throwing2CheckedExceptions<?, ReturnType, E1, E2> createControllable()
        {
            return new ControllableNonVoidMethod.Throwing2CheckedExceptions<>(this.controlledHandler);
        }
    }

    public static class Throwing3CheckedExceptions<ReturnType, E1 extends Exception, E2 extends Exception, E3 extends Exception>
            extends ControlledNonVoidMethod<ReturnType>
    {

        public ReturnType exit(Object... methodArgumentValues) throws E1, E2, E3
        {
            return this.controlledHandler
                    .getNextExit().<E1, E2, E3, E1, E1>exitByCheckedOrUncheckedExceptionOrReturnValue(
                            methodArgumentValues);
        }

        public ControllableNonVoidMethod.Throwing3CheckedExceptions<?, ReturnType, E1, E2, E3> createControllable()
        {
            return new ControllableNonVoidMethod.Throwing3CheckedExceptions<>(this.controlledHandler);
        }
    }

    public static class Throwing4CheckedExceptions<ReturnType, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception>
            extends ControlledNonVoidMethod<ReturnType>
    {

        public ReturnType exit(Object... methodArgumentValues) throws E1, E2, E3, E4
        {
            return this.controlledHandler
                    .getNextExit().<E1, E2, E3, E4, E1>exitByCheckedOrUncheckedExceptionOrReturnValue(
                            methodArgumentValues);
        }

        public ControllableNonVoidMethod.Throwing4CheckedExceptions<?, ReturnType, E1, E2, E3, E4> createControllable()
        {
            return new ControllableNonVoidMethod.Throwing4CheckedExceptions<>(this.controlledHandler);
        }
    }

    public static class Throwing5CheckedExceptions<ReturnType, E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception>
            extends ControlledNonVoidMethod<ReturnType>
    {

        public ReturnType exit(Object... methodArgumentValues) throws E1, E2, E3, E4, E5
        {
            return this.controlledHandler
                    .getNextExit().<E1, E2, E3, E4, E5>exitByCheckedOrUncheckedExceptionOrReturnValue(
                            methodArgumentValues);
        }

        public ControllableNonVoidMethod.Throwing5CheckedExceptions<?, ReturnType, E1, E2, E3, E4, E5> createControllable()
        {
            return new ControllableNonVoidMethod.Throwing5CheckedExceptions<>(this.controlledHandler);
        }
    }
}
