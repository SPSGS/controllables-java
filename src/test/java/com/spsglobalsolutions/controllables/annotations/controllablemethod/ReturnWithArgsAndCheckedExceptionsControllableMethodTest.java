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

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author stevo58008
 */
public class ReturnWithArgsAndCheckedExceptionsControllableMethodTest extends
                                                                      CheckedExceptionThrowingControllableMethodTestBase<String, IOException, JAXBException,
                                                                              ControllableNonVoidMethod.Throwing2CheckedExceptions<?, String, IOException, JAXBException>> {

    @Override
    protected IOException getUniqueException1Instance() {
        return new IOException("Junit made me be naughty!");
    }

    @Override
    protected JAXBException getUniqueException2Instance() {
        return new JAXBException("Junit made me be naughty!");
    }


    @Override
    protected ControllableNonVoidMethod.Throwing2CheckedExceptions<?, String, IOException,  JAXBException> getHistoryInstance() {
        return this.exampleInstanceInUse.getStringReturnMethodWithCheckedException_history();
    }

    @Override
    protected String getUniqueReturnValue() {
        return UUID.randomUUID().toString();
    }

    @Override
    protected Object[] getUniqueMethodArguments() {
        return new Object[]{UUID.randomUUID().toString()};
    }

    @Override
    protected void executeMethodUnderTest(final Object[] withArgs) throws Exception {
        String arg1 = TestUtils.castTo(withArgs[0]);
        this.exampleInstanceInUse.stringReturnMethodWithCheckedException(arg1);
    }
}