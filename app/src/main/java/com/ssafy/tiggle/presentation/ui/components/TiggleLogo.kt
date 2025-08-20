package com.ssafy.tiggle.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.tiggle.R

/**
 * 공통 로고 컴포넌트
 */
@Composable
fun TiggleLogo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Tiggle 로고",
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TiggleLogoPreview() {
    TiggleLogo()
}

@Preview(showBackground = true, widthDp = 200)
@Composable
private fun TiggleLogoSmallPreview() {
    TiggleLogo(
        modifier = Modifier.width(200.dp)
    )
}
