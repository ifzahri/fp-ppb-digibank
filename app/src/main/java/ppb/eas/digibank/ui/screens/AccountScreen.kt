package ppb.eas.digibank.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ppb.eas.digibank.R

@Composable
fun AccountScreen(
    navController: NavHostController,
    userId: Int,
    username: String,
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(3) } // Account tab selected
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                navController = navController
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                ProfileHeader(userId = userId, username = username)
            }

            item {
                SettingsSection(
                    onLogoutClick = { showLogoutDialog = true }
                )
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@Composable
private fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    if (item.isCustomIcon) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_transaction),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = item.icon!!,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp
                    )
                },
                selected = selectedTab == index,
                onClick = {
                    onTabSelected(index)
                    if (item.route.isNotEmpty()) {
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileHeader(userId: Int, username: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = username,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SettingsSection(
    onLogoutClick: () -> Unit
) {
    Column {
        Text(
            text = "Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Update your personal information",
                    onClick = { /* Handle edit profile */ }
                )

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    subtitle = "Update your account password",
                    onClick = { /* Handle change password */ }
                )

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Manage notification preferences",
                    onClick = { /* Handle notifications */ }
                )

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingsItem(
                    icon = Icons.Default.Settings,
                    title = "App Settings",
                    subtitle = "General app preferences",
                    onClick = { /* Handle app settings */ }
                )

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                SettingsItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Logout",
                    subtitle = "Sign out of your account",
                    onClick = onLogoutClick,
                    isDestructive = true
                )
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = if (isDestructive) Color.Red else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) Color.Red else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Navigate",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Logout",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Are you sure you want to logout from your account?",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(
                    text = "Logout",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

// Bottom Navigation Items
private val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, "home"),
    BottomNavItem("Transaction", null, "transaction", isCustomIcon = true),
    BottomNavItem("History", Icons.Default.History, "transactionHistory"),
    BottomNavItem("Account", Icons.Default.AccountCircle, "")
)