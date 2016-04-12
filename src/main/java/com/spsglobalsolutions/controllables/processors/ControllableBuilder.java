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

import com.spsglobalsolutions.controllables.annotations.Controllable;
import com.squareup.javapoet.*;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A class which validates a {@link Controllable} annotated class, and can build the functionality that is expected.
 *
 * @author stevo58008
 */
class ControllableBuilder
{

    private final static Pattern PATTERN_FOR_NOSUPERCLASS = Pattern.compile("[<]?none[>]?");
    private final Controllable annotation;
    private final ProcessorHelper helper;
    private final TypeElement classAnnotated;
    private final List<TypeElement> controllingClassesToGenerate;
    private final boolean createSeparateControllableClasses;
    private final String packageWhereToGenerate;
    private final List<Controllable.MethodModifier> methodsToControlFilter;
    private final TypeMirror mirrorOfBaseObject;

    ControllableBuilder(final ProcessorHelper helper, final Controllable annotation, final TypeElement annotated)
            throws IllegalAnnotationException
    {
        this.helper = helper;
        this.mirrorOfBaseObject = this.helper.getElementUtils().getTypeElement(Object.class.getName()).asType();
        this.classAnnotated = annotated;
        this.packageWhereToGenerate = this.classAnnotated.getEnclosingElement().toString();
        this.annotation = annotation;
        try
        {
            this.helper.debug("Building " + this.annotation + " for " + annotated.getSimpleName(), annotated);
            // must be a non final class
            if(annotated.getKind() != ElementKind.CLASS)
            {
                throw new IllegalAnnotationException("This annotation must be used on a class", annotation, annotated);
            }

            // get the separateControllables value (if it is default/empty then check for extended superclass).
            final List<TypeElement> classesToControl = this.getTypeElementsOfClassesToControl(annotation);
            this.createSeparateControllableClasses = !classesToControl.isEmpty(); // if they have specified seperate
            if(classesToControl.isEmpty())
            {
                this.onlyAddIfNotFinal(classesToControl, annotated);
            }
            this.helper.debug(String.format("Classes found to Control : %s", classesToControl), annotated);
            if(classesToControl.isEmpty())
            {
                throw new IllegalAnnotationException(
                        "No classes/interfaces were found which are suitable for controlling (i.e. are non-final)",
                        annotation, annotated);
            }
            this.controllingClassesToGenerate = classesToControl;

            // how are we filtering which methods to control
            this.methodsToControlFilter = new ArrayList<>();
            Collections.addAll(this.methodsToControlFilter, annotation.includedModifierFilter());
        }
        catch(final IllegalAnnotationException e)
        {
            throw e;
        }
        catch(final Exception e)
        {
            throw new IllegalAnnotationException(e, annotation, annotated);
        }
    }

    void generateControlledClasses() throws IOException, IllegalAnnotationException
    {
        // what we need to do.
        // - for separates, then we need to create new generated classes (names after controlled class) for each controlled class.
        // - for non separates, then we need to create a single generated class (named after annotated), but combine all
        //   methods from all controlled classes
        if(this.createSeparateControllableClasses)
        {
            for(final TypeElement classToControl : this.controllingClassesToGenerate)
            {
                this.generateControlledClass("Controllable_" + classToControl.getSimpleName(), classToControl);
            }
        }
        else
        {
            this.generateControlledClass("Controllable_" + this.classAnnotated.getSimpleName(),
                                         this.controllingClassesToGenerate.get(0));
        }
    }

