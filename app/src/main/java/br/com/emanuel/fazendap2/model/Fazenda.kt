package br.com.emanuel.fazendap2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Declaração da classe Fazenda com a anotação @Parcelize para torná-la parcelável
@Parcelize
data class Fazenda(val codigo: String, val nome: String, val valorDaPropriedade: Double, val qtdeFuncionarios: Int) : Parcelable
