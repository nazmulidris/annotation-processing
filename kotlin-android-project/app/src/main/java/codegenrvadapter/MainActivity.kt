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

package codegenrvadapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import codegen.AdapterUtils
import codegenrvadapter.android.autoadapter.R
import index.AdapterIndex
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    recyclerView.apply {
      layoutManager = LinearLayoutManager(this@MainActivity)
    }

    // Comment one or the other to load the desired adapter via reflection.
    bindDebugModelAdapter()
    //bindPersonModelAdapter()
  }

  private fun bindDebugModelAdapter() {
    val items: MutableList<DebugModel> = mutableListOf()
    AdapterUtils.getAdapterIndex()?.apply {
      (this as AdapterIndex).index.map { classAnnotationHolder ->
        val title: String = classAnnotationHolder.name
        val description: String = classAnnotationHolder.list.joinToString(",", "{", "}") { it.name }
        items.add(DebugModel(title, description))
      }
      val adapter = AdapterUtils.createBindingForModel(DebugModel::class.java, items)
      adapter?.apply {
        recyclerView.adapter = this as RecyclerView.Adapter<*>
      }
    }
  }

  private fun bindPersonModelAdapter() {
    val items = listOf(
        PersonModel("Jane Doe", "123 Street"),
        PersonModel("John Doe", "789 Street")
    )
    val adapter = AdapterUtils.createBindingForModel(PersonModel::class.java, items)
    adapter?.apply {
      recyclerView.adapter = this as RecyclerView.Adapter<*>
    }
  }
}