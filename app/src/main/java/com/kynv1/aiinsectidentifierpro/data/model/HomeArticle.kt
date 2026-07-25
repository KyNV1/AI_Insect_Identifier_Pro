package com.kynv1.aiinsectidentifierpro.data.model

data class HomeArticle(
    val id: Long,
    val commonName: String,
    val scientificName: String,
    val imageResId: Int,
    val category: String
)
