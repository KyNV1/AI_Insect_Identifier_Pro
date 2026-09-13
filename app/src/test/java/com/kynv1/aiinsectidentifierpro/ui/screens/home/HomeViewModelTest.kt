package com.kynv1.aiinsectidentifierpro.ui.screens.home

import com.kynv1.aiinsectidentifierpro.data.local.PremiumStore
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: InsectRepository
    private lateinit var premiumStore: PremiumStore

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
        premiumStore = mockk(relaxed = true)
        every { repository.getMockInsects() } returns emptyList()
        every { repository.getMockArticles() } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isPremium reflects the persisted value on start`() = runTest(dispatcher) {
        every { premiumStore.isPremium() } returns true

        val viewModel = HomeViewModel(repository, premiumStore)

        assertTrue(viewModel.uiState.value.isPremium)
    }

    @Test
    fun `purchasePremium persists the purchase so it survives a restart`() = runTest(dispatcher) {
        every { premiumStore.isPremium() } returns false
        val viewModel = HomeViewModel(repository, premiumStore)

        viewModel.purchasePremium()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isPremium)
        verify(exactly = 1) { premiumStore.setPremium(true) }
    }
}
