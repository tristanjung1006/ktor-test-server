package com.example.plugins

import com.example.di.koinModule
import io.ktor.server.application.*
import org.koin.core.context.GlobalContext.startKoin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {

    /*
    install(Koin)으로 선언할 경우 Ktor 플러그인 시스템을 통한 설정 방식인데
    람다의 receiver 타입이 명확하지 않아서 컴파일러가 타입추론에 실패함

    startKoin은 Koin의 GlobalContext를 직접 초기화하는 방식으로
    람다의 receiver 타입이 명확하게 정의되어있음
     */

    startKoin {
        slf4jLogger()
        modules(
            // Your modules here
            koinModule
        )
    }
}