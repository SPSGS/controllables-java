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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spsglobalsolutions.controllables.annotations.Controllable;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper methods for processing annotations
 *
 * @author stevo58008
 */
class ProcessorHelper
{

    private static final Set<Modifier> allNonPackageAccessModifiers = Sets.newHashSet(Modifier.PRIVATE, Modifier.PROTECTED, Modifier.PUBLIC);
    private final Types typeUtils;
    private final Elements elementUtils;
    private final Filer filer;
    private final Messager messager;

    public ProcessorHelper(final Types typeUtils, final Elements elementUtils, final Filer filer,
                           final Messager messager)
    {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.filer = filer;
        this.messager = messager;
    }

    /**
     * {@link Messager#printMessage(Diagnostic.Kind, CharSequence, Element)}
     */
    public void error(String reason, Element element)
    {
        System.out
                .println(element + " : " + reason); // double up here, as maven sometimes supresses messages (naughty!).
        this.messager.printMessage(Diagnostic.Kind.ERROR, reason, element);
    }

    /**
     * {@link Messager#printMessage(Diagnostic.Kind, CharSequence, Element)} including the stack trace and message from exception, in the message.
     */
    public void error(String reason, Exception e, Element element)
    {
        String message = reason + System.lineSeparator() + Throwables.getStackTraceAsString(e);
        System.out.println(
                element + " : " + message); // double up here, as maven sometimes supresses messages (naughty!).
        this.messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    /**
     * {@link Messager#printMessage(Diagnostic.Kind, CharSequence, Element)}, stripping required info from exception
     */
    public void error(IllegalAnnotationException e)
    {
        System.out.println(e.getAnnotated() + " : " +
                           e.getMessage()); // double up here, as maven sometimes supresses messages (naughty!).
        this.messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getAnnotated());
    }

    public void debug(final String message, Element element)
    {
        System.out.println(message);
        //this.messager.printMessage(Diagnostic.Kind.NOTE, message, element); -- apparently maven compiler (has a bug) swallows any non error messages.
    }

    public Elements getElementUtils()
    {
        return elementUtils;
    }

    public Types getTypeUtils()
    {
        return this.typeUtils;
    }

    public void printInterfaces(final List<? extends TypeMirror> interfaces)
    {
        for(TypeMirror typeMirror : interfaces)
        {
            System.out.println("Interface typeMirror : " + typeMirror);
            System.out.println("Interface typeMirror Kind : " + typeMirror.getKind());
            System.out.println("Interface typeMirror class : " + typeMirror.getClass());
            TypeElement iface = this.elementUtils.getTypeElement(typeMirror.toString());
            printTypeElement(iface);
        }
    }

    public void printTypeElement(TypeElement element)
    {
        System.out.println("typeElement : " + element);
        System.out.println("typeElement qualName : " + element.getQualifiedName());
        System.out.println("typeElement enclosing ele : " + element.getEnclosingElement());
        System.out.println("typeElement encloded eles : " + element.getEnclosedElements());
        System.out.println("typeElement simpleName : " + element.getSimpleName());
        System.out.println("typeElement nestingKind : " + element.getNestingKind());
        System.out.println("typeElement kind : " + element.getKind());
        System.out.println("typeElement superclass : " + element.getSuperclass());
        System.out.println("typeElement interfaces : " + element.getInterfaces());
    }

    public void printAnnotationMirrors(final List<? extends AnnotationMirror> annotationMirrors)
    {
        for(AnnotationMirror annMirror : annotationMirrors)
        {
            System.out.println("annotationMirror : " + annMirror);
            System.out.println("annotationMirror decType : " + annMirror.getAnnotationType());
            System.out.println("annotationMirror eleVals : " + annMirror.getElementValues());
            printElementValues(annMirror.getElementValues());
        }
    }

    public void printElementValues(final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues)
    {
        for(ExecutableElement exeEle : elementValues.keySet())
        {
            System.out.println("exeEle(" + exeEle + ") annVal(" + elementValues.get(exeEle) + ")");
        }
    }

    public void printContainedMethods(final TypeElement classElement)
    {
        for(Element e : classElement.getEnclosedElements())
        {
            if(e.getKind() == ElementKind.METHOD)
            {
                ExecutableElement exe = (ExecutableElement) e;
                this.debug(String.format("%s %s %s", exe.getModifiers(), exe.getReturnType(), exe), classElement);
            }
        }
    }

    public void printElement(String prefix, ExecutableElement element)
    {
        System.out
                .println(prefix + String.format("%s %s %s", element.getModifiers(), element.getReturnType(), element));
    }

