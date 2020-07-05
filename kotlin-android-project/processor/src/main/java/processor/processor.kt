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

package processor

import annotations.AdapterModel
import annotations.ViewHolderBinding
import codegen.AdapterCodeGeneratorBuilder
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.*
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class Processor : AbstractProcessor() {
  private val MyClassAnnotationClass = AdapterModel::class.java
  private val MyPropertyAnnotationClass = ViewHolderBinding::class.java
  private lateinit var messager: Messager

  override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(MyClassAnnotationClass.canonicalName)

  override fun init(processingEnv: ProcessingEnvironment) {
    super.init(processingEnv)
    messager = processingEnv.messager
  }

  private fun printNoteMessage(msg: String) {
    messager.printMessage(Diagnostic.Kind.NOTE, "\t$msg\r\r")
  }

  private fun printErrorMessage(msg: String) {
    messager.printMessage(Diagnostic.Kind.ERROR, "\t$msg\r\r")
  }

  override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
    printNoteMessage("🚀 Processor start")
    try {
      val kaptKotlinGeneratedDir: String = processingEnv.options["kapt.kotlin.generated"] ?: return false
      processAnnotations(kaptKotlinGeneratedDir, roundEnv)
    }
    catch (err: Throwable) {
      val sw = StringWriter()
      val pw = PrintWriter(sw)
      err.printStackTrace(pw)
      printErrorMessage("☠️ Processor failed w/ this error: $sw")
      return false
    }
    printNoteMessage("🛑 Processor stop")
    return true;
  }

  private fun processAnnotations(kaptKotlinGeneratedDir: String, roundEnv: RoundEnvironment) {
    val classElements: MutableSet<out Element> = roundEnv.getElementsAnnotatedWith(MyClassAnnotationClass)
    return classElements.forEach { classElement ->
      val metadata: AdapterModelAnnotationMetadata = processClassElements(classElement)
      generateSourceFiles(kaptKotlinGeneratedDir, metadata)
    }
  }

  /**
   * These are top (class) level elements; see [annotations.AdapterModel]. They may contain enclosed elements
   * that are annotated properties; see [annotations.ViewHolderBinding].
   */
  private fun processClassElements(classElement: Element): AdapterModelAnnotationMetadata {
    val packageName: String = processingEnv.elementUtils.getPackageOf(classElement).toString()
    val modelName: String = classElement.simpleName.toString()
    val layoutId: Int = classElement.getAnnotation(MyClassAnnotationClass).rowRendererLayoutId
    val viewHolderBindingData: List<ViewHolderBindingAnnotationMetadata> = processEnclosedPropertyElements(classElement)
    return AdapterModelAnnotationMetadata(packageName, modelName, layoutId, viewHolderBindingData)
  }

  /**
   * These property elements ([annotations.ViewHolderBinding]) are enclosed in a class level element
   * ([annotations.AdapterModel]).
   */
  private fun processEnclosedPropertyElements(classElement: Element): List<ViewHolderBindingAnnotationMetadata> =
      classElement.enclosedElements.mapNotNull { enclosedElement ->
        val viewHolderBinding: ViewHolderBinding? = enclosedElement.getAnnotation(MyPropertyAnnotationClass)
        if (viewHolderBinding == null) {
          null
        }
        else {
          val elementName: String = enclosedElement.simpleName.toString()
          printNoteMessage(elementName)
          // Note: fieldNames look like "name$annotations" or "address$annotations".
          val fieldName: String = elementName.substring(0, elementName.indexOf("$"))
          ViewHolderBindingAnnotationMetadata(fieldName, viewHolderBinding.textViewId)
        }
      }

  private fun generateSourceFiles(kaptKotlinGeneratedDir: String, metadata: AdapterModelAnnotationMetadata) =
      FileSpec.builder(metadata.packageName, metadata.generatedAdapterClassName)
          .addType(
              AdapterCodeGeneratorBuilder(metadata).build()
          )
          .build()
          .writeTo(
              File(kaptKotlinGeneratedDir)
          )

}