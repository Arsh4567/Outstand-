package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.di.AppContainer
import com.example.di.MindCoachViewModelFactory
import com.example.ui.components.MindCoachBottomNav
import com.example.ui.navigation.AICoachRoute
import com.example.ui.navigation.AddGoalRoute
import com.example.ui.navigation.DashboardRoute
import com.example.ui.navigation.GoalsRoute
import com.example.ui.navigation.InsightsRoute
import com.example.ui.navigation.OnboardingRoute
import com.example.ui.screens.dashboard.AICoachScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.dashboard.InsightsScreen
import com.example.ui.screens.goals.AddGoalScreen
import com.example.ui.screens.goals.GoalsScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GoalsViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import com.example.ui.navigation.SplashScreenRoute
import com.example.ui.screens.splash.AnimatedSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route?.substringAfterLast(".")
                
                // Provide ViewModel
                val factory = MindCoachViewModelFactory(AppContainer.mindCoachRepository)
                val goalsViewModel: GoalsViewModel = viewModel(factory = factory)
                val aiCoachViewModel: com.example.viewmodel.AICoachViewModel = viewModel(factory = factory)
                val dashboardViewModel: com.example.viewmodel.DashboardViewModel = viewModel(factory = factory)
                val goals by goalsViewModel.allGoals.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentDestination != "SplashScreenRoute" && currentDestination != "OnboardingRoute" && currentDestination != "AddGoalRoute" && currentDestination != null) {
                            MindCoachBottomNav(
                                currentRoute = currentDestination,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(DashboardRoute) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = SplashScreenRoute,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<SplashScreenRoute> {
                            AnimatedSplashScreen(
                                onSplashComplete = {
                                    navController.navigate(OnboardingRoute) {
                                        popUpTo(SplashScreenRoute) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable<OnboardingRoute> {
                            OnboardingScreen(
                                onFinishOnboarding = {
                                    navController.navigate(DashboardRoute) {
                                        popUpTo(OnboardingRoute) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable<DashboardRoute> {
                            DashboardScreen(viewModel = dashboardViewModel)
                        }
                        composable<GoalsRoute> {
                            GoalsScreen(
                                goals = goals,
                                onAddGoal = { navController.navigate(AddGoalRoute) },
                                onGoalClick = { /* Navigate to Roadmap Detail */ }
                            )
                        }
                        composable<AddGoalRoute> {
                            AddGoalScreen(
                                onSaveGoal = { title, description, category ->
                                    goalsViewModel.addGoal(title, description, category)
                                    navController.popBackStack()
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable<InsightsRoute> {
                            InsightsScreen()
                        }
                        composable<AICoachRoute> {
                            AICoachScreen(viewModel = aiCoachViewModel)
                        }
                    }
                }
            }
        }
    }
}


