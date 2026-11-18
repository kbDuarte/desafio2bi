package com.minhaloja.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class LoginActivity : AppCompatActivity() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser != null) { abrirProdutos(); return }
        setContentView(R.layout.activity_login)
        findViewById<Button>(R.id.entrar).setOnClickListener { autenticar(false) }
        findViewById<Button>(R.id.cadastrar).setOnClickListener { autenticar(true) }
    }
    private fun autenticar(cadastro: Boolean) {
        val email = findViewById<EditText>(R.id.email).text.toString().trim()
        val senha = findViewById<EditText>(R.id.senha).text.toString()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            aviso("Informe um e-mail válido."); return
        }
        if (senha.length < 6) { aviso("A senha precisa ter pelo menos 6 caracteres."); return }
        val progresso = findViewById<ProgressBar>(R.id.progresso)
        val entrar = findViewById<Button>(R.id.entrar)
        val criar = findViewById<Button>(R.id.cadastrar)
        progresso.visibility = View.VISIBLE
        entrar.isEnabled = false
        criar.isEnabled = false
        val tarefa = if (cadastro) auth.createUserWithEmailAndPassword(email, senha)
                     else auth.signInWithEmailAndPassword(email, senha)
        tarefa.addOnCompleteListener { resultado ->
            progresso.visibility = View.GONE
            entrar.isEnabled = true
            criar.isEnabled = true
            if (resultado.isSuccessful) abrirProdutos()
            else {
                val mensagem = when (resultado.exception) {
                    is FirebaseAuthUserCollisionException -> "Este e-mail já está cadastrado."
                    is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha inválidos."
                    else -> resultado.exception?.localizedMessage ?: "Não foi possível autenticar."
                }
                aviso(mensagem)
            }
        }
    }
    private fun aviso(texto: String) = Toast.makeText(this, texto, Toast.LENGTH_LONG).show()
    private fun abrirProdutos() {
        startActivity(Intent(this, ProdutosActivity::class.java))
        finish()
    }
}
