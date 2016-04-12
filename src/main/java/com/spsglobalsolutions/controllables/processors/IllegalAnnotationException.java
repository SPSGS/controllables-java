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

package com.spsglobalsolutions.controllables.processors;

import com.google.common.base.Throwables;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.Scanner;

/**
 * An exception thrown when an annotation cannot be processed, and which contains information which will help identify which annotation and where it was misused.
 *
 * @author stevo58008
 */
public class IllegalAnnotationException extends Exception
{

    private final Element annotated;

    /**
     * @param description description of what was wrong
     * @param annotation  The annotation that could not be processed.
     * @param annotated   Whatever the annotation was used on.
     */
    public IllegalAnnotationException(final String description, final Annotation annotation, final Element annotated)
    {
        super(buildErrorMessage(description, annotation, annotated));
        this.annotated = annotated;
    }

    /**
     * @param cause      What caused it
     * @param annotation The annotation that could not be processed.
     * @param annotated  Whatever the annotation was used on.
     */
    public IllegalAnnotationException(final Throwable cause, final Annotation annotation, final Element annotated)
    {
        super(buildErrorMessage(cause, annotation, annotated));
        this.annotated = annotated;
    }

    private static String buildErrorMessage(final Throwable cause, final Annotation annotation, final Element annotated)
    {
        // the messenger (used to output the error to a users) cannot show new lines (for some reason) so what we want to give them is the what happened and the line number of
        // where it happend (not as good as a full stack trace, but better than nothing).  So we convert all lines sep until we have a line number which is in our world
        String description = getStackTraceUptoFirstSgsCall(cause).replaceAll(System.lineSeparator(), " ");
        return buildErrorMessage(description, annotation, annotated);
    }

    /**
     * Get the stack trace, as a string, upto and including, the first call that is in the same package as this class.
     *
     * @param cause
     * @return
     */
    private static String getStackTraceUptoFirstSgsCall(final Throwable cause)
    {
        Scanner scanner = new Scanner(Throwables.getStackTraceAsString(cause));
        StringBuilder sb = new StringBuilder();
        while(scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            sb.append(line);
            if(line.contains(IllegalAnnotationException.class.getPackage().getName()))
            {
                break; // we have found first sgs line so stop.
            }
        }
        return sb.toString();
    }


    private static String buildErrorMessage(final String description, final Annotation annotation,
                                            final Element annotated)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("The ").append(annotation).append(" annotation used to annotate the ")
          .append(elementInfoToString(annotated)).append(", failed because... ")
          .append(description);
        return sb.toString();
    }

    private static String elementInfoToString(Element element)
    {
        return String.format("%s %s %s", element.getModifiers(), element.getKind(), element);
    }


    public Element getAnnotated()
    {
        return this.annotated;
    }
}
