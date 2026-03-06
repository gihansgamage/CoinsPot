# Coins Pot – Build Prompt

Create a modern Android mobile application named **Coins Pot** that helps users track and manage savings for personal financial goals.

## 1) Core Purpose
Build an app where users can:
- Define personal savings goals.
- Track daily savings and occasional withdrawals.
- Visualize progress and trends.
- Stay motivated via milestones, rewards, and reminders.

## 2) First-Run Onboarding
When the app is opened for the first time, collect and persist:
- Name
- Age
- Monthly income
- Monthly food expenses
- Other monthly expenses

Then:
- Calculate estimated monthly saving capacity.
- Show this estimate in a friendly summary view.
- Save onboarding/profile data locally for future sessions.

## 3) Goal Management
Support multiple savings goals. Each goal must include:
- Goal name
- Target price/amount
- Initial amount (starting capital)
- Daily saving amount (planned)
- Target purchase date
- Goal image

For each goal, provide features to:
- Add daily savings manually.
- Withdraw from saved balance if needed.
- Auto-calculate and display:
  - Total saved amount
  - Remaining amount
  - Estimated purchase date (based on saving behavior)
- Show visual progress (progress bars and chart widgets).
- Mark goal as completed.
- Trigger a congratulatory state and award badges/achievements on completion.

## 4) Analytics & Insights
Implement an analytics section that shows:
- Daily savings trend.
- Total savings growth over time.
- Goal-by-goal progress snapshots.

Use data visualizations such as:
- Line charts
- Bar charts
- Progress indicators/cards

## 5) Smart Discovery Features
Add contextual features per goal:
- Related articles, buying guides, and relevant news.
- Recommended places to purchase items based on user country/region.
- Currency exchange support with daily rate updates.
- Multi-currency display and conversion.

## 6) UI/UX Requirements
Design a modern, clean, intuitive experience:
- Responsive layouts for different phone sizes.
- Smooth transitions/animations.
- Bottom navigation with at least:
  - Home
  - Goals
  - Analytics
  - Settings

## 7) Settings
Include a settings module with:
- Edit profile information.
- Currency and regional preferences.
- Reset all app data.
- About developer section.

## 8) Technical Constraints
- Platform: **Android**
- Architecture: **MVVM** (preferred)
- Persistence: **Room** (or equivalent local DB)
- Must be **offline-first**
- Keep code clean, maintainable, modular, and scalable

## 9) Suggested Engineering Expectations
- Separate layers clearly (presentation/domain/data).
- Use repository pattern for data access.
- Add validation for all user inputs.
- Add unit tests for core calculations (savings capacity, timeline estimates, currency conversion logic).
- Ensure accessibility basics (content descriptions, readable contrast, scalable text).

## 10) Delivery Criteria
The build is considered complete when:
- Onboarding, goals, analytics, and settings flows are fully functional.
- Data persists locally and survives app restarts.
- Multiple goals and currencies are supported.
- Charts and progress visuals reflect real stored data.
- Goal completion rewards are visible and persisted.
