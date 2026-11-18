package com.minhaloja.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProdutosActivity : AppCompatActivity() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val adapter = ProdutoAdapter(false) { salvar(it) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser == null) { voltarLogin(); return }
        setContentView(R.layout.activity_lista)
        findViewById<RecyclerView>(R.id.lista).apply {
            layoutManager = LinearLayoutManager(this@ProdutosActivity)
            adapter = this@ProdutosActivity.adapter
        }
        findViewById<Button>(R.id.favoritos).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        findViewById<Button>(R.id.sair).setOnClickListener {
            auth.signOut()
            voltarLogin()
        }
        carregar()
    }
    private fun carregar() {
        val progresso = findViewById<ProgressBar>(R.id.progresso)
        val mensagem = findViewById<TextView>(R.id.mensagem)
        progresso.visibility = View.VISIBLE
        mensagem.text = "Carregando produtos..."
        lifecycleScope.launch {
            try {
                val produtos = ApiCliente.service.listarProdutos()
                adapter.atualizar(produtos)
                mensagem.text = if (produtos.isEmpty()) "Nenhum produto encontrado." else ""
            } catch (erro: Exception) {
                mensagem.text = "Erro ao carregar produtos: ${erro.localizedMessage}"
            } finally {
                progresso.visibility = View.GONE
            }
        }
    }
    private fun salvar(produto: Produto) {
        val uid = auth.currentUser?.uid ?: run { voltarLogin(); return }
        val dados = hashMapOf(
            "id" to produto.id,
            "titulo" to produto.title,
            "preco" to produto.price,
            "categoria" to produto.category,
            "salvoEm" to FieldValue.serverTimestamp()
        )
        db.collection("usuarios").document(uid).collection("favoritos")
            .document(produto.id.toString()).set(dados)
            .addOnSuccessListener { aviso("Produto salvo nos favoritos!") }
            .addOnFailureListener { aviso("Erro ao salvar: ${it.localizedMessage}") }
    }
    private fun aviso(texto: String) = Toast.makeText(this, texto, Toast.LENGTH_LONG).show()
    private fun voltarLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
