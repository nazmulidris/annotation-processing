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

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import processor.AdapterModelAnnotationMetadata
import java.io.File

data class ClassAnnotationHolder(val name: String, val list: MutableList<PropertyAnnotationHolder> = mutableListOf())
data class PropertyAnnotationHolder(val name: String)

/**
 * Build a static index of annotated classes. There are times when it would be useful to find all the classes that are
 * annotated w/ a particular annotation.
 *
 * For a made up example, in our activity, instead of populating the RecyclerView adapter w/ dummy data, we could have
 * found all the classes and methods where our annotations appear in the code, and then display that in the list.
 *
 * This is exactly what this code generator does. And it saves the generated index to `index.AdapterIndex.kt` file. You
 * can also just access it reflectively using [AdapterUtils.getAdapterIndex].
 */
class AdapterIndexGeneratorBuilder {

  // Index generation.

  companion object {
    const val PACKAGE_NAME = "index"
    const val CLASS_NAME = "AdapterIndex"
    const val PROPERTY_ANNOTATION_HOLDER = "PropertyAnnotationHolder"
    const val CLASS_ANNOTATION_HOLDER = "ClassAnnotationHolder"
    private val index: MutableList<ClassAnnotationHolder> = mutableListOf()

    fun addToIndex(adapterModelMetadata: AdapterModelAnnotationMetadata) {
      index.add(ClassAnnotationHolder(adapterModelMetadata.generatedAdapterClassName).apply {
        adapterModelMetadata.viewHolderBindingAnnotations.map { viewHolderBindingAnnotationMetadata ->
          list.add(PropertyAnnotationHolder(viewHolderBindingAnnotationMetadata.fieldName))
        }
      })
    }

    fun printIndex(processor: processor.Processor) {
      processor.printNoteMessage("debugList: $index")
    }
  }

  // Code generation.

  /** Populate the fields w/ default values for class + property annotation names. */
  private fun build(): TypeSpec {
    // TypeNames that are used below.
    val mutableList: ClassName = ClassName("kotlin.collections", "MutableList")
    val mutableListOfPropertyAnnotationHolder: ParameterizedTypeName =
        mutableList.parameterizedBy(ClassName("", PROPERTY_ANNOTATION_HOLDER))
    val mutableListOfClassAnnotationHolder: ParameterizedTypeName =
        mutableList.parameterizedBy(ClassName("", CLASS_ANNOTATION_HOLDER))

    // Generate the outer class.
    return TypeSpec.classBuilder(CLASS_NAME)
        .addType(
            buildNestedClassForPropertyAnnotationHolder()
        )
        .addType(
            buildNestedClassForClassAnotationHolder(mutableListOfPropertyAnnotationHolder)
        )
        .primaryConstructor(
            buildPrimaryConstructorForOuterClass()
        )
        .addProperty(
            PropertySpec.builder("index", mutableListOfClassAnnotationHolder)
                .initializer("mutableListOf()")
                .build()
        )
        .build()
  }

  private fun buildNestedClassForClassAnotationHolder(mutableListOfPropertyAnnotationHolder: ParameterizedTypeName): TypeSpec {
    val classAnnotationHolderType = TypeSpec.classBuilder(CLASS_ANNOTATION_HOLDER)
        .addModifiers(KModifier.DATA)
        // The three following function calls generate a merged primary constructor parameter & property.
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("name", STRING)
                .addParameter("list", mutableListOfPropertyAnnotationHolder)
                .build()
        )
        .addProperty(
            PropertySpec.builder("name", String::class)
                .initializer("name")
                .build()
        )
        .addProperty(
            PropertySpec.builder("list", mutableListOfPropertyAnnotationHolder)
                .initializer("list")
                .build()
        )
        .build()
    return classAnnotationHolderType
  }

  private fun buildNestedClassForPropertyAnnotationHolder(): TypeSpec {
    val propertyAnnotationHolderType = TypeSpec.classBuilder(PROPERTY_ANNOTATION_HOLDER)
        .addModifiers(KModifier.DATA)
        // The two following function calls generate a merged primary constructor parameter & property.
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("name", String::class)
                .build()
        )
        .addProperty(
            PropertySpec.builder("name", String::class)
                .initializer("name")
                .build()
        )
        .build()
    return propertyAnnotationHolderType
  }

  private fun buildPrimaryConstructorForOuterClass(): FunSpec {
    val initCodeBlock = buildString {
      for (classAnnotationHolder in index) {
        append("index.add(ClassAnnotationHolder(\"${classAnnotationHolder.name}\", mutableListOf()).apply {\n")
        for (propertyAnnotationHolder in classAnnotationHolder.list) {
          append("list.add(PropertyAnnotationHolder(\"${propertyAnnotationHolder.name}\"))\n")
        }
        append("})\n")
      }

    }
    return FunSpec.constructorBuilder()
        .addStatement(initCodeBlock)
        .build()
  }

  fun writeToFile(kaptKotlinGeneratedDir: String) {
    FileSpec.builder(PACKAGE_NAME, CLASS_NAME)
        .addType(
            build()
        )
        .build()
        .writeTo(
            File(kaptKotlinGeneratedDir)
        )
  }

}