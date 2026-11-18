package com.minhaloja.app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ProdutoService {
    @GET("products")
    suspend fun listarProdutos(): List<Produto>
}
object ApiCliente {
    val service: ProdutoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProdutoService::class.java)
    }
}
