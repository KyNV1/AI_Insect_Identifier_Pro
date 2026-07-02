package com.kynv1.aiinsectidentifierpro.data.model

data class InsectShort(
    val id: Long,
    val commonName: String,
    val scientificName: String,
    val imageResId: Int,
    val category: String
)
