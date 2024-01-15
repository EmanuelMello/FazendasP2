package br.com.emanuel.fazendap2.Fazenda

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.emanuel.fazendap2.model.Fazenda
import com.google.firebase.database.FirebaseDatabase

class TelaEditarFazendas : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val fazenda = intent.getParcelableExtra<Fazenda>("fazenda")
            if (fazenda != null) {
                EditaFazendas(fazenda)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditaFazendas(fazenda: Fazenda) {
    var contexto = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val fazendasRef = database.getReference("fazendas")
    var codigo by remember { mutableStateOf(fazenda.codigo) }
    var nome by remember { mutableStateOf(fazenda.nome) }
    var valorDaPropriedade by remember { mutableStateOf(fazenda.valorDaPropriedade.toString()) }
    var qtdeFuncionarios by remember { mutableStateOf(fazenda.qtdeFuncionarios.toString()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Edite os seguintes dados:",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = codigo,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = valorDaPropriedade,
            onValueChange = { valorDaPropriedade = it },
            label = { Text("Valor da Propriedade") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = qtdeFuncionarios,
            onValueChange = { qtdeFuncionarios = it },
            label = { Text("Quantidade de Funcionários") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Button(
            onClick = {
                val fazendaAtualizada = Fazenda(codigo, nome, valorDaPropriedade.toDouble(), qtdeFuncionarios.toInt())
                fazendasRef.child(codigo).setValue(fazendaAtualizada)
                // Voltar para a tela de fazendas
                Toast.makeText(contexto, "Fazenda editada com sucesso!", Toast.LENGTH_LONG).show()
                contexto.startActivity(Intent(contexto, TelaFazendas::class.java))
            },
            enabled = nome.isNotEmpty() && valorDaPropriedade.isNotEmpty() && qtdeFuncionarios.isNotEmpty(),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Salvar")
        }

        Button(
            onClick = {
                // Voltar para a tela de fazendas sem fazer alterações
                contexto.startActivity(Intent(contexto, TelaFazendas::class.java))
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Cancelar")
        }
    }
}
