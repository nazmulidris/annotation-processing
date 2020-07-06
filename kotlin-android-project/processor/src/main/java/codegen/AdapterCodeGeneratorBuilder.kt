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
import processor.ViewHolderBindingAnnotationMetadata

/**
 * Generates a RecyclerView adapter for every model class that's annotated w/ [annotations.AdapterModel]. The implicit contract
 * is that a List of items (of the type of the model class) must be passed to the constructor of this generated adapter.
 *
 * For example, for a model class `PersonModel`, a `PersonModelAdapter` class is generated. You can reference this class
 * directly in your code, or you can get to it reflectively via [AdapterUtils.createBindingForModel].
 *
 * [More info on KotlinPoet](https://github.com/square/kotlinpoet).
 */
class AdapterCodeGeneratorBuilder(metadata: AdapterModelAnnotationMetadata) {
  private val adapterModelClassMetadata: TopLevelClassMetadata = TopLevelClassMetadata(metadata.copy())
  private val viewHolderClassMetadata: NestedClassMetadata = NestedClassMetadata(metadata.copy())
  private val textViewClassName = ClassName("android.widget", "TextView")

  /**
   * Extract all the necessary data from [metadata] and save it in this class for generating code for the top level /
   * enclosing class.
   */
  data class TopLevelClassMetadata(
      val metadata: AdapterModelAnnotationMetadata,
      val generatedAdapterClassName: ClassName = ClassName(metadata.packageName, metadata.generatedAdapterClassName),
      val className: ClassName = ClassName(metadata.packageName, metadata.modelClassName),
      val propertyClassName: ParameterizedTypeName = ClassName("kotlin.collections", "List").parameterizedBy(className),
      val viewHolderBindingAnnotations: List<ViewHolderBindingAnnotationMetadata> = metadata.viewHolderBindingAnnotations,
      val rowRendererLayoutId: Int = metadata.rowRendererLayoutId
  )

  /**
   * Extract all the necessary data from [metadata] and save it in this class for generating code for the nested class.
   */
  data class NestedClassMetadata(
      val metadata: AdapterModelAnnotationMetadata,
      val name: String = "ViewHolder",
      val className: ClassName = ClassName(metadata.packageName, name),
      val qualifiedClassName: ClassName = ClassName(metadata.packageName, metadata.generatedAdapterClassName + ".$name")
  )

  fun build(): TypeSpec = TypeSpec.classBuilder(adapterModelClassMetadata.generatedAdapterClassName)
      .addModifiers(KModifier.DATA)
      // Merge the primary constructor parameter and property (below).
      .primaryConstructor(
          FunSpec.constructorBuilder()
              .addParameter("items", adapterModelClassMetadata.propertyClassName)
              .build()
      )
      .addProperty(
          PropertySpec.builder("items", adapterModelClassMetadata.propertyClassName)
              .initializer("items")
              .addModifiers(KModifier.PRIVATE)
              .build()
      )
      .superclass(
          ClassName("androidx.recyclerview.widget.RecyclerView", "Adapter")
              .parameterizedBy(viewHolderClassMetadata.qualifiedClassName)
      )
      // Base method implementations.
      .addFunction(
          FunSpec.builder("getItemCount")
              .addModifiers(KModifier.OVERRIDE)
              .returns(INT)
              .addStatement("return items.size")
              .build()
      )
      .addFunction(
          FunSpec.builder("onCreateViewHolder")
              .addModifiers(KModifier.OVERRIDE)
              .addParameter("parent", ClassName("android.view", "ViewGroup"))
              .addParameter("viewType", INT)
              .returns(viewHolderClassMetadata.qualifiedClassName)
              .addStatement("val view = android.view.LayoutInflater.from(parent.context).inflate(%L, parent, false)",
                            adapterModelClassMetadata.rowRendererLayoutId)
              .addStatement("return ${viewHolderClassMetadata.name}(view)")
              .build()
      )
      .addFunction(
          FunSpec.builder("onBindViewHolder")
              .addModifiers(KModifier.OVERRIDE)
              .addParameter("viewHolder", viewHolderClassMetadata.qualifiedClassName)
              .addParameter("position", INT)
              .addStatement("viewHolder.bind(items[position])")
              .build()
      )
      .addType(buildNestedClass())
      .build()

  private fun buildNestedClass(): TypeSpec = TypeSpec.classBuilder(viewHolderClassMetadata.className)
      .primaryConstructor(
          FunSpec.constructorBuilder()
              .addParameter("itemView", ClassName("android.view", "View"))
              .build()
      )
      .superclass(
          ClassName("androidx.recyclerview.widget.RecyclerView", "ViewHolder")
      )
      .addSuperclassConstructorParameter("itemView")
      .addFunction(
          FunSpec.builder("bind")
              .addParameter("item", adapterModelClassMetadata.className)
              .apply {
                adapterModelClassMetadata.viewHolderBindingAnnotations.forEach {
                  addStatement("itemView.findViewById<%T>(%L).text = item.%L",
                               textViewClassName, it.textViewId, it.fieldName)
                }
              }
              .build()
      )
      .build()

}