package com.ayteneve93.apexexplorer.di

import com.ayteneve93.apexexplorer.data.DataModelManager
import com.ayteneve93.apexexplorer.prompt.BiometricAuthPrompt.BiometricAuthManager
import com.ayteneve93.apexexplorer.prompt.FireBaseAuthPrompt.FireBaseAuthManager
import com.ayteneve93.apexexplorer.application.ApplicationPreference
import com.ayteneve93.apexexplorer.view.entry.EntryViewModel
import com.ayteneve93.apexexplorer.view.main.MainViewModel
import com.ayteneve93.apexexplorer.view.main.fragments.favorite.FavoriteViewModel
import com.ayteneve93.apexexplorer.view.main.fragments.filelist.FileListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Activities
    viewModel {
        EntryViewModel(get(), get())
    }
    viewModel {
        MainViewModel(get())
    }
    // Fragments - Main
    viewModel {
        FileListViewModel(get())
    }
    viewModel {
        FavoriteViewModel(get())
    }
}

val singleTonModule  = module {
    single {
        DataModelManager()
    }
    single {
        FireBaseAuthManager()
    }
    single {
        BiometricAuthManager()
    }
    single {
        ApplicationPreference(get())
    }
}

val apexExplorerApplicationModule = listOf(
    viewModelModule,
    singleTonModule
)