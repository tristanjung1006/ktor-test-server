package com.example.routes

import com.example.models.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.response.respond

fun Route.getAllHeroes() {
    get("/boruto/heroes") {
        // 클라이언트가 엔드포인트에서 오류를 발생하면 엘비스 연산자로 기본 1페이지 제공
        try {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            require(page in 1..5)

            call.respond(message = page)
        } catch (e: NumberFormatException) {
            call.respond(
                message = ApiResponse(success = false, message = "Only Numbers Allowed"),
                status = HttpStatusCode.BadRequest
            )
        } catch (e: IllegalArgumentException) {
            call.respond(
                message = ApiResponse(success = false, message = "Heroes not Found"),
                status = HttpStatusCode.NotFound
            )
        }
    }
}