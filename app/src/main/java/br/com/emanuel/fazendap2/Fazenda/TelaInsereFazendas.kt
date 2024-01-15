package br.com.emanuel.fazendap2.Fazenda

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import br.com.emanuel.fazendap2.model.Fazenda
import com.google.firebase.database.FirebaseDatabase

class TelaInsereFazendas : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InsereFazendas()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsereFazendas() {
    var contexto = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val fazendasRef = database.getReference("fazendas")
    var codigo by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var valorDaPropriedade by remember { mutableStateOf("") }
    var qtdeFuncionarios by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Insira os seguintes dados:",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Código") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
                val fazenda = Fazenda(codigo, nome, valorDaPropriedade.toDouble(), qtdeFuncionarios.toInt())
                fazendasRef.child(codigo).setValue(fazenda)
                // Limpar os campos de texto
                codigo = ""
                nome = ""
                valorDaPropriedade = ""
                qtdeFuncionarios = ""
                Toast.makeText(contexto,"Fazenda inserida com sucesso!", Toast.LENGTH_LONG).show()
            },
            enabled = codigo.isNotEmpty() && nome.isNotEmpty() && valorDaPropriedade.isNotEmpty() && qtdeFuncionarios.isNotEmpty(),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Enviar")
        }

        Button(
            onClick = {contexto.startActivity(Intent(contexto, TelaFazendas::class.java))},//onVoltarClick()
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Voltar")
        }
    }
}