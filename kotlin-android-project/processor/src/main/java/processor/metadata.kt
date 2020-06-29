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

/**
 *  Contains all the information required to generate an adapter from a class level annotation
 * [annotations.AdapterModel].
 */
data class AdapterModelAnnotationMetadata(
    val packageName: String,
    val modelClassName: String,
    /** From [annotations.AdapterModel]. */
    val rowRendererLayoutId: Int,
    val viewHolderBindingAnnotations: List<ViewHolderBindingAnnotationMetadata>
) {
  val generatedAdapterClassName: String = "${modelClassName}Adapter"
}

/** Store information extracted from the property level annotation [annotations.ViewHolderBinding]. */
data class ViewHolderBindingAnnotationMetadata(
    val fieldName: String,
    val textViewId: Int
)