    /**
     * find any methods (which have any of the modifiers on @{link#methodsToControlFilter}) on any of the classes that the given class inherits from (apart from Object of course)
     *
     * @param foundMethods           all the ones we have found (keyed by ExecutableElement.toString() - which deals with dup methods)
     * @param inheritedFromThisClass the class to add and look to see if it has any inheritance
     */
    private void findAllMethodsNeededToControl(final Map<String, ExecutableElement> foundMethods,
                                               final Map<String, Map<TypeMirror, TypeMirror>> foundGenericArgsForMethods,
                                               final TypeElement inheritedFromThisClass,
                                               final Map<TypeMirror, TypeMirror> genericTypeNamesAndValuesPossibleForInheritedClass)
    {
        this.helper.debug("Find all methods needing controlling on " + inheritedFromThisClass, inheritedFromThisClass);
        if(!inheritedFromThisClass.getModifiers().contains(Modifier.FINAL))
        {
            // all matching filter methods on this class
            final List<ExecutableElement> methodsToInclude = this.helper
                    .getContainedMethodsWithAnyMethodModifiers(inheritedFromThisClass, this.methodsToControlFilter);
            this.helper.debug(String.format("Methods found : %s", methodsToInclude), inheritedFromThisClass);
            for(ExecutableElement method : methodsToInclude)
            {
                foundMethods.put(method.toString(), method);
                if(genericTypeNamesAndValuesPossibleForInheritedClass != null)
                {
                    foundGenericArgsForMethods
                            .put(method.getSimpleName().toString(), genericTypeNamesAndValuesPossibleForInheritedClass);
                }
            }

            // any superclass or interfaces
            for(TypeMirror mirror : this.helper.getTypeUtils().directSupertypes(inheritedFromThisClass.asType()))
            {
                if(!this.helper.getTypeUtils().isSameType(this.mirrorOfBaseObject, mirror))
                {
                    System.out.println("Some sort of super class found : " + mirror.toString());

                    TypeElement superElement = (TypeElement) this.helper.getTypeUtils().asElement(mirror);
                    Map<TypeMirror, TypeMirror> genericArgNamesAndValuesForSuper = null;

                    // check if we need to extract any generics out of the class
                    final DeclaredType declaredType = (DeclaredType) mirror;
                    final List<? extends TypeMirror> genericArgsValues = declaredType.getTypeArguments();
                    if(!genericArgsValues.isEmpty())
                    {
                        final List<? extends TypeMirror> genericArgNames =
                                ((DeclaredType) superElement.asType()).getTypeArguments();
                        System.out.println(
                                "Generic Types found : " + Arrays.toString(genericArgNames.toArray()) + " = " +
                                Arrays.toString(genericArgsValues.toArray()));
                        genericArgNamesAndValuesForSuper = new HashMap<>();
                        for(int i = 0; i < genericArgNames.size(); i++)
                        {
                            genericArgNamesAndValuesForSuper.put(genericArgNames.get(i), genericArgsValues.get(i));
                        }
                    }

                    this.findAllMethodsNeededToControl(foundMethods, foundGenericArgsForMethods, superElement,
                                                       genericArgNamesAndValuesForSuper);
                }
            }
        }
    }


    private void generateControlledClass(final String nameOfClassToGenerate, final TypeElement classToControl)
            throws IllegalAnnotationException, IOException
    {
        this.helper.debug("Generating class " + this.packageWhereToGenerate + "." + nameOfClassToGenerate +
                          " which is controlling " + classToControl, this.classAnnotated);

        final Map<String, ExecutableElement> allMethodsForThisGeneratedClass = new HashMap<>();
        final Map<String, Map<TypeMirror, TypeMirror>> allPossibleGenericArgValuesForMethods = new HashMap<>();
        TypeSpec.Builder classBuilder =
                TypeSpec.classBuilder(nameOfClassToGenerate).addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        // extends or implements
        if(classToControl.getKind() == ElementKind.INTERFACE)
        {
            classBuilder.addSuperinterface(TypeName.get(classToControl.asType()));
        }
        else
        {
            classBuilder = classBuilder.superclass(TypeName.get(classToControl.asType()));

            // need to check constructors
            // - if there are only private ones, then this class cannot be extended and it is game over; otherwise impl all non-private ones.
            final List<ExecutableElement> privateConstructors = new ArrayList<>();
            final List<ExecutableElement> nonprivateConstructors = new ArrayList<>();
            for(final ExecutableElement constructor : ElementFilter
                    .constructorsIn(classToControl.getEnclosedElements()))
            {
                if(constructor.getModifiers().contains(Modifier.PRIVATE))
                {
                    privateConstructors.add(constructor);
                }
                else
                {
                    nonprivateConstructors.add(constructor);
                }
            }
            if(!privateConstructors.isEmpty() && nonprivateConstructors.isEmpty())
            {
                throw new IllegalAnnotationException("The superclass " + classToControl +
                                                     " only has private constructors and therefore cannot be extended (or controlled).",
                                                     this.annotation, classToControl);
            }
            // - impl all the non-private ones
            for(final ExecutableElement constructor : nonprivateConstructors)
            {
                this.helper.printElement("Generating super constructor : ", constructor);
                classBuilder = classBuilder.addMethod(this.copySuperConstructor(constructor, null));
            }
        }
        this.findAllMethodsNeededToControl(allMethodsForThisGeneratedClass, allPossibleGenericArgValuesForMethods,
                                           classToControl, null);
        System.out.println(
                String.format("Possible generic args found for methods : %s", allPossibleGenericArgValuesForMethods));
        this.addImplementationAndCreateFile(classBuilder, allMethodsForThisGeneratedClass.values(),
                                            allPossibleGenericArgValuesForMethods);
    }


