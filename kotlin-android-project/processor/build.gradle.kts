import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

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

plugins {
  id("java-library")
  id("kotlin")
  kotlin("kapt")
}
dependencies {
  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")

  // Add dependency on our custom annotations.
  implementation(project(":annotations"))

  // Add dependency for KotlinPoet to generate source code.
  implementation("com.squareup:kotlinpoet:1.6.0")

  // Add dependency for AutoService to dynamically generate the required `javax.annotation.processing.Processor` file.
  implementation("com.google.auto.service:auto-service:1.0-rc7")
  kapt("com.google.auto.service:auto-service:1.0-rc7")
}
