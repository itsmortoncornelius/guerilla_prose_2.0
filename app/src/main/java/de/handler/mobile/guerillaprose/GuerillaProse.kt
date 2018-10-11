package de.handler.mobile.guerillaprose

import java.util.Date

data class GuerillaProse(
        val id: String,
        val text: String?,
        val imageUrl: String?,
        val label: String,
        val userId: String,
        val date: Date
)
