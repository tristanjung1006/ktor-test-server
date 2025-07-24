package com.example.di

import com.example.repository.HeroRepository
import com.example.repository.HeroRepositoryImpl
import org.koin.dsl.module

val koinModule = module {
    /*
    RepositoryImpl 인스턴스는 싱글톤 객체로 제공되어야함
     */
    single<HeroRepository> {
        HeroRepositoryImpl()
    }
}