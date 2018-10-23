package de.handler.mobile.guerillaprose

data class GuerillaProse(
        val id: String = "",
        val text: String?,
        val imageUrl: String?,
        val label: String,
        val userId: String,
        val date: Long? = null
)
