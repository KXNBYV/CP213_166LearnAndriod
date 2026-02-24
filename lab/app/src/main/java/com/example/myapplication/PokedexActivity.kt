package com.example.myapplication

import PokemonEntry
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


class PokedexActivity : ComponentActivity() {
    private val viewModel: PokemonViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ListScreen(viewModel)
        }
    }
}

@Composable
fun ListScreen(viewModel: PokemonViewModel) {

    LaunchedEffect(Unit) {
        viewModel.fetchPokemon()
    }

    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PokedexRedBody)
    ) {
        // 🔴 Header (ไม่มี Box ครอบ)
        PokedexHeader(
            query = query,
            onQueryChange = { query = it }
        )

        // 🟦 Screen
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            PokedexScreen(query, viewModel)
        }
    }

}

// 🎨 Pokédex color palette
val PokedexRedBody   = Color(0xFFB71C23) // ตัวเครื่อง
val PokedexRedHeader = Color(0xFF94161D) // ฝาบน
val PokedexRedInset  = Color(0xFFBE1F25) // ช่องค้นหา

@Composable
fun PokedexHeader(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(PokedexRedHeader)
            .padding(
                start = 15.dp,
                end = 15.dp,
                top = 15.dp,
                bottom = 15.dp
            )
    ) {

        // 🔼 แถวบน: เลนส์ + ไฟ + search (บรรทัดเดียว)
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🔵 วงกลมฟ้า (สูงเท่า header)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .background(Color(0xFFCFE7FF), CircleShape)
                    .border(4.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.75f)
                        .aspectRatio(1f)
                        .background(Color(0xFF3479B7), CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ขวา: 🔴🟡🟢 (บน) + 🔍 (ล่าง)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // 🔴🟡🟢
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    LightDot(Color(0xFFFF0000))
                    LightDot(Color(0xFFFFB500))
                    LightDot(Color(0xFF00FF09))
                }

                // 🔍 ช่องค้นหา
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(46.dp)
                        .background(PokedexRedInset, RoundedCornerShape(23.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        placeholder = {
                            Text(
                                "Fill pokemon name or ID",
                                color = Color(0xFF2D2A2A),
                                fontSize = 14.sp,
                                lineHeight = 16.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Start
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }

        // 🔽 แถวล่าง (แบ่งส่วนหัว/จอ)
        Spacer(modifier = Modifier.height(16.dp))
    }
}



@Composable
fun PokedexScreen(query: String, viewModel: PokemonViewModel) {

    val pokemonList by viewModel.pokemonList.collectAsState()


    val filteredPokemon = remember(query, pokemonList) {
        pokemonList.filter {
            it.pokemon_species.name.contains(query, true) ||
                    it.entry_number.toString().contains(query)
        }
    }

    // กรอบนอก
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(Color(0xFFA9A9A9), RoundedCornerShape(32.dp))
            .padding(8.dp)
    ) {
        // กรอบใน (จอ)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.7.dp,              // 👈 ขอบดำบาง ๆ
                    color = Color.Black,
                    shape = RoundedCornerShape(26.dp)
                )
                .background(Color.White, RoundedCornerShape(26.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            LazyColumn {
                items(filteredPokemon) { pokemon ->
                    PokemonRow(pokemon)
                    Divider(color = Color(0xFFC4C4C4))
                }
            }
        }
    }
}

@Composable
fun PokemonRow(pokemon: PokemonEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 18.dp), // ⬅️ คุมจำนวนแถว
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "#${pokemon.entry_number}",
            color = Color.DarkGray,
            fontSize = 18.sp,         // ⬅️ เพิ่ม
            modifier = Modifier.width(44.dp)
        )

        Text(
            pokemon.pokemon_species.name.lowercase(),
            fontSize = 20.sp,         // ⬅️ เพิ่ม (หลัก)
            modifier = Modifier.weight(1f)
        )

        AsyncImage(
            model = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-iii/firered-leafgreen/${pokemon.entry_number}.png",
            contentDescription = pokemon.pokemon_species.name,
            modifier = Modifier.size(40.dp)
        )
    }
}


@Composable
fun PokedexSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = {
            Text("Search by name or ID")
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF2F2F2),
            unfocusedContainerColor = Color(0xFFF2F2F2),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun LightDot(color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .border(1.dp, Color.White, CircleShape)
            .background(color, CircleShape)
    )
}

// Tips: for image : https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-iii/firered-leafgreen/1.png