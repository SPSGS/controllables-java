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
public abstract class ControlledVoidMethod
{

    protected ControlledMethodHandler<Void> controlledHandler = new ControlledMethodHandler<>();

    public static class Throwing0CheckedExceptions extends ControlledVoidMethod
    {

        public void exit(Object... methodArgumentValues)
        {
            this.controlledHandler.getNextExit().exitByUncheckedExceptionOrReturningValue(methodArgumentValues);
        }

        public ControllableVoidMethod<?> createControllable()
        {
            return new ControllableVoidMethod<>(this.controlledHandler);
        }
    }

    public static class Throwing1CheckedException<E1 extends Exception> extends ControlledVoidMethod
    {

        public void exit(Object... methodArgumentValues) throws E1
        {
            this.controlledHandler.getNextExit().<E1, E1, E1, E1, E1>exitByCheckedOrUncheckedExceptionOrReturnValue(
                    methodArgumentValues);
        }

        public ControllableVoidMethod.Throwing1CheckedException<?, E1> createControllable()
        {
            return new ControllableVoidMethod.Throwing1CheckedException<>(this.controlledHandler);
        }
    }

    public static class Throwing2CheckedExceptions<E1 extends Exception, E2 extends Exception>
            extends ControlledVoidMethod
    {

        public void exit(Object... methodArgumentValues) throws E1, E2
        {
            this.controlledHandler.getNextExit().<E1, E2, E1, E1, E1>exitByCheckedOrUncheckedExceptionOrReturnValue(
                    methodArgumentValues);
        }

        public ControllableVoidMethod.Throwing2CheckedExceptions<?, E1, E2> createControllable()
        {
            return new ControllableVoidMethod.Throwing2CheckedExceptions<>(this.controlledHandler);
        }
    }

    public static class Throwing3CheckedExceptions<E1 extends Exception, E2 extends Exception, E3 extends Exception>
            extends ControlledVoidMethod
    {

        public void exit(Object... methodArgumentValues) throws E1, E2, E3
        {
            this.controlledHandler.getNextExit().<E1, E2, E3, E1, E1>exitByCheckedOrUncheckedExceptionOrReturnValue(
                    methodArgumentValues);
        }

        public ControllableVoidMethod.Throwing3CheckedExceptions<?, E1, E2, E3> createControllable()
        {
            return new ControllableVoidMethod.Throwing3CheckedExceptions<>(this.controlledHandler);
        }
    }

    public static class Throwing4CheckedExceptions<E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception>
            extends ControlledVoidMethod
    {

        public void exit(Object... methodArgumentValues) throws E1, E2, E3, E4
        {
            this.controlledHandler.getNextExit().<E1, E2, E3, E4, E1>exitByCheckedOrUncheckedExceptionOrReturnValue(
                    methodArgumentValues);
        }

        public ControllableVoidMethod.Throwing4CheckedExceptions<?, E1, E2, E3, E4> createControllable()
        {
            return new ControllableVoidMethod.Throwing4CheckedExceptions<>(this.controlledHandler);
        }
    }

    public static class Throwing5CheckedExceptions<E1 extends Exception, E2 extends Exception, E3 extends Exception, E4 extends Exception, E5 extends Exception>
            extends ControlledVoidMethod
    {

        public void exit(Object... methodArgumentValues) throws E1, E2, E3, E4, E5
        {
            this.controlledHandler.getNextExit().<E1, E2, E3, E4, E5>exitByCheckedOrUncheckedExceptionOrReturnValue(
                    methodArgumentValues);
        }

        public ControllableVoidMethod.Throwing5CheckedExceptions<?, E1, E2, E3, E4, E5> createControllable()
        {
            return new ControllableVoidMethod.Throwing5CheckedExceptions<>(this.controlledHandler);
        }
    }
}
