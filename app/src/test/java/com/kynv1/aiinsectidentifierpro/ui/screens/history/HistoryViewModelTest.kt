package com.kynv1.aiinsectidentifierpro.ui.screens.history

import com.kynv1.aiinsectidentifierpro.data.local.entity.InsectEntity
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import io.mockk.coVerify
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: InsectRepository

    private val entity = InsectEntity(
        id = 1L,
        imageUri = "content://fake",
        commonName = "Test Bug",
        scientificName = "Testus bugus",
        confidence = 90,
        description = "desc",
        characteristicsJson = "[]",
        habitat = "habitat",
        dangerLevel = "Low",
        dangerDescription = "none",
        timestamp = 0L
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `historyList starts null and updates once the repository flow emits`() = runTest(dispatcher) {
        every { repository.allInsectsFlow } returns flow {
            delay(100)
            emit(listOf(entity))
        }
        val viewModel = HistoryViewModel(repository)

        // Not loaded yet: distinct from "loaded and empty".
        assertNull(viewModel.historyList.value)

        // stateIn(WhileSubscribed) only starts the upstream flow once someone actually
        // collects — reading .value alone never triggers it.
        backgroundScope.launch { viewModel.historyList.collect {} }
        advanceUntilIdle()

        assertEquals(listOf(entity), viewModel.historyList.value)
    }

    @Test
    fun `deleteInsect delegates to the repository`() = runTest(dispatcher) {
        every { repository.allInsectsFlow } returns flow { }
        coEvery { repository.deleteInsectById(1L) } returns Unit
        val viewModel = HistoryViewModel(repository)

        viewModel.deleteInsect(1L)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.deleteInsectById(1L) }
    }
}
