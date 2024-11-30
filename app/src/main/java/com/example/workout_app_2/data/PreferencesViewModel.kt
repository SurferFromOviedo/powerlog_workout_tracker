package com.example.workout_app_2.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PreferencesViewModel(
    private val preferencesDataStore: PreferencesDataStore,
    private val firebaseRepository: FirebaseRepository,
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val templateDao: TemplateDao,
    private val templateSetDao: TemplateSetDao,
    private val setDao: SetDao

) : ViewModel(){

    init {
        observeRoomData()
    }

    private fun observeRoomData() {
        viewModelScope.launch {
            exerciseDao.getAll().collect { dataList ->
                syncExercisesWithFirebase(dataList)
            }
        }
        viewModelScope.launch {
            workoutDao.getAllWorkouts2().collect { dataList ->
                syncWorkoutsWithFirebase(dataList)
            }
        }
        viewModelScope.launch {
            templateDao.getAllTemplatesFlow().collect { dataList ->
                syncTemplatesWithFirebase(dataList)
            }
        }
        viewModelScope.launch {
            workoutDao.getAllWorkoutsExercises().collect { dataList ->
                syncWorkoutsExercisesWithFirebase(dataList)
            }
        }
        viewModelScope.launch {
            templateDao.getAllTemplateExercises().collect { dataList ->
                syncTemplateExercisesWithFirebase(dataList)
            }
        }
        viewModelScope.launch {
            templateSetDao.getAll().collect { dataList ->
                syncTemplateSetsWithFirebase(dataList)
            }
        }
        viewModelScope.launch {
            setDao.getAll().collect { dataList ->
                syncSetsWithFirebase(dataList)
            }
        }
    }

    private suspend fun syncExercisesWithFirebase(exerciseList: List<ExerciseEntity>) {
        val userDocument = firebaseRepository.getUserDocument()?: return
        val existingData = userDocument.collection("exercises").get().await()

        val firebaseDataMap = existingData.documents.associateBy(
            { it.id.toInt() },
            { it.toObject(ExerciseEntity::class.java) }
        )

        val roomIds = exerciseList.map { it.id }.toSet()
        firebaseDataMap.keys.filterNot { it in roomIds }.forEach { id ->
            firebaseRepository.deleteDataById(id, "exercises")
        }

        exerciseList.forEach { exercise ->
            firebaseRepository.uploadData(exercise, null, null, null, null, null, null)
        }
    }

    private suspend fun syncWorkoutsWithFirebase(workoutList: List<WorkoutEntity>) {
        val userDocument = firebaseRepository.getUserDocument()?: return
        val existingData = userDocument.collection("workouts").get().await()

        val firebaseDataMap = existingData.documents.associateBy(
            { it.id.toInt() },
            { it.toObject(WorkoutEntity::class.java) }
        )

        val roomIds = workoutList.map { it.id }.toSet()
        firebaseDataMap.keys.filterNot { it in roomIds }.forEach { id ->
            firebaseRepository.deleteDataById(id, "workouts")
        }

        workoutList.forEach { workout ->
            firebaseRepository.uploadData(null, null, null, null, null, null, workout)
        }
    }

    private suspend fun syncTemplatesWithFirebase(templateList: List<TemplateEntity>) {
        val userDocument = firebaseRepository.getUserDocument()?: return
        val existingData = userDocument.collection("templates").get().await()

        val firebaseDataMap = existingData.documents.associateBy(
            { it.id.toInt() },
            { it.toObject(TemplateEntity::class.java) }
        )

        val roomIds = templateList.map { it.id }.toSet()
        firebaseDataMap.keys.filterNot { it in roomIds }.forEach { id ->
            firebaseRepository.deleteDataById(id, "templates")
        }

        templateList.forEach { template ->
            firebaseRepository.uploadData(null, null, null, null, template, null, null)
        }
    }

    private suspend fun syncTemplateSetsWithFirebase(templateSetList: List<TemplateSetEntity>) {
        val userDocument = firebaseRepository.getUserDocument()?: return
        val existingData = userDocument.collection("template_sets").get().await()

        val firebaseDataMap = existingData.documents.associateBy(
            { it.id.toInt() },
            { it.toObject(ExerciseEntity::class.java) }
        )

        val roomIds = templateSetList.map { it.id }.toSet()
        firebaseDataMap.keys.filterNot { it in roomIds }.forEach { id ->
            firebaseRepository.deleteDataById(id, "template_sets")
        }

        templateSetList.forEach { templateSet ->
            firebaseRepository.uploadData(null, null, null, templateSet, null, null, null)
        }
    }

    private suspend fun syncSetsWithFirebase(setList: List<SetEntity>) {
        val userDocument = firebaseRepository.getUserDocument()?: return
        val existingData = userDocument.collection("sets").get().await()

        val firebaseDataMap = existingData.documents.associateBy(
            { it.id.toInt() },
            { it.toObject(ExerciseEntity::class.java) }
        )

        val roomIds = setList.map { it.id }.toSet()
        firebaseDataMap.keys.filterNot { it in roomIds }.forEach { id ->
            firebaseRepository.deleteDataById(id, "sets")
        }

        setList.forEach { set ->
            firebaseRepository.uploadData(null, set, null, null, null, null, null)
        }
    }

    private suspend fun syncWorkoutsExercisesWithFirebase(workoutsExercisesList: List<WorkoutExerciseEntity>) {
        val userDocument = firebaseRepository.getUserDocument()?: return
        val existingData = userDocument.collection("workout_exercises").get().await()

        val firebaseDataMap = existingData.documents.associateBy(
            { it.id.toInt() },
            { it.toObject(ExerciseEntity::class.java) }
        )

        val roomIds = workoutsExercisesList.map { it.id }.toSet()
        firebaseDataMap.keys.filterNot { it in roomIds }.forEach { id ->
            firebaseRepository.deleteDataById(id, "workout_exercises")
        }

        workoutsExercisesList.forEach { workoutExercise ->
            firebaseRepository.uploadData(null, null, null, null, null, workoutExercise, null)
        }
    }

    private suspend fun syncTemplateExercisesWithFirebase(templateExercisesList: List<TemplateExerciseEntity>) {
        val userDocument = firebaseRepository.getUserDocument()?: return
        val existingData = userDocument.collection("template_exercises").get().await()

        val firebaseDataMap = existingData.documents.associateBy(
            { it.id.toInt() },
            { it.toObject(ExerciseEntity::class.java) }
        )

        val roomIds = templateExercisesList.map { it.id }.toSet()
        firebaseDataMap.keys.filterNot { it in roomIds }.forEach { id ->
            firebaseRepository.deleteDataById(id, "template_exercises")
        }

        templateExercisesList.forEach { templateExercise ->
            firebaseRepository.uploadData(null, null, templateExercise, null, null, null, null)
        }
    }

    fun syncAfterLogin(onDecisionRequired: (Boolean) -> Unit) {
        viewModelScope.launch {
            val hasDataInFirestore = firebaseRepository.hasDataInFirestore()
            if (hasDataInFirestore) {
                onDecisionRequired(true)
            } else {
                uploadAllRoomDataToFirebase()
            }
        }
    }

    private suspend fun uploadAllRoomDataToFirebase() {
        val exercises = exerciseDao.getAll().firstOrNull() ?: emptyList()
        val workouts = workoutDao.getAllWorkouts2().firstOrNull() ?: emptyList()
        val workoutExercises = workoutDao.getAllWorkoutsExercises().firstOrNull() ?: emptyList()
        val templates = templateDao.getAllTemplatesFlow().firstOrNull() ?: emptyList()
        val templateExercises = templateDao.getAllTemplateExercises().firstOrNull() ?: emptyList()
        val templateSets = templateSetDao.getAll().firstOrNull() ?: emptyList()
        val sets = setDao.getAll().firstOrNull() ?: emptyList()

        syncExercisesWithFirebase(exercises)
        syncWorkoutsWithFirebase(workouts)
        syncWorkoutsExercisesWithFirebase(workoutExercises)
        syncTemplatesWithFirebase(templates)
        syncTemplateExercisesWithFirebase(templateExercises)
        syncTemplateSetsWithFirebase(templateSets)
        syncSetsWithFirebase(sets)
    }

    fun deleteAllDataInRoom() {
        viewModelScope.launch {
            exerciseDao.deleteAll()
            workoutDao.deleteAllWorkouts()
            workoutDao.deleteAllWorkoutsExercises()
            templateDao.deleteAllTemplates()
            templateDao.deleteAllTemplateExercises()
            templateSetDao.deleteAll()
            setDao.deleteAll()
        }
    }

    private suspend fun importDataToRoom(){
        val userDocument = firebaseRepository.getUserDocument()?: return
        val exercises = userDocument.collection("exercises").get().await()
        val sets = userDocument.collection("sets").get().await()
        val templateExercises = userDocument.collection("template_exercises").get().await()
        val templateSets = userDocument.collection("template_sets").get().await()
        val templates = userDocument.collection("templates").get().await()
        val workoutExercises = userDocument.collection("workout_exercises").get().await()
        val workouts = userDocument.collection("workouts").get().await()

        workoutDao.deleteAllWorkoutsExercises()
        templateDao.deleteAllTemplateExercises()

        val exerciseList = exercises.documents.mapNotNull { it.toObject(ExerciseEntity::class.java) }
        exerciseDao.deleteAll()
        exerciseDao.insertExercises(exerciseList)

        val workoutList = workouts.documents.mapNotNull { it.toObject(WorkoutEntity::class.java) }
        workoutDao.deleteAllWorkouts()
        workoutDao.insertAllWorkouts(workoutList)

        val workoutExerciseList = workoutExercises.documents.mapNotNull { it.toObject(WorkoutExerciseEntity::class.java) }
        workoutDao.insertAllWorkoutExercises(workoutExerciseList)

        val setList = sets.documents.mapNotNull { it.toObject(SetEntity::class.java) }
        setDao.deleteAll()
        setDao.insertSets(setList)

        val templateList = templates.documents.mapNotNull { it.toObject(TemplateEntity::class.java) }
        templateDao.deleteAllTemplates()
        templateDao.insertAllTemplates(templateList)

        val templateExerciseList = templateExercises.documents.mapNotNull { it.toObject(TemplateExerciseEntity::class.java) }
        templateDao.insertAllTemplateExercises(templateExerciseList)

        val templateSetList = templateSets.documents.mapNotNull { it.toObject(TemplateSetEntity::class.java) }
        templateSetDao.deleteAll()
        templateSetDao.insertAllTemplateSets(templateSetList)

    }

    fun importDataFromFirestore(){
        viewModelScope.launch {
            importDataToRoom()
        }
    }

    fun overwriteFirestoreWithRoomData(){
        viewModelScope.launch {
            val exercises = exerciseDao.getAll().first()
            syncExercisesWithFirebase(exercises)
            val workouts = workoutDao.getAllWorkouts2().first()
            syncWorkoutsWithFirebase(workouts)
            val workoutExercises = workoutDao.getAllWorkoutsExercises().first()
            syncWorkoutsExercisesWithFirebase(workoutExercises)
            val templates = templateDao.getAllTemplatesFlow().first()
            syncTemplatesWithFirebase(templates)
            val templateExercises = templateDao.getAllTemplateExercises().first()
            syncTemplateExercisesWithFirebase(templateExercises)
            val templateSets = templateSetDao.getAll().first()
            syncTemplateSetsWithFirebase(templateSets)
            val sets = setDao.getAll().first()
            syncSetsWithFirebase(sets)
        }
    }

    val statsExercise = mutableStateOf<Exercise?>(null)

    val startTimeFlow: StateFlow<Long> = preferencesDataStore.startTimeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val accumulatedTimeFlow: StateFlow<Long> = preferencesDataStore.accumulatedTimeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val isRunningFlow: StateFlow<Boolean> = preferencesDataStore.isRunningFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val exerciseFlow: StateFlow<List<Exercise>> = preferencesDataStore.exerciseFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unitFlow: StateFlow<String> = preferencesDataStore.unitFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "KG")

    val themeFlow: StateFlow<String> = preferencesDataStore.themeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Default")

    val dynamicColorFlow: StateFlow<Boolean> = preferencesDataStore.dynamicColorFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val primaryColorFlow: StateFlow<Int> = preferencesDataStore.primaryColorFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Color(0xFF6650a4).toArgb())

    val screenOnFlow: StateFlow<Boolean> = preferencesDataStore.screenOnFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun saveScreenOn(screenOn: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.saveScreenOn(screenOn)
        }
    }

    fun saveDynamicColor(dynamicColor: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.saveDynamicColor(dynamicColor)
        }
    }

    fun savePrimaryColor(primaryColor: Color) {
        viewModelScope.launch {
            preferencesDataStore.savePrimaryColor(primaryColor)
        }
    }

    fun saveTheme(theme: String) {
        viewModelScope.launch {
            preferencesDataStore.saveTheme(theme)
        }
    }

    fun saveUnit(unit: String) {
        viewModelScope.launch {
            preferencesDataStore.saveUnit(unit)
        }
    }

    fun saveTimer(startTime: Long, accumulatedTime: Long, isRunning: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.saveTimer(startTime, accumulatedTime, isRunning)
        }
    }

    fun saveExercises(exercises: List<Exercise>) {
        viewModelScope.launch {
            preferencesDataStore.saveExercises(exercises)
        }
    }
}