    /**
     * Get the methods of this class
     *
     * @param classElement
     * @param withMatchingModifiers if given, it must contain all the given modifiers, to be included in returned methods
     * @return
     */
    public List<ExecutableElement> getContainedMethodsWithAllModifiers(final TypeElement classElement,
                                                                       Modifier... withMatchingModifiers)
    {
        List<ExecutableElement> methods = new ArrayList<>();
        for(Element e : classElement.getEnclosedElements())
        {
            if(e.getKind() == ElementKind.METHOD)
            {
                if(e.getModifiers().containsAll(Lists.newArrayList(withMatchingModifiers)))
                {
                    methods.add((ExecutableElement) e);
                }
            }
        }
        return methods;
    }

    /**
     * Get the methods of this class
     *
     * @param classElement
     * @param withMatchingModifiers if given, it must have any of the given modifiers, to be included in returned methods
     * @return
     */
    public List<ExecutableElement> getContainedMethodsWithAnyModifiers(final TypeElement classElement,
                                                                       Modifier... withMatchingModifiers)
    {
        List<ExecutableElement> methods = new ArrayList<>();
        for(Element e : classElement.getEnclosedElements())
        {
            if(e.getKind() == ElementKind.METHOD)
            {
                if(Lists.newArrayList(withMatchingModifiers).removeAll(e.getModifiers()))
                {
                    methods.add((ExecutableElement) e);
                }
            }
        }
        return methods;
    }

    /**
     * Get the methods of this class
     *
     * @param classElement
     * @param withMatchingModifiers if given, it must have any of the given modifiers, to be included in returned methods
     * @return
     */
    public List<ExecutableElement> getContainedMethodsWithAnyMethodModifiers(final TypeElement classElement,
                                                                             List<Controllable.MethodModifier> withMatchingModifiers)
    {
        // public, protected, private - can search normally (i.e. see if original would be changed if removed all filters from it)
        // package protected doesn't have a Modifier (argghh) so need to do a reverse check (see if there is not a public/protected/private).
        final boolean requiresPackageLookup = withMatchingModifiers.contains(Controllable.MethodModifier.Package);
        final List<Modifier> modifiersFilter = convertMethodModifiers(withMatchingModifiers);
        List<ExecutableElement> methods = new ArrayList<>();
        for(Element e : classElement.getEnclosedElements())
        {
            if(e.getKind() == ElementKind.METHOD)
            {
                // normal lookup
                if(Lists.newArrayList(modifiersFilter).removeAll(e.getModifiers()))
                {
                    methods.add((ExecutableElement) e);
                }
                // if we need lookup for package
                if(requiresPackageLookup)
                {
                    if(!Lists.newArrayList(allNonPackageAccessModifiers).removeAll(e.getModifiers()))
                    {
                        methods.add((ExecutableElement) e);
                    }
                }
            }
        }
        return methods;
    }

    /**
     * Get the methods of this class
     *
     * @param classElement
     * @param withoutMatchingModifiers if given, it must not have any of the given modifiers, to be included in returned methods
     * @return
     */
    public List<ExecutableElement> getContainedMethodsWithoutAnyModifiers(final TypeElement classElement,
                                                                          Modifier... withoutMatchingModifiers)
    {
        List<ExecutableElement> methods = new ArrayList<>();
        for(Element e : classElement.getEnclosedElements())
        {
            if(e.getKind() == ElementKind.METHOD)
            {
                if(!Lists.newArrayList(withoutMatchingModifiers).removeAll(e.getModifiers()))
                {
                    methods.add((ExecutableElement) e);
                }
            }
        }
        return methods;
    }

    public Filer getFiler()
    {
        return filer;
    }

    public void writeSourceFile(String className, String src, TypeElement originatingType)
    {
        try
        {
            this.debug("Writing File " + className, originatingType);
            JavaFileObject sourceFile = this.filer.createSourceFile(className, originatingType);
            Writer writer = sourceFile.openWriter();
            try
            {
                writer.write(src);
            }
            finally
            {
                writer.close();
            }
        }
        catch(IOException e)
        {
            error("Could not write generated class " + className, e, originatingType);
        }
    }

    private List<Modifier> convertMethodModifiers(final List<Controllable.MethodModifier> withMatchingModifiers)
    {
        List<Modifier> modifiers = new ArrayList<>();
        for(Controllable.MethodModifier withMatchingModifier : withMatchingModifiers)
        {
            Modifier mod = withMatchingModifier.getModifier();
            if(mod != null)
            {
                modifiers.add(mod);
            }
        }
        return modifiers;
    }
}
