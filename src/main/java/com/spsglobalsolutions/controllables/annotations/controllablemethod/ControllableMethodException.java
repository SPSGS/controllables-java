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
 * An exception for when things go unexpectedly wrong with this library.  A specific exception for this is useful, since this lib does deal with throwing other exceptions in its
 * standard operation, so this lets things differentiate between exceptions coming out in normal use, and whens somethings wrong (which is when this exception will be thrown).
 *
 * @author stevo58008
 */
public class ControllableMethodException extends RuntimeException
{

    public ControllableMethodException(final String message)
    {
        super(message);
    }

    public ControllableMethodException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public ControllableMethodException(final Throwable cause)
    {
        super(cause);
    }
}
