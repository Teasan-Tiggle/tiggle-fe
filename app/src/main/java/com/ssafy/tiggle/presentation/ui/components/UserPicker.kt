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
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ssafy.tiggle.R
import com.ssafy.tiggle.domain.entity.dutchpay.UserSummary
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlueLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText
import com.ssafy.tiggle.presentation.ui.theme.TiggleSkyBlue

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
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아바타 (둥근 사각형)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = if (isSelected) TiggleBlue else TiggleGrayLight
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.firstOrNull()?.toString() ?: "?",
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = FontWeight.Bold,
                style = AppTypography.titleMedium
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = user.name,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                style = AppTypography.bodyLarge
            )
            
            // 학교와 학과 정보
            if (user.university != null || user.department != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = buildString {
                        if (user.university != null) {
                            append(user.university)
                        }
                        if (user.university != null && user.department != null) {
                            append(" • ")
                        }
                        if (user.department != null) {
                            append(user.department)
                        }
                    },
                    color = TiggleGrayText,
                    style = AppTypography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 선택 상태 표시 (TiggleSkyBlue 원형 버튼)
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    color = if (isSelected) TiggleBlueLight else Color.White
                )
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "selected",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                // 선택되지 않은 상태의 테두리 (동일한 크기 유지)
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            color = TiggleGrayText.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
            }
        }
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
        Text(text = name, style = AppTypography.bodySmall)
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


