package br.com.emanuel.fazendap2.Fazenda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.emanuel.fazendap2.model.Fazenda
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class TelaMostrarFazendas : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MostrarFazendas()
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MostrarFazendas() {
    val listState: LazyListState = rememberLazyListState()
    val contexto: Context = LocalContext.current
    val listaFazendas: MutableList<Fazenda> = remember { mutableStateListOf() }
    var mediaPropriedades: Double by remember { mutableStateOf(0.0) }

    // Referência ao nó "fazendas" no Firebase Realtime Database
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val fazendasRef: DatabaseReference = database.reference.child("fazendas")

    // Função para calcular a média das propriedades
    fun calcularMediaPropriedades(fazendas: List<Fazenda>) {
        if (fazendas.isNotEmpty()) {
            val soma = fazendas.sumOf { it.valorDaPropriedade }
            mediaPropriedades = soma / fazendas.size
        } else {
            mediaPropriedades = 0.0
        }
    }
    calcularMediaPropriedades(listaFazendas)

    // Função para carregar as fazendas do Firebase Realtime Database
    fun carregarFazendas() {
        fazendasRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fazendas: MutableList<Fazenda> = mutableListOf()

                for (fazendaSnapshot in snapshot.children) {
                    val codigo = fazendaSnapshot.child("codigo").value.toString()
                    val nome = fazendaSnapshot.child("nome").value.toString()
                    val valorDaPropriedade = fazendaSnapshot.child("valorDaPropriedade").value.toString().toDouble()
                    val qtdeFuncionarios = fazendaSnapshot.child("qtdeFuncionarios").value.toString().toInt()

                    val fazenda = Fazenda(codigo, nome, valorDaPropriedade, qtdeFuncionarios)
                    fazendas.add(fazenda)
                }
                listaFazendas.clear()
                listaFazendas.addAll(fazendas)
            }

            override fun onCancelled(error: DatabaseError) {
                // Tratamento de erro, se necessário
            }
        })
    }

    // Carregar as fazendas ao entrar na tela
    LaunchedEffect(Unit) {
        carregarFazendas()
    }

    Column(Modifier.padding(40.dp)) {
        Text(
            text = "Tela de Apresentação de fazendas",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Média dos valores das propriedades: $mediaPropriedades",
            modifier = Modifier.padding(vertical = 8.dp)
        )



        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(listaFazendas) { index, fazenda ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    onClick = { /* Ação ao clicar no card */ }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Código: ${fazenda.codigo}")
                        Text(text = "Nome: ${fazenda.nome}")
                        Text(text = "Valor da Propriedade: R$ ${fazenda.valorDaPropriedade}")
                        Text(text = "Quantidade de Funcionários: ${fazenda.qtdeFuncionarios}")

                        MenuTresPontosOpcoes(fazenda)
                    }
                }
            }
        }
        Button(
            onClick = { salvarFazendasEmArquivo(listaFazendas, contexto) },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Salvar Fazendas")
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTresPontosOpcoes(fazenda: Fazenda) {
    val contexto: Context = LocalContext.current
    val database =FirebaseDatabase.getInstance()
    val fazendasRef = database.getReference("fazendas")
    var isOpened: Boolean by remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { isOpened = !isOpened }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More vert"
            )
        }
        DropdownMenu(expanded = isOpened, onDismissRequest = { isOpened = false }) {
            DropdownMenuItem(text = { Text(text = "Editar") }, onClick = {
                val intent = Intent(contexto, TelaEditarFazendas::class.java)
                intent.putExtra("fazenda", fazenda)
                contexto.startActivity(intent)
                isOpened = !isOpened
            })
            DropdownMenuItem(text = { Text(text = "Excluir") }, onClick = {
                // ExibirDialogExclusaoFazenda(contexto, fazenda, fazendasRef)
                showDialog.value = true
                isOpened = !isOpened
            })
        }
    }
    if (showDialog.value) {
        ExibirDialogExclusaoFazenda(contexto, fazenda, fazendasRef)
    }
}

@Composable
fun ExibirDialogExclusaoFazenda(
    contexto: Context,
    fazenda: Fazenda,
    fazendasRef: DatabaseReference
) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Excluir fazenda") },
            text = { Text("Tem certeza de que deseja excluir esta fazenda?") },
            confirmButton = {
                Button(
                    onClick = {
                        fazendasRef.child(fazenda.codigo).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    snapshot.ref.removeValue()
                                    Toast.makeText(
                                        contexto,
                                        "Fazenda excluída com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        contexto,
                                        "Fazenda não encontrada.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    contexto,
                                    "Erro ao excluir a fazenda.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })

                        showDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
fun salvarFazendasEmArquivo(fazendas: List<Fazenda>, contexto: Context) {
    val arquivo = File(contexto.filesDir, "fazendas.txt")
    val textoFazendas = fazendas.joinToString(separator = "\n") { fazenda ->
        "${fazenda.codigo}, ${fazenda.nome}, ${fazenda.valorDaPropriedade}, ${fazenda.qtdeFuncionarios}"
    }

    // Executar em uma coroutine para não bloquear a thread principal
    CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.IO) {
            arquivo.writeText(textoFazendas)
        }

        withContext(Dispatchers.Main) {
            Toast.makeText(contexto, "Fazendas salvas no arquivo fazendas.txt", Toast.LENGTH_SHORT).show()
        }
    }
}