    /**
     * This could be for one class, or for multiple interfaces - which is why i pass in everything that needs to go in the class
     *
     * @param classBuilder
     * @param containedMethods
     * @param allPossibleGenericArgValuesForMethods
     * @throws IOException
     * @throws IllegalAnnotationException
     */
    private void addImplementationAndCreateFile(final TypeSpec.Builder classBuilder,
                                                final Collection<ExecutableElement> containedMethods,
                                                final Map<String, Map<TypeMirror, TypeMirror>> allPossibleGenericArgValuesForMethods)
            throws IOException, IllegalAnnotationException
    {
        // run over all methods first, so we can work out which ones are overloads
        final Map<String, Boolean> methodNamesAndIsOverloaded = new HashMap<>();
        for(final ExecutableElement method : containedMethods)
        {
            final String methodName = method.getSimpleName().toString();
            methodNamesAndIsOverloaded.put(methodName, methodNamesAndIsOverloaded.containsKey(methodName));
        }

        for(final ExecutableElement method : containedMethods)
        {
            final String controlledMethodName = method.getSimpleName().toString();
            final Set<Modifier> controlledMethodModifiers = this.copyModifiersMinusAbstract(method.getModifiers());
            final TypeMirror controlledMethodReturnType = replaceGenericTypeWithValueIfNeeded(method.getReturnType(),
                                                                                              allPossibleGenericArgValuesForMethods
                                                                                                      .get(controlledMethodName));
            final List<? extends TypeMirror> controlledMethodCheckedExceptions = method.getThrownTypes();
            final List<ControllableClassBuilder.MethodArg> controlledMethodArgs =
                    this.getParamsOfMethod(method, allPossibleGenericArgValuesForMethods.get(controlledMethodName));
            if(controlledMethodModifiers.contains(Modifier.FINAL))
            {
                this.helper
                        .debug(String.format(
                                "Skipping Method with final modifier: name(%s) modifiers(%s) returnType(%s) checkedExceptions(%s) args(%s)",
                                controlledMethodName,
                                controlledMethodModifiers,
                                controlledMethodReturnType, controlledMethodCheckedExceptions, controlledMethodArgs),
                               this.classAnnotated);
                continue; // final methods can't be implemented
            }
            this.helper
                    .debug(String.format(
                            "Creating Method : name(%s) modifiers(%s) returnType(%s) checkedExceptions(%s) args(%s)",
                            controlledMethodName, controlledMethodModifiers,
                            controlledMethodReturnType, controlledMethodCheckedExceptions, controlledMethodArgs),
                           this.classAnnotated);

            if(controlledMethodCheckedExceptions.size() > ControllableClassBuilder.CONTROLLABLE_CHECKED_EXCEPTION_LIMIT)
            {
                this.helper.debug(method +
                                  " has more checked exceptions than we support controllably - limiting controllable checked exceptions to first " +
                                  ControllableClassBuilder.CONTROLLABLE_CHECKED_EXCEPTION_LIMIT, method);
            }

            final ControllableClassBuilder.ControlledMethod controlledMethod =
                    new ControllableClassBuilder.ControlledMethod(controlledMethodName, controlledMethodModifiers,
                                                                  controlledMethodReturnType,
                                                                  controlledMethodCheckedExceptions,
                                                                  controlledMethodArgs,
                                                                  methodNamesAndIsOverloaded.get(controlledMethodName));

            classBuilder.addMethod(controlledMethod.getCreatedControlledMethod())
                        .addField(controlledMethod.getCreatedField())
                        .addMethod(controlledMethod.getCreatedAccessor());
        }
        final JavaFile srcFile = JavaFile.builder(this.packageWhereToGenerate, classBuilder.build()).build();
        this.helper.debug("Outputting source file of new class ", this.classAnnotated);
        srcFile.writeTo(this.helper.getFiler());
    }

