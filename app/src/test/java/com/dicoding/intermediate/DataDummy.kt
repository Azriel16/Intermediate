package com.dicoding.intermediate

import com.dicoding.intermediate.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStory(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = "id_$i",
                name = "author $i",
                description = "description $i",
                photoUrl = "photoUrl $i",
                createdAt = "createdAt $i",
                lat = i.toDouble(),
                lon = i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}
