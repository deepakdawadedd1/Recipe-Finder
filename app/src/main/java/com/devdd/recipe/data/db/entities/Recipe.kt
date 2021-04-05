package com.devdd.recipe.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
     @PrimaryKey(autoGenerate = true)
     val key: Int = 0,

     @ColumnInfo(name = "id")
     val id: Int = 0,
     @ColumnInfo(name = "author_name")
     val authorName: String = "",

     @ColumnInfo(name = "description")
     val description: String = "",

     @ColumnInfo(name = "image_url")
     val imageUrl: String = "",

     )