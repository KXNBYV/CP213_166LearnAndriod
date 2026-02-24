package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Gray)
                    .padding(32.dp))
            {
                //hp
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(color = Color.White))
                {
                        Text(
                            text = "hp",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterStart)
                                .fillMaxWidth(fraction = 0.5f)
                                .background(color = Color.Red)
                                .padding(8.dp)
                        )
                }

                //image
                Image(
                    painter = painterResource(id = R.drawable.kan),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(600.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                        .clickable {
                            startActivity(Intent(this@MainActivity , PokedexActivity::class.java))
                        }
                )

                //status
                var str by remember { mutableStateOf(10) }
                var def by remember { mutableStateOf(10) }
                var int by remember { mutableStateOf(10) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Column {
                        Button(onClick = {
                            str = str + 1
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_up_24),
                                contentDescription = "arrow_up",
                                modifier = Modifier)
                        }
                        Text(text = "Str", fontSize = 32.sp)
                        Text(text = str.toString(), fontSize = 32.sp)
                        Button(onClick = {
                            str = str - 1
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                                contentDescription = "arrow_down",
                                modifier = Modifier)
                        }
                    }
                    Column {
                        Button(onClick = {
                            def = def + 1
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_up_24),
                                contentDescription = "arrow_up",
                                modifier = Modifier)
                        }
                        Text(text = "Def", fontSize = 32.sp)
                        Text(text = def.toString(), fontSize = 32.sp)
                        Button(onClick = {
                            def = def - 1
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                                contentDescription = "arrow_down",
                                modifier = Modifier)
                        }
                    }
                    Column {
                        Button(onClick = {
                            int = int + 1
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_up_24),
                                contentDescription = "arrow_up",
                                modifier = Modifier)
                        }
                        Text(text = "Int", fontSize = 32.sp)
                        Text(text = int.toString(), fontSize = 32.sp)
                        Button(onClick = {
                            int = int - 1
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                                contentDescription = "arrow_down",
                                modifier = Modifier)
                        }
                    }
                }




            }
        }
    }
}
