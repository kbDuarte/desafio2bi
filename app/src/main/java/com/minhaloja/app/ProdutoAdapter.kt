package com.minhaloja.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import java.util.Locale

class ProdutoAdapter(
    private val remover: Boolean,
    private val aoClicar: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.Holder>() {
    private val itens = mutableListOf<Produto>()
    fun atualizar(novos: List<Produto>) {
        itens.clear()
        itens.addAll(novos)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_produto, parent, false))
    override fun getItemCount() = itens.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val produto = itens[position]
        holder.titulo.text = produto.title
        holder.preco.text = String.format(Locale.US, "US$ %.2f", produto.price)
        holder.categoria.text = produto.category
        holder.imagem.load(produto.image.takeIf { it.isNotBlank() }) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }
        holder.acao.text = if (remover) "Remover favorito" else "Salvar favorito"
        holder.acao.setOnClickListener { aoClicar(produto) }
        holder.itemView.setOnClickListener { aoClicar(produto) }
    }
    class Holder(item: View) : RecyclerView.ViewHolder(item) {
        val titulo: TextView = item.findViewById(R.id.titulo)
        val preco: TextView = item.findViewById(R.id.preco)
        val categoria: TextView = item.findViewById(R.id.categoria)
        val imagem: ImageView = item.findViewById(R.id.imagem)
        val acao: Button = item.findViewById(R.id.acao)
    }
}
