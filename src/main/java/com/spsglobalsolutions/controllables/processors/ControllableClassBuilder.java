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

import com.google.common.collect.Lists;
import com.spsglobalsolutions.controllables.annotations.controllablemethod.ControllableNonVoidMethod;
import com.spsglobalsolutions.controllables.annotations.controllablemethod.ControllableVoidMethod;
import com.spsglobalsolutions.controllables.annotations.controllablemethod.ControlledNonVoidMethod;
import com.spsglobalsolutions.controllables.annotations.controllablemethod.ControlledVoidMethod;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author stevo58008
 */
class ControllableClassBuilder
{

    static final int CONTROLLABLE_CHECKED_EXCEPTION_LIMIT = 5;

    private static TypeName getWildCard()
    {
        return WildcardTypeName.subtypeOf(Object.class);
    }

    static class ControlledMethod
    {

        // the controlled method e.g. : public String getName(Person of)
        private final String controlledMethodName;
        private final Set<Modifier> controlledMethodModifiers;
        private final boolean isOverload;
        private final TypeName controlledMethodReturnType;
        private final List<TypeName> controlledMethodCheckedExceptions;
        private final List<MethodArg> controlledMethodArgs;

        // the private field holding the MethodCallHistoryBase
        private final String fieldName;
        // the public accessor method to the ControllableMethod
        private final String accessorMethodName;
        private final FieldSpec createdField;
        private final MethodSpec createdControlledMethod;
        private final MethodSpec createdAccessor;
        private TypeName fieldType;
        private TypeName accessorType;

        public ControlledMethod(final String controlledMethodName, final Set<Modifier> controlledMethodModifiers,
                                final TypeMirror controlledMethodReturnType,
                                final List<? extends TypeMirror> controlledMethodCheckedExceptions,
                                final List<MethodArg> controlledMethodArgs, final boolean isOverload)
        {
            this.controlledMethodName = controlledMethodName;
            this.controlledMethodModifiers = controlledMethodModifiers;
            this.isOverload = isOverload;
            this.controlledMethodReturnType = TypeName.get(controlledMethodReturnType);
            this.controlledMethodCheckedExceptions = new ArrayList<>();
            for(final TypeMirror checked : controlledMethodCheckedExceptions)
            {
                this.controlledMethodCheckedExceptions.add(TypeName.get(checked));
            }
            this.controlledMethodArgs = controlledMethodArgs;

            // only tag the args on to the end if we really need to.
            String argsIdentifier = this.isOverload && !this.controlledMethodArgs.isEmpty() ?
                                    "_" + MethodArg.getArgTypesAsCamelCaseStringForMethodNameUniqueness(
                                            this.controlledMethodArgs) : "";
            this.fieldName = "aControlled_" + controlledMethodName + argsIdentifier;
            this.accessorMethodName = this.fieldName;

            this.getCorrectTypes(this.controlledMethodReturnType, this.controlledMethodCheckedExceptions);

            this.createdField = this.createField(controlledMethodModifiers.contains(Modifier.STATIC));
            this.createdAccessor = this.createAccessor();
            this.createdControlledMethod = this.createControlledMethod();
        }

        public FieldSpec getCreatedField()
        {
            return this.createdField;
        }

        public MethodSpec getCreatedControlledMethod()
        {
            return this.createdControlledMethod;
        }

        public MethodSpec getCreatedAccessor()
        {
            return this.createdAccessor;
        }

        private MethodSpec createControlledMethod()
        {
            MethodSpec.Builder builder = MethodSpec.methodBuilder(this.controlledMethodName)
                                                   .addModifiers(this.controlledMethodModifiers)
                                                   .returns(this.controlledMethodReturnType)
                                                   .addExceptions(this.controlledMethodCheckedExceptions);
            for(final MethodArg args : this.controlledMethodArgs)
            {
                builder = builder.addParameter(args.createSpec());
            }
            final String start = this.controlledMethodReturnType.equals(TypeName.VOID) ? "" : "return ";
            builder = builder.addStatement(start + "$N.exit($L)", this.createdField,
                                           MethodArg.getArgNamesAsCommaSepString(this.controlledMethodArgs));
            return builder.build();
        }

