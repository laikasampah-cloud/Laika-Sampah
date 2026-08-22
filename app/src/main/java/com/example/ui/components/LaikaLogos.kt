package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.*

@Composable
fun Laika3DLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 5))
            .background(Color.White)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.logo_laika_3d)
                .crossfade(true)
                .build(),
            contentDescription = "Logo Laika Sampah 3D",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LaikaHouseLogo(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.logo_laika_house)
                .crossfade(true)
                .build(),
            contentDescription = "Logo Laika Sampah TPS 3R House",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun PtValeLogo(
    modifier: Modifier = Modifier,
    height: Dp = 50.dp
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier.height(height),
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.logo_pt_vale)
                    .crossfade(true)
                    .build(),
                contentDescription = "Logo PT Vale Indonesia Tbk",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxHeight()
            )
        }
    }
}

/**
 * Prominent Enlarged Dashboard Header Banner featuring the two requested logos:
 * 1. LAIKA SAMPAH TPS 3R House Logo (Enlarged)
 * 2. PT VALE Indonesia Tbk Logo (Enlarged)
 */
@Composable
fun DashboardBrandHeroBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Enlarged Dual Logos Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Enlarged LAIKA SAMPAH House Logo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LaikaHouseLogo(size = 110.dp)
                    Text(
                        text = "LAIKA SAMPAH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = ForestGreenPrimary
                    )
                }

                // Center Divider with Eco Icon
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LightMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("♻️", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TPS 3R",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                }

                // Right: Enlarged PT VALE Indonesia Tbk Logo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PtValeLogo(height = 65.dp)
                    Text(
                        text = "PT VALE INDONESIA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007E8A)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE8ECE9), thickness = 1.dp)

            // Slogan & Location Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MEMILAH • MENGUMPUL • MENYETOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = ForestGreenPrimary,
                    letterSpacing = 0.5.sp
                )
                Surface(
                    color = LightMint,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "IGP POMALAA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TripleLogoBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo 1: 3D Badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Laika3DLogo(size = 76.dp)
                    Text("LAIKA 3D", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                }

                // Logo 2: TPS 3R House Emblem (Enlarged)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LaikaHouseLogo(size = 85.dp)
                    Text("TPS 3R VALE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                }

                // Logo 3: PT Vale Indonesia (Enlarged)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PtValeLogo(height = 55.dp)
                    Text("PT VALE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF007E8A))
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MEMILAH, MENGUMPUL, MENYETOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = ForestGreenPrimary,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
