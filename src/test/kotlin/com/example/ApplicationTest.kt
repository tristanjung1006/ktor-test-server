package com.example

import com.example.models.ApiResponse
import com.example.repository.HeroRepositoryImpl
import com.example.repository.NEXT_PAGE_KEY
import com.example.repository.PREVIOUS_PAGE_KEY
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Test
import org.koin.core.context.stopKoin
import kotlin.test.assertEquals

class ApplicationTest {

    /*
    테스트를 실행할 때마다 module() 함수가 호출되는데
    이전 테스트에서 이미 Koin이 초기화되어있어서 충돌이 발생하므로 Koin을 정리해준다.
     */

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `access root endpoint, assert correct information`() =
        testApplication {
            application { module() }
            val response = client.get("/")
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = response.status
            )
            assertEquals(
                expected = "Welcome to Boruto API!",
                actual = response.bodyAsText()
            )
        }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`() =
        testApplication {
            application { module() }
            val heroRepository = HeroRepositoryImpl()
            val pages = 1..5
            val heroes = listOf(
                heroRepository.page1,
                heroRepository.page2,
                heroRepository.page3,
                heroRepository.page4,
                heroRepository.page5
            )
            pages.forEach { page ->
                val response = client.get("/boruto/heroes?page=$page")
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status
                )
                val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())
                val expected = ApiResponse(
                    success = true,
                    message = "ok",
                    prevPage = calculatePage(page = page)["prevPage"],
                    nextPage = calculatePage(page = page)["nextPage"],
                    heroes = heroes[page - 1],
//                    lastUpdated = actual.lastUpdated
                )
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query non existing page number, assert error`() =
        testApplication {
            application { module() }
            val response = client.get("/boruto/heroes?page=6")
            assertEquals(
                expected = HttpStatusCode.NotFound,
                actual = response.status
            )
            assertEquals(
                expected = "Page Not Found.",
                actual = response.bodyAsText()
            )
        }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query invalid page number, assert error`() =
        testApplication {
            application { module() }
            val response = client.get("/boruto/heroes?page=invalid")
            assertEquals(
                expected = HttpStatusCode.BadRequest,
                actual = response.status
            )
            val expected = ApiResponse(
                success = false,
                message = "Only Numbers Allowed"
            )
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())
            assertEquals(
                expected = expected,
                actual = actual
            )
        }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hero name, assert single hero result`() =
        testApplication {
            application { module() }
            val response = client.get("/boruto/heroes/search?name=sas")
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())
                .heroes.size
            assertEquals(expected = 1, actual = actual)
        }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query hero name, assert multiple heroes result`() =
        testApplication {
            application { module() }
            val response = client.get("/boruto/heroes/search?name=sa")
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())
                .heroes.size
            assertEquals(expected = 3, actual = actual)
        }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query an empty text, assert empty list as a result`() =
        testApplication {
            application { module() }
            val response = client.get("/boruto/heroes/search?name=")
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())
                .heroes
            assertEquals(expected = emptyList(), actual = actual)
        }

    @ExperimentalSerializationApi
    @Test
    fun `access search heroes endpoint, query non existing hero, assert empty list as a result`() =
        testApplication {
            application { module() }
            val response = client.get("/boruto/heroes/search?name=unknown")
            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
            val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())
                .heroes
            assertEquals(expected = emptyList(), actual = actual)
        }

    @ExperimentalSerializationApi
    @Test
    fun `access non existing endpoint,assert not found`() =
        testApplication {
            application { module() }
            val response = client.get("/unknown")
            assertEquals(expected = HttpStatusCode.NotFound, actual = response.status)
            assertEquals(expected = "Page Not Found.", actual = response.bodyAsText())
        }

    private fun calculatePage(page: Int) =
        mapOf(
            PREVIOUS_PAGE_KEY to if (page in 2..5) page.minus(1) else null,
            NEXT_PAGE_KEY to if (page in 1..4) page.plus(1) else null
        )
}