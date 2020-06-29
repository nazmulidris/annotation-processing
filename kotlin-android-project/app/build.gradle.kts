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
  id("com.android.application")
  kotlin("android")
  kotlin("android.extensions")
  kotlin("kapt")
}

android {
  compileSdkVersion(29)
  defaultConfig {
    applicationId = BuildConfig.id
    minSdkVersion(BuildConfig.minSdk)
    targetSdkVersion(BuildConfig.targetSdk)
    versionCode = BuildConfig.code
    versionName = BuildConfig.version
    testInstrumentationRunner = BuildConfig.testInstrumentationRunner
  }
  lintOptions {
    isAbortOnError = true
    isIgnoreWarnings = false
    isQuiet = false
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  // Our annotation processor output (generated source code).
  sourceSets {
    getByName("main").java.srcDir("${buildDir.absolutePath}/generated/source/kotlin")
  }
}

dependencies {
  // Kotlin.
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")

  // Support Libraries.
  implementation("androidx.appcompat:appcompat:1.1.0")
  implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta7")
  implementation("androidx.recyclerview:recyclerview:1.1.0")

  // Testing Dependencies.
  testImplementation("junit:junit:4.13")
  androidTestImplementation("androidx.test.ext:junit:1.1.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

  // Add dependency on our custom annotations.
  implementation(project(":annotations"))

  // Add dependency on our custom annotation processor.
  kapt(project(":processor"))
}
