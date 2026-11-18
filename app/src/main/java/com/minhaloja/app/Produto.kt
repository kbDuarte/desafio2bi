package com.minhaloja.app

data class Produto(
    val id: Int,
    val title: String,
    val price: Double,
    val category: String
)
data class Favorito(
    val id: Int = 0,
    val titulo: String = "",
    val preco: Double = 0.0,
    val categoria: String = ""
)