        private FieldSpec createField(boolean isStatic)
        {
            List<Modifier> modifiers = Lists.newArrayList(Modifier.PRIVATE, Modifier.FINAL);
            if(isStatic)
            {
                modifiers.add(Modifier.STATIC);
            }
            return FieldSpec.builder(this.fieldType, this.fieldName, modifiers.toArray(new Modifier[]{}))
                            .initializer("new $T()", this.fieldType)
                            .build();
        }

        private MethodSpec createAccessor()
        {
            return MethodSpec.methodBuilder(this.accessorMethodName)
                             .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                             .returns(this.accessorType)
                             .addStatement("return $N.createControllable()", this.createdField)
                             .build();
        }

        private void getCorrectTypes(final TypeName returnType, List<TypeName> checkedExceptions)
        {
            // we only support a certain amount of checked exceptions to be controllable - so need to limit to this number (or could throw exception - but limiting is friendlier)
            if(checkedExceptions.size() > CONTROLLABLE_CHECKED_EXCEPTION_LIMIT)
            {
                checkedExceptions = checkedExceptions.subList(0, CONTROLLABLE_CHECKED_EXCEPTION_LIMIT);
            }
            if(returnType.equals(TypeName.VOID))
            {
                switch(checkedExceptions.size())
                {
                    case 0:
                        this.fieldType = TypeName.get(ControlledVoidMethod.Throwing0CheckedExceptions.class);
                        this.accessorType =
                                ParameterizedTypeName.get(ClassName.get(ControllableVoidMethod.class), getWildCard());
                        break;
                    case 1:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledVoidMethod.Throwing1CheckedException.class),
                                     checkedExceptions.get(0));
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableVoidMethod.Throwing1CheckedException.class),
                                     getWildCard(), checkedExceptions.get(0));
                        break;
                    case 2:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledVoidMethod.Throwing2CheckedExceptions.class),
                                     checkedExceptions.get(0), checkedExceptions.get(1));
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableVoidMethod.Throwing2CheckedExceptions.class),
                                     getWildCard(), checkedExceptions.get(0),
                                     checkedExceptions.get(1));
                        break;
                    case 3:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledVoidMethod.Throwing3CheckedExceptions.class),
                                     checkedExceptions.get(0), checkedExceptions.get(1),
                                     checkedExceptions.get(2));
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableVoidMethod.Throwing3CheckedExceptions.class),
                                     getWildCard(), checkedExceptions.get(0),
                                     checkedExceptions.get(1),
                                     checkedExceptions.get(2));
                        break;
                    case 4:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledVoidMethod.Throwing4CheckedExceptions.class),
                                     checkedExceptions.get(0), checkedExceptions.get(1),
                                     checkedExceptions.get(2),
                                     checkedExceptions.get(3));
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableVoidMethod.Throwing4CheckedExceptions.class),
                                     getWildCard(), checkedExceptions.get(0),
                                     checkedExceptions.get(1),
                                     checkedExceptions.get(2),
                                     checkedExceptions.get(3));
                        break;
                    case 5:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledVoidMethod.Throwing5CheckedExceptions.class),
                                     checkedExceptions.get(0), checkedExceptions.get(1),
                                     checkedExceptions.get(2),
                                     checkedExceptions.get(3), checkedExceptions.get(4));
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableVoidMethod.Throwing5CheckedExceptions.class),
                                     getWildCard(), checkedExceptions.get(0),
                                     checkedExceptions.get(1),
                                     checkedExceptions.get(2),
                                     checkedExceptions.get(3), checkedExceptions.get(4));
                        break;
                }
            }
            else
            {
                switch(checkedExceptions.size())
                {
                    case 0:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledNonVoidMethod.Throwing0CheckedExceptions.class),
                                     this.controlledMethodReturnType.box());
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableNonVoidMethod.class), getWildCard(),
                                     this.controlledMethodReturnType.box());
                        break;
                    case 1:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledNonVoidMethod.Throwing1CheckedException.class),
                                     this.controlledMethodReturnType.box(), checkedExceptions.get(0));
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableNonVoidMethod.Throwing1CheckedException.class),
                                     getWildCard(),
                                     this.controlledMethodReturnType.box(), checkedExceptions.get(0));
                        break;
                    case 2:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledNonVoidMethod.Throwing2CheckedExceptions.class),
                                     this.controlledMethodReturnType.box(), checkedExceptions.get(0),
                                     checkedExceptions.get(1));
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableNonVoidMethod.Throwing2CheckedExceptions.class),
                                     getWildCard(),
                                     this.controlledMethodReturnType.box(), checkedExceptions.get(0),
                                     checkedExceptions.get(1));
                        break;
                    case 3:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledNonVoidMethod.Throwing3CheckedExceptions.class),
                                     this.controlledMethodReturnType.box(), checkedExceptions.get(0),
                                     checkedExceptions.get(1), checkedExceptions.get(2));
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableNonVoidMethod.Throwing3CheckedExceptions.class),
                                     getWildCard(),
                                     this.controlledMethodReturnType.box(), checkedExceptions.get(0),
                                     checkedExceptions.get(1),
                                     checkedExceptions.get(2));
                        break;
                    case 4:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledNonVoidMethod.Throwing4CheckedExceptions.class),
                                     this.controlledMethodReturnType.box(), checkedExceptions.get(0),
                                     checkedExceptions.get(1), checkedExceptions.get(2),
                                     checkedExceptions.get(3));
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableNonVoidMethod.Throwing4CheckedExceptions.class),
                                     getWildCard(),
                                     this.controlledMethodReturnType.box(), checkedExceptions.get(0),
                                     checkedExceptions.get(1),
                                     checkedExceptions.get(2),
                                     checkedExceptions.get(3));
                        break;
                    case 5:
                        this.fieldType = ParameterizedTypeName
                                .get(ClassName.get(ControlledNonVoidMethod.Throwing5CheckedExceptions.class),
                                     this.controlledMethodReturnType.box(), checkedExceptions.get(0),
                                     checkedExceptions.get(1), checkedExceptions.get(2),
                                     checkedExceptions.get(3), checkedExceptions.get(4));
                        this.accessorType = ParameterizedTypeName
                                .get(ClassName.get(ControllableNonVoidMethod.Throwing5CheckedExceptions.class),
                                     getWildCard(),
                                     this.controlledMethodReturnType.box(), checkedExceptions.get(0),
                                     checkedExceptions.get(1),
                                     checkedExceptions.get(2),
                                     checkedExceptions.get(3), checkedExceptions.get(4));
                        break;
                }
            }
        }
    }

    static class MethodArg
    {

        private final TypeName type;
        private final String name;
        private final Modifier[] modifiers;

        public MethodArg(final TypeMirror type, final String name, final Set<Modifier> modifiers)
        {
            this.type = TypeName.get(type);
            this.name = name;
            this.modifiers = modifiers != null ? modifiers.toArray(new Modifier[]{}) : new Modifier[]{};
        }

        public static String getArgNamesAsCommaSepString(final List<MethodArg> args)
        {
            final StringBuilder sb = new StringBuilder();
            for(final MethodArg arg : args)
            {
                sb.append(arg.name).append(",");
            }
            if(sb.length() > 0)
            {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        }

        public static String getArgTypesAsCamelCaseStringForMethodNameUniqueness(final List<MethodArg> args)
        {
            final StringBuilder sb = new StringBuilder();
            for(final MethodArg arg : args)
            {
                sb.append(getTypeSuitableForUsingInMethodName(arg));
            }
            return sb.toString();
        }

        private static String getTypeSuitableForUsingInMethodName(MethodArg arg)
        {
            return arg.type.toString().replaceAll("[\\w\\.]*\\.", "").replaceAll("<.*?>", "")
                           .replaceAll("\\[\\]", "Array");
        }

        public static Iterable<ParameterSpec> createSpecs(final List<MethodArg> args)
        {
            List<ParameterSpec> specs = new ArrayList<>();
            for(MethodArg arg : args)
            {
                specs.add(arg.createSpec());
            }
            return specs;
        }

        public ParameterSpec createSpec()
        {
            return ParameterSpec.builder(this.type, this.name, this.modifiers).build();
        }

        @Override
        public String toString()
        {
            final StringBuilder sb = new StringBuilder("MethodArg{");
            sb.append("type=").append(this.type);
            sb.append(", name='").append(this.name).append('\'');
            sb.append(", modifiers=").append(Arrays.toString(this.modifiers));
            sb.append('}');
            return sb.toString();
        }
    }
}
