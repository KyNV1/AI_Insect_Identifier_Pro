package com.kynv1.aiinsectidentifierpro.ui.screens.assistance

import com.kynv1.aiinsectidentifierpro.R
import com.kynv1.aiinsectidentifierpro.data.local.entity.ChatMessageEntity
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssistanceViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: InsectRepository
    private lateinit var viewModel: AssistanceViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
        coEvery { repository.getChatHistory() } returns emptyList()
        coEvery { repository.insertChatMessage(any()) } returns 1L
        viewModel = AssistanceViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendMessage appends user and assistant messages on success`() = runTest(dispatcher) {
        coEvery { repository.getChatResponse("Hello") } returns "Hi there"

        viewModel.sendMessage("Hello")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.messages.size)
        assertTrue(state.messages[0].isUser)
        assertEquals("Hello", state.messages[0].text)
        assertFalse(state.messages[1].isUser)
        assertEquals("Hi there", state.messages[1].text)
        assertFalse(state.isSending)
    }

    @Test
    fun `sendMessage while a reply is in flight is ignored`() = runTest(dispatcher) {
        coEvery { repository.getChatResponse(any()) } coAnswers {
            delay(1_000)
            "reply"
        }

        viewModel.sendMessage("first")
        // Reply is still in flight (dispatcher hasn't been advanced past the delay yet),
        // so this second call must be dropped by the isSending guard.
        viewModel.sendMessage("second")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.messages.size)
        assertEquals("first", state.messages[0].text)
        assertEquals("reply", state.messages[1].text)
    }

    @Test
    fun `sendMessage sets errorResId and clears isSending on failure`() = runTest(dispatcher) {
        coEvery { repository.getChatResponse(any()) } throws RuntimeException("boom")

        viewModel.sendMessage("Hello")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.assistance_error_generic, state.errorResId)
        assertFalse(state.isSending)
    }

    @Test
    fun `clearError resets errorResId to null`() = runTest(dispatcher) {
        coEvery { repository.getChatResponse(any()) } throws RuntimeException("boom")
        viewModel.sendMessage("Hello")
        advanceUntilIdle()
        assertEquals(R.string.assistance_error_generic, viewModel.uiState.value.errorResId)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorResId)
    }

    @Test
    fun `init loads chat history persisted from a previous session`() = runTest(dispatcher) {
        coEvery { repository.getChatHistory() } returns listOf(
            ChatMessageEntity(id = 1L, text = "Hello", isUser = true, timestamp = 100L),
            ChatMessageEntity(id = 2L, text = "Hi there", isUser = false, timestamp = 200L)
        )

        val restoredViewModel = AssistanceViewModel(repository)
        advanceUntilIdle()

        val messages = restoredViewModel.uiState.value.messages
        assertEquals(2, messages.size)
        assertEquals("Hello", messages[0].text)
        assertEquals("Hi there", messages[1].text)
    }

    @Test
    fun `clearHistory empties the conversation and the persisted store`() = runTest(dispatcher) {
        coEvery { repository.getChatResponse(any()) } returns "Hi there"
        coEvery { repository.clearChatHistory() } returns Unit
        viewModel.sendMessage("Hello")
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.messages.size)

        viewModel.clearHistory()
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.messages.size)
        coVerify(exactly = 1) { repository.clearChatHistory() }
    }
}
