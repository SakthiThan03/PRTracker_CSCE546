PRTracker – Check-in Outline
STANDARD REQUIREMENTS:
1. Multiple Screens
- Home/Dashboard screen (recent workouts + navigation)
- Log Workout screen (add exercises, sets, reps, weight)
- Workout History screen (view past workouts by date)
- Progress screen (track improvement over time)
- Optional: Edit Workout screen or editing within History

2. Clear Use-Case and Intuitive UI
- Purpose: Track workouts and personal records over time
- Simple navigation (buttons or bottom nav bar)
- Clear labels: Log Workout, History, Progress
- Large buttons and readable layout
- Straightforward form input (exercise, sets, reps, weight)
- Goal: User can log a workout in under a minute

3. ViewModels and Data Storage
- ViewModel:
  - Handles UI state
  - Stores current workout being entered
  - Survives screen rotation
- Room Database:
  - Stores workouts and exercise entries
- Optional:
  - DataStore/SharedPreferences for settings (units, theme)
- Architecture:
  - Entity → DAO → Repository → ViewModel → UI

4. Screen Rotation and Lifecycle Handling
- Store UI state in ViewModel
- Use rememberSaveable for temporary UI state
- Persist workouts in Room after saving
- Ensure no data loss on rotation or backgrounding
- Test rotation during input and navigation

5. Code Organization and Readability
- Package structure:
  - ui/
  - viewmodel/
  - data/
  - data/local/
  - repository/
  - model/
  - navigation/
- Use MVVM architecture
- Keep UI, logic, and data layers separate
- Add comments for complex logic only
PRTRACKER SPECIFIC REQUIREMENTS:
6. Logging Exercises
- Each workout contains multiple exercise entries
- Each entry includes:
  - Exercise name
  - Sets
  - Reps
  - Weight
- Users can add multiple exercises per workout

7. Timestamped Workouts
- Each workout stores timestamp (Long)
- Display formatted date (e.g., Apr 1, 2026)
- Sort and group workouts by date in History

8. Local Database (Room)
- WorkoutEntity:
  - id
  - timestamp
- ExerciseEntryEntity:
  - id
  - workoutId (foreign key)
  - exerciseName
  - sets
  - reps
  - weight
- One-to-many relationship: Workout → ExerciseEntries

9. Progress Tracking
- Show improvement over time:
  - Personal records (max weight per exercise)
  - Workout frequency
  - Total volume trends
- Basic version:
  - Display best weight and recent history per exercise

10. Edit/Delete Workouts
- From History screen:
  - Tap workout → open details
  - Edit exercise entries
  - Delete individual entries or full workout
- Use IDs from Room to update/delete data
