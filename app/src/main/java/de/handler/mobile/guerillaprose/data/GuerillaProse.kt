package de.handler.mobile.guerillaprose.data

data class GuerillaProse(
        val id: String = "0",
        val text: String?,
        val imageUrl: String?,
        val label: String,
        val userId: String,
        val date: Long? = null
)
