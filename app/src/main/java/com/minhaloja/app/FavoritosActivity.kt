package com.minhaloja.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FavoritosActivity : AppCompatActivity() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val adapter = ProdutoAdapter(true) { remover(it) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)
        findViewById<Button>(R.id.voltar).setOnClickListener { finish() }
        findViewById<RecyclerView>(R.id.lista).apply {
            layoutManager = LinearLayoutManager(this@FavoritosActivity)
            adapter = this@FavoritosActivity.adapter
        }
    }
    override fun onResume() {
        super.onResume()
        carregar()
    }
    private fun colecao() = db.collection("usuarios")
        .document(auth.currentUser?.uid ?: error("Usuário não autenticado"))
        .collection("favoritos")
    private fun carregar() {
        if (auth.currentUser == null) { finish(); return }
        val progresso = findViewById<ProgressBar>(R.id.progresso)
        val mensagem = findViewById<TextView>(R.id.mensagem)
        progresso.visibility = View.VISIBLE
        mensagem.text = "Carregando favoritos..."
        colecao().orderBy("salvoEm", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { resultado ->
                val produtos = resultado.documents.mapNotNull { doc ->
                    val id = (doc.getLong("id") ?: return@mapNotNull null).toInt()
                    Produto(id, doc.getString("titulo") ?: "", doc.getDouble("preco") ?: 0.0,
                        doc.getString("categoria") ?: "")
                }
                adapter.atualizar(produtos)
                mensagem.text = if (produtos.isEmpty()) "Nenhum favorito salvo." else ""
            }
            .addOnFailureListener { mensagem.text = "Erro ao ler favoritos: ${it.localizedMessage}" }
            .addOnCompleteListener { progresso.visibility = View.GONE }
    }
    private fun remover(produto: Produto) {
        colecao().document(produto.id.toString()).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Favorito removido.", Toast.LENGTH_SHORT).show()
                carregar()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao remover: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }
}
