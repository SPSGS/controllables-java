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
 * Contains all the details about a particular method call.
 *
 * @param <MethodReturnType> The type of the return value of the method (for void methods it will use Void class)
 * @author stevo58008
 */
public class MethodCall<MethodReturnType>
{

    private final long callTimeInMillis;
    private final Object[] arguments;
    private final MethodExit<MethodReturnType> exit;

    public MethodCall(final MethodExit<MethodReturnType> howItExited, final Object... arguments)
    {
        this.callTimeInMillis = System.currentTimeMillis();
        this.arguments = arguments;
        this.exit = howItExited;
    }

    /**
     * @return the time, in milliseconds, that the controlled method was called.
     */
    public long getCallTimeInMillis()
    {
        return this.callTimeInMillis;
    }

    /**
     * @return the values of the call arguments of the controlled method.  They will be in the order that they are defined on the method signature.
     */
    public Object[] getArguments()
    {
        return this.arguments;
    }

    /**
     * @return how the execution of the controlled method exited.
     */
    public MethodExit<MethodReturnType> getExit()
    {
        return this.exit;
    }
}
