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

import com.google.auto.service.AutoService;
import com.spsglobalsolutions.controllables.annotations.Controllable;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link AbstractProcessor} which looks for and processes any {@link Controllable} annotations.
 *
 * @author stevo58008
 */
@AutoService(Processor.class)
public class ControllableProcessor extends AbstractProcessor
{

    private ProcessorHelper helper;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for(Element annotated : roundEnv.getElementsAnnotatedWith(Controllable.class))
        {
            try
            {
                new ControllableBuilder(helper, annotated.getAnnotation(Controllable.class), (TypeElement) annotated)
                        .generateControlledClasses();
            }
            catch(IllegalAnnotationException e)
            {
                helper.error(e);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                helper.error("A very very unexpected exception while processing annotations", e, annotated);
            }
        }
        // we don't let exceptions out, as this would probably lead to the JVM crashing (at some point) with a reason that isn't very useful to the user.
        return false;// don't claim the annotation, who knows who else wants to do something with it.
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        return new HashSet<String>(Arrays.asList(Controllable.class.getCanonicalName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        this.helper = new ProcessorHelper(processingEnv.getTypeUtils(), processingEnv.getElementUtils(),
                                          processingEnv.getFiler(), processingEnv.getMessager());
    }

}
