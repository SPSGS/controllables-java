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

package com.spsglobalsolutions.controllables.annotations;

import javax.lang.model.element.Modifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which can be applied to a class (concrete/abstract/interface).  When applied to a class, on the next compile, an annotation processor will pick it up and
 * generate one or more Controllable classes depending if the {@link #separateControllables()} returns an empty list or not.
 * <p/>
 * If {@link #separateControllables()} returns a non-empty list, then separate controllable classes will be generated (each in the same package as the annotated class, and with a
 * name which matches the controlled class name, prefixed with "Controllable_") for each of the classes in the list.  Each controllable class will contain controlled methods for
 * all non-final methods within the class, which match the given {@link #includedModifierFilter()} (the default for this is just public methods).
 * <p/>
 * If {@link #separateControllables()} returns an empty list, then a single controllable class will be generated (in the same package, and named the same - except with a
 * "Controllable_" prefix, as the annotated class), and the annotated classes signature is used to determine the controllable methods to generate.
 * <p/>
 * In both cases, all nonfinal methods, which are available to the controlled class (whether by inhertited or not), are controlled.
 * <p/>
 * A controllable version of a class means that all the non-final methods of the class will have an accompanying method (named the same as the original method,
 * but with a "aControlled_" prefix) which returns an appropriate interface extension of the {@code MethodHistory} interface.
 * <p/>
 * There are 2 main types of {@code MethodHistory} subinterface which may be returned, {@code ReturnMethodHistory} and {@code VoidMethodHistory}, depending on the controlled
 * method's return type.  Each of these subinterface types then has variations to cover throwing 0 to 5 different checked exceptions (unfortunately due to limitations of generics,
 * there has had to be a separate interface for each variation - and if you want more than 5 checked execptions then you will need to define a new subinterface for it).
 * <p/>
 * Each controlled method has 2 basic functions; setting how the controlled method should exit when it is next called, and getting the current call history.  The "exits" are
 * stored in a list, and are replayed in a FIFO order when the controlled method is executed.  There must be at least one exit set for a method (otherwise a validation exception
 * will be thrown on the first execution of the controlled method), and the last exit in the list will be used as the default exit and will not be removed from the list (unless
 * more exits are added).
 * <ul>
 * <li>{@code getHistorySnapshot()} - returns a list which represents an immutable snapshot of the current call history of the controlled method at this point in time.  The
 * history contains the call time, given call arguments, and exit value for each execution of the controlled method up to this point in time. This is available to all method
 * types, but the return type of the {@code MethodCall} exit will match the controlled method appropriately.</li>
 * <li>{@code addUncheckedExceptionExit(RuntimeException e)} - adds an exit, by throwing the given RuntimeException from the controlled method. This is available to all method
 * types.</li>
 * <li>{@code addException1Exit(Exception e)} - adds an exit, by throwing the given Exception from the controlled method.  This is only available to methods which actually throw
 * Checked exceptions, and there will be one of these methods for each of the different type of checked exception (up to the supported limit - which is currently 5).</li>
 * </ul>
 * <p/>
 * There are two main suggested approaches to using this...
 * <p/>
 * 1. Make use of the {@link #separateControllables()} and have a static util class which will return you instances of many different Controllables
 * <pre>
 *     <code>
 *         {@literal @}Controllable(separateControllables = {ControlThis.class, ControlThat.class, ControlSomeThingElse.class})
 *         public class AllMyControllables{
 *             public static Controllable_ControlThis getControllableControlThis(){
 *                 return new Controllable_ControlThis();
 *             }
 *
 *             public static Controllable_ControlThat getControllableControlThat(){
 *                 return new Controllable_ControlThat();
 *             }
 *
 *             public static Controllable_ControlSomeThingElse getControlSomeThingElse(){
 *                 return new Controllable_ControlSomeThingElse();
 *             }
 *         }
 *     </code>
 * </pre>
 * <p/>
 * 2. Ignore the {@link #separateControllables()}, and use an abstract class with an implements or extends, to define a single controllable class.
 * <pre>
 *     <code>
 *         {@literal @}Controllable
 *         public abstract class MyControllableControlThis extends ControlThis {
 *             public static Controllable_ControlThis getControllable(){
 *                 return new Controllable_ControlThis();
 *             }
 *         }
 *     </code>
 * </pre>
 * <p/>
 * Personally, I would stick to option 1.  The public static getters are actually optional (you could just put the {@code new Controllable_blabla()} in code yourself), but i think
 * it makes things a little neater and clearer having it.
 *
 * @author stevo58008
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Controllable
{

    /**
     * Optionally, you can provide a list of classes/interfaces you want to generate controllable version for.
     *
     * @return all the classes/interfaces that you want to generate controllable versions of.
     */
    Class[] separateControllables() default {};

    /**
     * Optionally, you can specify a list of {@link MethodModifier} values to filter which methods will be controlled.  A method will be controlled if it matches any of the given
     * values.  By default this returns a list containing just {@link MethodModifier#Public} and {@link MethodModifier#Abstract}.
     *
     * @return
     */
    MethodModifier[] includedModifierFilter() default {MethodModifier.Public, MethodModifier.Abstract};

    enum MethodModifier
    {
        Public(Modifier.PUBLIC), Private(Modifier.PRIVATE), Protected(Modifier.PROTECTED), Package(null),
        Abstract(Modifier.ABSTRACT);


        private final Modifier modifier;

        MethodModifier(Modifier modifier)
        {
            this.modifier = modifier;
        }

        public Modifier getModifier()
        {
            return this.modifier;
        }
    }
}
