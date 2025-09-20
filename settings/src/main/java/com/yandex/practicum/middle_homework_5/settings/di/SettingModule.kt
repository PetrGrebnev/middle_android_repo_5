package com.yandex.practicum.middle_homework_5.settings.di

import com.yandex.practicum.middle_homework_5.settings.data.data_store.SettingsServiceImpl
import com.yandex.practicum.middle_homework_5.settings.ui.SettingsViewModel
import com.yandex.practicum.middle_homework_5.settings.ui.contract.SettingsService
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingModule = module {
    single<SettingsService> { SettingsServiceImpl(androidApplication()) }
    viewModel { SettingsViewModel(get()) }
}
