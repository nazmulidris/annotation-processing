/*
 * Copyright 2020 Nazmul Idris. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package codegen

import processor.SUFFIX_FOR_GENERATED_CLASS
import java.lang.reflect.Constructor

/**
 * Allow access to the `index.AdapterIndex` class, and any adapter classes that are generated for a given model type
 * w/out having to know the full name of the generated classes at compile time. You can safely access the names
 * directly, but this is just an approach using reflection to achieve the same goal.
 */
object AdapterUtils {

  fun <T> createBindingForModel(klass: Class<out T>, items: List<T>): Any? {
    val bindingClassName = klass.name + SUFFIX_FOR_GENERATED_CLASS
    try {
      val bindingClass: Class<*> = Class.forName(bindingClassName)
      val constructor: Constructor<out Any> = bindingClass.getConstructor(List::class.java)
      return constructor.newInstance(items)
    }
    catch (e: ClassNotFoundException) {
      e.printStackTrace()
    }
    catch (e: NoSuchMethodException) {
      e.printStackTrace()
    }
    return null
  }

  fun getAdapterIndex(): Any? {
    return Class.forName("index.AdapterIndex").constructors[0].newInstance()
  }

}