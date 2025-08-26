package com.ssafy.tiggle.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssafy.tiggle.R
import com.ssafy.tiggle.domain.entity.dutchpay.UserSummary
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

@Composable
fun UserPicker(
    users: List<UserSummary>,
    selectedUserIds: Set<Long>,
    onToggleUser: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredUsers = remember(users, query) {
        if (query.isBlank()) users else users.filter { it.name.contains(query, ignoreCase = true) }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            placeholder = { Text(text = "이름으로 검색", color = TiggleGrayText) },
            singleLine = true,
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "clear")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = TiggleGrayLight,
                focusedBorderColor = TiggleBlue
            )
        )
        // 선택된 사용자 칩 영역
        if (selectedUserIds.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                users.filter { selectedUserIds.contains(it.id) }.forEach { user ->
                    SelectedUserChip(
                        name = user.name,
                        onRemove = { onToggleUser(user.id) },
                        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // 사용자 리스트
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(filteredUsers, key = { it.id }) { user ->
                    UserRow(
                        user = user,
                        isSelected = selectedUserIds.contains(user.id),
                        onClick = { onToggleUser(user.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserRow(user: UserSummary, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아바타 (이니셜)
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (isSelected) TiggleBlue else TiggleGrayLight,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.firstOrNull()?.toString() ?: "?",
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(text = user.name, color = Color.Black)
        }

        Icon(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.check),
            contentDescription = null,
            tint = if (isSelected) TiggleBlue else TiggleGrayText
        )
    }
}

@Composable
private fun SelectedUserChip(name: String, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(TiggleGrayLight, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.size(6.dp))
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(Color.White, CircleShape)
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "remove",
                tint = TiggleGrayText,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}


