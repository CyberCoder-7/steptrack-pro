package com.steptrack.pro.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.steptrack.pro.data.local.entity.DailyStepEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test running against a real (in-memory) SQLite database via
 * Room, verifying the upsert-by-date and range-query behavior that the
 * dashboard, history, and streak calculations all depend on.
 */
@RunWith(AndroidJUnit4::class)
class DailyStepDaoTest {

    private lateinit var db: StepTrackDatabase
    private lateinit var dao: DailyStepDao

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, StepTrackDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.dailyStepDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    private fun entity(date: String, steps: Int, goal: Int = 10000) = DailyStepEntity(
        date = date,
        steps = steps,
        goal = goal,
        distance = steps * 0.75,
        calories = steps * 0.04,
        activeMinutes = steps / 100,
        floors = steps / 160,
        lastUpdatedEpochMillis = System.currentTimeMillis()
    )

    @Test
    fun upsertThenGetByDate_returnsInsertedRow() = runBlocking {
        dao.upsert(entity("2026-01-01", steps = 4200))
        val result = dao.getByDate("2026-01-01")
        assertEquals(4200, result?.steps)
    }

    @Test
    fun upsertSameDateTwice_replacesRatherThanDuplicates() = runBlocking {
        dao.upsert(entity("2026-01-01", steps = 1000))
        dao.upsert(entity("2026-01-01", steps = 9000))
        val result = dao.getByDate("2026-01-01")
        assertEquals(9000, result?.steps)
    }

    @Test
    fun getByDate_missingDate_returnsNull() = runBlocking {
        assertNull(dao.getByDate("2099-12-31"))
    }

    @Test
    fun getRange_returnsOnlyDatesWithinBounds() = runBlocking {
        dao.upsert(entity("2026-01-01", steps = 1000))
        dao.upsert(entity("2026-01-05", steps = 2000))
        dao.upsert(entity("2026-01-10", steps = 3000))

        val result = dao.getRange("2026-01-02", "2026-01-06")
        assertEquals(1, result.size)
        assertEquals("2026-01-05", result.first().date)
    }

    @Test
    fun lifetimeTotalSteps_sumsAcrossAllDays() = runBlocking {
        dao.upsert(entity("2026-01-01", steps = 1000))
        dao.upsert(entity("2026-01-02", steps = 2500))
        dao.upsert(entity("2026-01-03", steps = 500))

        assertEquals(4000L, dao.getLifetimeTotalSteps())
    }

    @Test
    fun observeByDate_emitsUpdatedValueAfterUpsert() = runBlocking {
        dao.upsert(entity("2026-01-01", steps = 100))
        dao.upsert(entity("2026-01-01", steps = 250))
        val latest = dao.observeByDate("2026-01-01").first()
        assertEquals(250, latest?.steps)
    }

    @Test
    fun pruneOlderThan_removesOnlyStaleRows() = runBlocking {
        dao.upsert(entity("2025-01-01", steps = 1000))
        dao.upsert(entity("2026-01-01", steps = 2000))

        dao.pruneOlderThan("2025-06-01")

        assertNull(dao.getByDate("2025-01-01"))
        assertEquals(2000, dao.getByDate("2026-01-01")?.steps)
    }
}