    private TypeMirror replaceGenericTypeWithValueIfNeeded(final TypeMirror original,
                                                           final Map<TypeMirror, TypeMirror> possibleGenericArgReplacements)
    {
        if(possibleGenericArgReplacements != null)
        {
            System.out.println(String.format("Possible Generic Type Replacement : %s replacements(%s)", original,
                                             possibleGenericArgReplacements));
            TypeMirror replacement = possibleGenericArgReplacements.get(original);
            if(replacement != null)
            {
                return replacement;
            }
        }
        return original;
    }

    private Set<Modifier> copyModifiersMinusAbstract(final Set<Modifier> modifiers)
    {
        final Set<Modifier> mods = new HashSet<>();
        for(final Modifier m : modifiers)
        {
            if(m != Modifier.ABSTRACT)
            {
                mods.add(m);
            }
        }
        return mods;
    }

    private List<ControllableClassBuilder.MethodArg> getParamsOfMethod(final ExecutableElement method,
                                                                       final Map<TypeMirror, TypeMirror> possibleGenericArgReplacements)
    {
        final List<ControllableClassBuilder.MethodArg> params = new ArrayList<>();
        for(final VariableElement param : method.getParameters())
        {
            TypeMirror paramType = replaceGenericTypeWithValueIfNeeded(param.asType(), possibleGenericArgReplacements);
            params.add(new ControllableClassBuilder.MethodArg(paramType, param.getSimpleName().toString(),
                                                              param.getModifiers()));
        }
        return params;
    }

    private MethodSpec copySuperConstructor(final ExecutableElement superConstructor,
                                            final Map<TypeMirror, TypeMirror> possibleGenericArgReplacements)
    {
        final List<ControllableClassBuilder.MethodArg> paramsOfMethod =
                this.getParamsOfMethod(superConstructor, possibleGenericArgReplacements);
        final String argNamesAsCommaSepString =
                ControllableClassBuilder.MethodArg.getArgNamesAsCommaSepString(paramsOfMethod);
        final Iterable<ParameterSpec> parameters = ControllableClassBuilder.MethodArg.createSpecs(paramsOfMethod);
        return MethodSpec.constructorBuilder()
                         .addModifiers(superConstructor.getModifiers())
                         .addParameters(parameters)
                         .addExceptions(this.copyThrows(superConstructor.getThrownTypes()))
                         .addStatement("super($L)", argNamesAsCommaSepString)
                         .build();
    }

    private Iterable<? extends TypeName> copyThrows(final List<? extends TypeMirror> thrownTypes)
    {
        final List<TypeName> copied = new ArrayList<>();
        for(final TypeMirror throwType : thrownTypes)
        {
            copied.add(TypeName.get(throwType));
        }
        return copied;
    }

    private List<TypeElement> getTypeElementsOfClassesToControl(final Controllable annotation)
    {
        try
        {
            annotation.separateControllables(); // will throw exception;
        }
        catch(final MirroredTypesException e)
        {
            final List<TypeElement> nonFinalElements = new ArrayList<>();
            for(final TypeMirror mirror : e.getTypeMirrors())
            {
                this.onlyAddIfNotFinal(nonFinalElements,
                                       this.helper.getElementUtils().getTypeElement(mirror.toString()));
            }
            return nonFinalElements;
        }
        throw new IllegalStateException(
                "A MirroredTypesException was expected (and needed - to be able to get the TypeMirror for the class values of separateControllables())");
    }

    /**
     * @param listToAddTo
     * @param maybeAdd
     * @return if it was added
     */
    private boolean onlyAddIfNotFinal(final Collection<TypeElement> listToAddTo, final TypeElement maybeAdd)
    {
        if(maybeAdd.getModifiers().contains(Modifier.FINAL))
        {
            this.helper.debug("Skipping " + maybeAdd + " as it is final, and therefore cannot be controlled",
                              this.classAnnotated);
            return false;
        }
        else
        {
            listToAddTo.add(maybeAdd);
            return true;
        }
    }
}
