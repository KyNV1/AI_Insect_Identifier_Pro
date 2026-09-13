package com.kynv1.aiinsectidentifierpro.ui.screens.scan

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.kynv1.aiinsectidentifierpro.data.model.InsectInfo
import com.kynv1.aiinsectidentifierpro.data.repository.InsectRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.InputStream

@OptIn(ExperimentalCoroutinesApi::class)
class ScanViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: InsectRepository
    private lateinit var viewModel: ScanViewModel
    private lateinit var context: Context
    private lateinit var uri: Uri
    private lateinit var bitmap: Bitmap

    private val insectInfo = InsectInfo(
        commonName = "Test Bug",
        scientificName = "Testus bugus",
        confidence = 90,
        description = "desc",
        characteristics = emptyList(),
        habitat = "habitat",
        dangerLevel = "Low",
        dangerDescription = "none"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
        viewModel = ScanViewModel(repository)

        uri = mockk(relaxed = true)
        every { uri.toString() } returns "content://test"
        bitmap = mockk(relaxed = true)
        val resolver = mockk<ContentResolver>()
        val inputStream = mockk<InputStream>(relaxed = true)
        context = mockk()
        every { context.contentResolver } returns resolver
        every { resolver.openInputStream(uri) } returns inputStream

        // Bypasses the real (native-backed) decode pipeline: the fake input stream can't be
        // decoded for real, so the static decoder is stubbed to hand back a fixed test bitmap.
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeStream(any(), any(), any()) } returns bitmap
    }

    @After
    fun tearDown() {
        unmockkStatic(BitmapFactory::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `identifyInsect ignores reentrant call while already loading`() = runTest(dispatcher) {
        coEvery { repository.identifyInsect(bitmap) } coAnswers {
            delay(1_000)
            insectInfo
        }
        coEvery { repository.insertInsect(any()) } returns 1L

        viewModel.onImageSelected(uri)
        viewModel.identifyInsect(context)
        // Still Loading — the dispatcher hasn't advanced past the delay yet.
        viewModel.identifyInsect(context)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.identifyInsect(bitmap) }
    }

    @Test
    fun `identifyInsect sets Success state with the inserted id`() = runTest(dispatcher) {
        coEvery { repository.identifyInsect(bitmap) } returns insectInfo
        coEvery { repository.insertInsect(any()) } returns 42L

        viewModel.onImageSelected(uri)
        viewModel.identifyInsect(context)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ScanUiState.Success)
        assertEquals(42L, (state as ScanUiState.Success).insectId)
    }

    @Test
    fun `cancelIdentify prevents a late result from overwriting Idle`() = runTest(dispatcher) {
        coEvery { repository.identifyInsect(bitmap) } coAnswers {
            delay(1_000)
            insectInfo
        }
        coEvery { repository.insertInsect(any()) } returns 42L

        viewModel.onImageSelected(uri)
        viewModel.identifyInsect(context)
        viewModel.cancelIdentify()
        advanceUntilIdle()

        assertEquals(ScanUiState.Idle, viewModel.uiState.value)
    }
}
