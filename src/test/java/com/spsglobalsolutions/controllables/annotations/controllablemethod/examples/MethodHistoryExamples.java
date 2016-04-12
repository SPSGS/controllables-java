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

package com.spsglobalsolutions.controllables.annotations.controllablemethod.examples;

import com.spsglobalsolutions.controllables.annotations.controllablemethod.ControllableNonVoidMethod;
import com.spsglobalsolutions.controllables.annotations.controllablemethod.ControllableVoidMethod;
import com.spsglobalsolutions.controllables.annotations.controllablemethod.ControlledNonVoidMethod;
import com.spsglobalsolutions.controllables.annotations.controllablemethod.ControlledVoidMethod;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Some examples of CallHistorys to use for testing
 *
 * @author stevo58008
 */
public class MethodHistoryExamples {

    // FIELDS ****************************************************
    private final ControlledVoidMethod.Throwing0CheckedExceptions voidMethodNoArgs_history = new ControlledVoidMethod.Throwing0CheckedExceptions();
    private final ControlledVoidMethod.Throwing0CheckedExceptions voidMethod_history = new ControlledVoidMethod.Throwing0CheckedExceptions();
    private final ControlledVoidMethod.Throwing2CheckedExceptions<IOException, JAXBException> voidMethodWithCheckedException_history = new ControlledVoidMethod.Throwing2CheckedExceptions<>();
    private final ControlledNonVoidMethod.Throwing0CheckedExceptions<String> stringReturnMethodNoArgs_history = new ControlledNonVoidMethod.Throwing0CheckedExceptions<>();
    private final ControlledNonVoidMethod.Throwing0CheckedExceptions<String> stringReturnMethod_history = new ControlledNonVoidMethod.Throwing0CheckedExceptions<>();
    private final ControlledNonVoidMethod.Throwing2CheckedExceptions<String, IOException, JAXBException> stringReturnMethodWithCheckedException_history = new
            ControlledNonVoidMethod.Throwing2CheckedExceptions<>();


    // ACCESSORS ****************************************************
    public ControllableVoidMethod<?> getVoidMethodNoArgs_history() {
        return this.voidMethodNoArgs_history.createControllable();
    }

    public ControllableVoidMethod<?> getVoidMethod_history() {
        return this.voidMethod_history.createControllable();
    }

    public ControllableVoidMethod.Throwing2CheckedExceptions<?, IOException, JAXBException> getVoidMethodWithCheckedException_history() {
        return this.voidMethodWithCheckedException_history.createControllable();
    }

    public ControllableNonVoidMethod<?, String> getStringReturnMethodNoArgs_history() {
        return this.stringReturnMethodNoArgs_history.createControllable();
    }

    public ControllableNonVoidMethod<?, String> getStringReturnMethod_history() {
        return this.stringReturnMethod_history.createControllable();
    }

    public ControllableNonVoidMethod.Throwing2CheckedExceptions<?, String, IOException, JAXBException> getStringReturnMethodWithCheckedException_history() {
        return this.stringReturnMethodWithCheckedException_history.createControllable();
    }


    // CONTROLLED METHODS ****************************************************

    public void voidMethodNoArgs() {
        this.voidMethodNoArgs_history.exit();
    }

    public void voidMethod(final String arg1) {
        this.voidMethod_history.exit(arg1);
    }

    public void voidMethodWithCheckedException(final String arg1) throws IOException, JAXBException {
        this.voidMethodWithCheckedException_history.exit(arg1);
    }

    public String stringReturnMethodNoArgs() {
        return this.stringReturnMethodNoArgs_history.exit();
    }

    public String stringReturnMethod(final String arg1) {
        return this.stringReturnMethod_history.exit(arg1);
    }

    public String stringReturnMethodWithCheckedException(final String arg1) throws IOException, JAXBException {
        return this.stringReturnMethodWithCheckedException_history.exit(arg1);
    }
}
