package com.example.githubdemo.data.model

import com.google.gson.annotations.SerializedName

data class Topic(
    val name: String,
    @SerializedName("display_name")
    val displayName: String?,
    val description: String?,
    @SerializedName("short_description")
    val shortDescription: String?,
    @SerializedName("created_by")
    val createdBy: String?,
    @SerializedName("released")
    val released: String?,
    @SerializedName("featured")
    val featured: Boolean
)

data class TopicsResponse(
    val items: List<Topic>
) 