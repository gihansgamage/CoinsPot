//package com.gihansgamage.coinspot.domain.engine
//
//import com.gihansgamage.coinspot.data.local.database.entities.*
//import com.gihansgamage.coinspot.domain.repository.SavingGoalRepository
//import com.gihansgamage.coinspot.domain.repository.UserRepository
//import kotlinx.coroutines.flow.first
//import java.time.DayOfWeek
//import java.time.LocalDate
//import javax.inject.Inject
//import javax.inject.Singleton
//import kotlin.math.abs
//import kotlin.math.pow
//
///**
// * Smart Savings Engine
// * Provides AI-powered recommendations for optimal savings strategies
// */
//@Singleton
//class SmartSavingsEngine @Inject constructor(
//    private val userRepository: UserRepository,
//    private val goalRepository: SavingGoalRepository
//) {
//
//    /**
//     * Calculate optimal savings recommendations based on user profile and goals
//     */
//    suspend fun calculateOptimalSavings(): OptimalSavingsRecommendation {
//        val userProfile = userRepository.getUserProfile().first()
//            ?: return getDefaultRecommendation()
//
//        val activeGoals = goalRepository.getActiveGoals().first()
//        val savingsHistory = goalRepository.getAllGoals().first()
//
//        val disposableIncome = userProfile.disposableIncome
//        val totalTargetAmount = activeGoals.sumOf { it.remainingAmount }
//
//        // Calculate recommended daily amount
//        val recommendedDaily = calculateRecommendedDailyAmount(
//            disposableIncome = disposableIncome,
//            activeGoals = activeGoals,
//            urgentGoals = activeGoals.filter { it.priority == GoalPriority.URGENT }
//        )
//
//        // Analyze spending patterns
//        val opportunities = findSavingOpportunities(userProfile, activeGoals)
//
//        // Assess financial risk
//        val riskLevel = assessFinancialRisk(disposableIncome, totalTargetAmount)
//
//        // Prioritize goals
//        val prioritization = prioritizeGoals(activeGoals)
//
//        // Suggest alternative strategies
//        val strategies = suggestAlternativeStrategies(userProfile, activeGoals)
//
//        // Generate personalized tips
//        val tips = generatePersonalizedTips(userProfile, activeGoals)
//
//        return OptimalSavingsRecommendation(
//            recommendedDailyAmount = recommendedDaily,
//            recommendedWeeklyAmount = recommendedDaily * 7,
//            recommendedMonthlyAmount = recommendedDaily * 30,
//            savingOpportunities = opportunities,
//            riskAssessment = riskLevel,
//            goalPrioritization = prioritization,
//            alternativeStrategies = strategies,
//            personalizedTips = tips,
//            confidence = calculateConfidenceScore(userProfile, activeGoals)
//        )
//    }
//
//    /**
//     * Calculate recommended daily savings amount
//     */
//    private fun calculateRecommendedDailyAmount(
//        disposableIncome: Double,
//        activeGoals: List<SavingGoal>,
//        urgentGoals: List<SavingGoal>
//    ): Double {
//        if (activeGoals.isEmpty()) {
//            // Suggest 10% of disposable income for general savings
//            return (disposableIncome * 0.10) / 30
//        }
//
//        // Calculate based on goal deadlines and amounts
//        val totalNeeded = activeGoals.sumOf { it.remainingAmount }
//        val averageDaysRemaining = activeGoals.map { it.daysUntilTarget }
//            .filter { it > 0 }
//            .average()
//            .takeIf { !it.isNaN() } ?: 180.0 // Default 6 months
//
//        val baseDaily = totalNeeded / averageDaysRemaining
//
//        // Adjust for urgent goals
//        val urgentAdjustment = if (urgentGoals.isNotEmpty()) {
//            urgentGoals.sumOf { it.dailySavingNeeded } * 0.3
//        } else 0.0
//
//        val recommended = baseDaily + urgentAdjustment
//
//        // Cap at 30% of disposable income per day
//        val maxDaily = (disposableIncome * 0.30) / 30
//
//        return recommended.coerceIn(
//            minimumValue = 1.0,
//            maximumValue = maxDaily
//        )
//    }
//
//    /**
//     * Find potential saving opportunities based on user profile
//     */
//    private suspend fun findSavingOpportunities(
//        userProfile: UserProfile,
//        activeGoals: List<SavingGoal>
//    ): List<SavingOpportunity> {
//        val opportunities = mutableListOf<SavingOpportunity>()
//
//        // Opportunity 1: Expense reduction
//        val monthlyExpenses = userProfile.monthlyExpenses
//        if (monthlyExpenses > 0) {
//            val potentialSaving = monthlyExpenses * 0.10 // Assume 10% can be reduced
//            opportunities.add(
//                SavingOpportunity(
//                    id = "reduce_expenses",
//                    title = "Reduce Monthly Expenses",
//                    description = "By cutting 10% of your monthly expenses, you could save ${userProfile.currencySymbol}${String.format("%.2f", potentialSaving)} more each month",
//                    potentialMonthlySavings = potentialSaving,
//                    potentialDailySavings = potentialSaving / 30,
//                    category = "Expense Management",
//                    difficulty = Difficulty.MEDIUM,
//                    tips = listOf(
//                        "Review subscriptions you don't use",
//                        "Cook at home more often",
//                        "Compare prices before buying",
//                        "Use public transport when possible"
//                    ),
//                    impact = Impact.HIGH
//                )
//            )
//        }
//
//        // Opportunity 2: Side income
//        val disposableIncome = userProfile.disposableIncome
//        if (disposableIncome < userProfile.monthlyIncome * 0.3) {
//            opportunities.add(
//                SavingOpportunity(
//                    id = "side_income",
//                    title = "Consider Side Income",
//                    description = "A small side income of ${userProfile.currencySymbol}200/month could significantly boost your savings",
//                    potentialMonthlySavings = 200.0,
//                    potentialDailySavings = 200.0 / 30,
//                    category = "Income",
//                    difficulty = Difficulty.HARD,
//                    tips = listOf(
//                        "Freelance in your spare time",
//                        "Sell unused items online",
//                        "Offer tutoring or consulting",
//                        "Start a small online business"
//                    ),
//                    impact = Impact.VERY_HIGH
//                )
//            )
//        }
//
//        // Opportunity 3: Round-up savings
//        opportunities.add(
//            SavingOpportunity(
//                id = "round_up",
//                title = "Round-Up Savings",
//                description = "Round up your purchases to the nearest ${userProfile.currencySymbol}5 and save the difference",
//                potentialMonthlySavings = 50.0, // Estimate
//                potentialDailySavings = 50.0 / 30,
//                category = "Automated Savings",
//                difficulty = Difficulty.EASY,
//                tips = listOf(
//                    "Enable round-up feature in settings",
//                    "Link your bank account",
//                    "Watch small amounts add up",
//                    "Set round-up to ${userProfile.currencySymbol}5 or ${userProfile.currencySymbol}10"
//                ),
//                impact = Impact.MEDIUM
//            )
//        )
//
//        // Opportunity 4: Challenge-based savings
//        opportunities.add(
//            SavingOpportunity(
//                id = "52_week_challenge",
//                title = "52-Week Savings Challenge",
//                description = "Start with ${userProfile.currencySymbol}1 in week 1, ${userProfile.currencySymbol}2 in week 2, and so on. Save ${userProfile.currencySymbol}1,378 in a year!",
//                potentialMonthlySavings = 114.83, // Average per month
//                potentialDailySavings = 3.77,
//                category = "Challenge",
//                difficulty = Difficulty.MEDIUM,
//                tips = listOf(
//                    "Mark your calendar for weekly deposits",
//                    "Start with reverse challenge if easier",
//                    "Join with friends for motivation",
//                    "Track progress in the app"
//                ),
//                impact = Impact.HIGH
//            )
//        )
//
//        // Opportunity 5: Goal-specific optimization
//        activeGoals.forEach { goal ->
//            if (goal.daysUntilTarget > 0 && goal.velocity < -15) {
//                opportunities.add(
//                    SavingOpportunity(
//                        id = "optimize_${goal.id}",
//                        title = "Accelerate '${goal.name}'",
//                        description = "You're ${abs(goal.velocity).toInt()}% behind schedule. Save an extra ${userProfile.currencySymbol}${String.format("%.2f", goal.dailySavingNeeded * 0.5)} daily to catch up",
//                        potentialMonthlySavings = goal.dailySavingNeeded * 0.5 * 30,
//                        potentialDailySavings = goal.dailySavingNeeded * 0.5,
//                        category = "Goal Optimization",
//                        difficulty = Difficulty.MEDIUM,
//                        tips = listOf(
//                            "Skip one coffee/meal out per day",
//                            "Temporarily reduce non-essential spending",
//                            "Extend deadline by ${(abs(goal.velocity) / 10).toInt()} days if needed",
//                            "Consider a one-time boost from savings"
//                        ),
//                        impact = Impact.HIGH
//                    )
//                )
//            }
//        }
//
//        return opportunities.sortedByDescending { it.impact.priority }
//    }
//
//    /**
//     * Assess financial risk level
//     */
//    private fun assessFinancialRisk(
//        disposableIncome: Double,
//        totalTargetAmount: Double
//    ): RiskAssessment {
//        val monthsToSave = if (disposableIncome > 0) {
//            totalTargetAmount / disposableIncome
//        } else 0.0
//
//        val riskLevel = when {
//            monthsToSave <= 3 -> RiskLevel.LOW
//            monthsToSave <= 6 -> RiskLevel.MEDIUM
//            monthsToSave <= 12 -> RiskLevel.HIGH
//            else -> RiskLevel.VERY_HIGH
//        }
//
//        val messages = when (riskLevel) {
//            RiskLevel.LOW -> listOf(
//                "Great! Your goals are achievable within 3 months",
//                "You have healthy savings capacity",
//                "Consider setting stretch goals"
//            )
//            RiskLevel.MEDIUM -> listOf(
//                "Your goals are achievable in 3-6 months",
//                "Maintain consistent savings habits",
//                "Look for opportunities to save more"
//            )
//            RiskLevel.HIGH -> listOf(
//                "Your goals will take 6-12 months",
//                "Consider prioritizing goals",
//                "Look for ways to increase income or reduce expenses"
//            )
//            RiskLevel.VERY_HIGH -> listOf(
//                "Your goals are ambitious (12+ months)",
//                "Focus on 1-2 priority goals",
//                "Consider adjusting timelines or amounts",
//                "Explore additional income sources"
//            )
//        }
//
//        return RiskAssessment(
//            level = riskLevel,
//            score = calculateRiskScore(disposableIncome, totalTargetAmount),
//            messages = messages,
//            recommendations = generateRiskRecommendations(riskLevel)
//        )
//    }
//
//    /**
//     * Calculate numeric risk score (0-100)
//     */
//    private fun calculateRiskScore(disposableIncome: Double, totalTarget: Double): Int {
//        if (disposableIncome <= 0) return 100
//
//        val ratio = totalTarget / (disposableIncome * 12) // Years to save
//        val score = (ratio * 50).coerceIn(0.0, 100.0)
//
//        return score.toInt()
//    }
//
//    /**
//     * Generate recommendations based on risk level
//     */
//    private fun generateRiskRecommendations(riskLevel: RiskLevel): List<String> {
//        return when (riskLevel) {
//            RiskLevel.LOW -> listOf(
//                "You're in great shape! Keep up the momentum",
//                "Consider setting additional goals",
//                "Help friends with their savings journey"
//            )
//            RiskLevel.MEDIUM -> listOf(
//                "Stay focused on your current goals",
//                "Review expenses monthly for optimization",
//                "Celebrate small wins to stay motivated"
//            )
//            RiskLevel.HIGH -> listOf(
//                "Prioritize your most important goals",
//                "Consider extending deadlines slightly",
//                "Look for 1-2 expense reduction opportunities",
//                "Set up automatic savings if possible"
//            )
//            RiskLevel.VERY_HIGH -> listOf(
//                "Focus on ONE priority goal at a time",
//                "Significantly extend deadlines or reduce amounts",
//                "Explore side income opportunities",
//                "Consider starting with smaller, achievable goals",
//                "Review if goals are realistic given current income"
//            )
//        }
//    }
//
//    /**
//     * Prioritize goals using weighted scoring
//     */
//    private fun prioritizeGoals(goals: List<SavingGoal>): List<PrioritizedGoal> {
//        return goals.map { goal ->
//            val score = calculateGoalScore(goal)
//            PrioritizedGoal(
//                goal = goal,
//                priorityScore = score,
//                reasoning = generatePriorityReasoning(goal, score)
//            )
//        }.sortedByDescending { it.priorityScore }
//    }
//
//    /**
//     * Calculate priority score for a goal
//     */
//    private fun calculateGoalScore(goal: SavingGoal): Double {
//        var score = 0.0
//
//        // Factor 1: Priority level (0-40 points)
//        score += when (goal.priority) {
//            GoalPriority.URGENT -> 40.0
//            GoalPriority.HIGH -> 30.0
//            GoalPriority.MEDIUM -> 20.0
//            GoalPriority.LOW -> 10.0
//        }
//
//        // Factor 2: Progress (0-20 points)
//        score += goal.progressPercentage * 0.2
//
//        // Factor 3: Time urgency (0-20 points)
//        val timeUrgency = when {
//            goal.daysUntilTarget <= 7 -> 20.0
//            goal.daysUntilTarget <= 30 -> 15.0
//            goal.daysUntilTarget <= 90 -> 10.0
//            else -> 5.0
//        }
//        score += timeUrgency
//
//        // Factor 4: Behind schedule penalty (0-20 points)
//        if (goal.velocity < 0) {
//            score += abs(goal.velocity) * 0.2
//        }
//
//        return score.coerceIn(0.0, 100.0)
//    }
//
//    /**
//     * Generate reasoning for priority
//     */
//    private fun generatePriorityReasoning(goal: SavingGoal, score: Double): String {
//        return when {
//            score >= 80 -> "Critical: ${goal.name} needs immediate attention"
//            score >= 60 -> "High priority: Focus on ${goal.name} soon"
//            score >= 40 -> "Medium priority: Keep ${goal.name} on track"
//            else -> "Low priority: ${goal.name} is going well"
//        }
//    }
//
//    /**
//     * Suggest alternative saving strategies
//     */
//    private fun suggestAlternativeStrategies(
//        userProfile: UserProfile,
//        goals: List<SavingGoal>
//    ): List<AlternativeStrategy> {
//        val strategies = mutableListOf<AlternativeStrategy>()
//
//        // Strategy 1: Accelerated savings
//        strategies.add(
//            AlternativeStrategy(
//                name = "52-Week Challenge",
//                description = "Gradually increase savings each week",
//                projectedSavings = 1378.0,
//                duration = "1 year",
//                difficulty = Difficulty.MEDIUM,
//                pros = listOf(
//                    "Starts easy and builds momentum",
//                    "Saves substantial amount over time",
//                    "Fun and gamified approach"
//                ),
//                cons = listOf(
//                    "Requires discipline",
//                    "Gets harder at the end",
//                    "May conflict with other goals"
//                )
//            )
//        )
//
//        // Strategy 2: Percentage-based
//        strategies.add(
//            AlternativeStrategy(
//                name = "50/30/20 Rule",
//                description = "Save 20% of income automatically",
//                projectedSavings = userProfile.monthlyIncome * 0.20 * 12,
//                duration = "Ongoing",
//                difficulty = Difficulty.EASY,
//                pros = listOf(
//                    "Simple and automatic",
//                    "Balanced approach",
//                    "Works with any income"
//                ),
//                cons = listOf(
//                    "May be too aggressive for some",
//                    "Less flexible",
//                    "Doesn't account for varying expenses"
//                )
//            )
//        )
//
//        // Strategy 3: Targeted sprint
//        if (goals.isNotEmpty()) {
//            val topGoal = goals.maxByOrNull { calculateGoalScore(it) }
//            topGoal?.let {
//                strategies.add(
//                    AlternativeStrategy(
//                        name = "Goal Sprint",
//                        description = "Focus all savings on '${it.name}' for maximum speed",
//                        projectedSavings = it.remainingAmount,
//                        duration = "${(it.remainingAmount / (userProfile.disposableIncome * 0.5)).toInt()} months",
//                        difficulty = Difficulty.HARD,
//                        pros = listOf(
//                            "Achieve one goal quickly",
//                            "Clear focus and motivation",
//                            "Faster results"
//                        ),
//                        cons = listOf(
//                            "Other goals delayed",
//                            "Requires sacrifice",
//                            "Higher pressure"
//                        )
//                    )
//                )
//            }
//        }
//
//        return strategies
//    }
//
//    /**
//     * Generate personalized tips
//     */
//    private fun generatePersonalizedTips(
//        userProfile: UserProfile,
//        goals: List<SavingGoal>
//    ): List<PersonalizedTip> {
//        val tips = mutableListOf<PersonalizedTip>()
//
//        // Tip based on day of week
//        val dayOfWeek = LocalDate.now().dayOfWeek
//        if (dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY) {
//            tips.add(
//                PersonalizedTip(
//                    title = "Weekend Savings Challenge",
//                    description = "Try a no-spend weekend and save what you would have spent!",
//                    category = "Behavioral",
//                    potentialSaving = 50.0,
//                    ease = Difficulty.MEDIUM
//                )
//            )
//        }
//
//        // Tip based on goal progress
//        goals.firstOrNull { it.progressPercentage >= 80 }?.let {
//            tips.add(
//                PersonalizedTip(
//                    title = "You're So Close!",
//                    description = "'${it.name}' is ${it.progressPercentage.toInt()}% complete. One final push!",
//                    category = "Motivation",
//                    potentialSaving = it.remainingAmount,
//                    ease = Difficulty.EASY
//                )
//            )
//        }
//
//        // Tip based on disposable income
//        if (userProfile.disposableIncome > userProfile.monthlyIncome * 0.4) {
//            tips.add(
//                PersonalizedTip(
//                    title = "You Have Room to Save More",
//                    description = "With ${userProfile.currencySymbol}${String.format("%.2f", userProfile.disposableIncome)} disposable income, consider increasing your savings rate",
//                    category = "Opportunity",
//                    potentialSaving = userProfile.disposableIncome * 0.1,
//                    ease = Difficulty.EASY
//                )
//            )
//        }
//
//        return tips
//    }
//
//    /**
//     * Calculate confidence score for recommendations
//     */
//    private fun calculateConfidenceScore(
//        userProfile: UserProfile,
//        goals: List<SavingGoal>
//    ): Double {
//        var confidence = 50.0 // Base confidence
//
//        // More confidence if user has disposable income data
//        if (userProfile.monthlyIncome > 0) confidence += 20.0
//        if (userProfile.monthlyExpenses > 0) confidence += 20.0
//
//        // More confidence with more goals (better pattern analysis)
//        confidence += (goals.size.coerceAtMost(3) * 3.0)
//
//        return confidence.coerceIn(0.0, 100.0)
//    }
//
//    /**
//     * Get default recommendation if user profile is incomplete
//     */
//    private fun getDefaultRecommendation(): OptimalSavingsRecommendation {
//        return OptimalSavingsRecommendation(
//            recommendedDailyAmount = 5.0,
//            recommendedWeeklyAmount = 35.0,
//            recommendedMonthlyAmount = 150.0,
//            savingOpportunities = emptyList(),
//            riskAssessment = RiskAssessment(
//                level = RiskLevel.MEDIUM,
//                score = 50,
//                messages = listOf("Complete your profile for personalized recommendations"),
//                recommendations = listOf("Add income and expense information")
//            ),
//            goalPrioritization = emptyList(),
//            alternativeStrategies = emptyList(),
//            personalizedTips = emptyList(),
//            confidence = 30.0
//        )
//    }
//}
//
//// Data classes for recommendations
//data class OptimalSavingsRecommendation(
//    val recommendedDailyAmount: Double,
//    val recommendedWeeklyAmount: Double,
//    val recommendedMonthlyAmount: Double,
//    val savingOpportunities: List<SavingOpportunity>,
//    val riskAssessment: RiskAssessment,
//    val goalPrioritization: List<PrioritizedGoal>,
//    val alternativeStrategies: List<AlternativeStrategy>,
//    val personalizedTips: List<PersonalizedTip>,
//    val confidence: Double // 0-100%
//)
//
//data class SavingOpportunity(
//    val id: String,
//    val title: String,
//    val description: String,
//    val potentialMonthlySavings: Double,
//    val potentialDailySavings: Double,
//    val category: String,
//    val difficulty: Difficulty,
//    val tips: List<String>,
//    val impact: Impact
//)
//
//data class RiskAssessment(
//    val level: RiskLevel,
//    val score: Int, // 0-100
//    val messages: List<String>,
//    val recommendations: List<String>
//)
//
//data class PrioritizedGoal(
//    val goal: SavingGoal,
//    val priorityScore: Double,
//    val reasoning: String
//)
//
//data class AlternativeStrategy(
//    val name: String,
//    val description: String,
//    val projectedSavings: Double,
//    val duration: String,
//    val difficulty: Difficulty,
//    val pros: List<String>,
//    val cons: List<String>
//)
//
//data class PersonalizedTip(
//    val title: String,
//    val description: String,
//    val category: String,
//    val potentialSaving: Double,
//    val ease: Difficulty
//)
//
//enum class Difficulty {
//    EASY, MEDIUM, HARD
//}
//
//enum class Impact(val priority: Int) {
//    LOW(1),
//    MEDIUM(2),
//    HIGH(3),
//    VERY_HIGH(4)
//}
//
//enum class RiskLevel {
//    LOW, MEDIUM, HIGH, VERY_HIGH
//}