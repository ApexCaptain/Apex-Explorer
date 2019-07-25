package com.ayteneve93.apexexplorer.di

import com.ayteneve93.apexexplorer.data.managers.AppTitleModelManager
import com.ayteneve93.apexexplorer.data.managers.FileModelManager
import com.ayteneve93.apexexplorer.data.managers.UserAccountInfoModelManager
import com.ayteneve93.apexexplorer.prompt.BiometricAuthPrompt.BiometricAuthManager
import com.ayteneve93.apexexplorer.prompt.FireBaseAuthPrompt.FireBaseAuthManager
import com.ayteneve93.apexexplorer.utils.PreferenceUtils
import com.ayteneve93.apexexplorer.view.entry.EntryViewModel
import com.ayteneve93.apexexplorer.view.main.MainViewModel
import com.ayteneve93.apexexplorer.view.main.PathRecyclerAdapter
import com.ayteneve93.apexexplorer.view.main.PathViewModel
import com.ayteneve93.apexexplorer.view.main.fragments.favorite.FavoriteFileViewModel
import com.ayteneve93.apexexplorer.view.main.fragments.favorite.FavoriteListRecyclerAdapter
import com.ayteneve93.apexexplorer.view.main.fragments.favorite.FavoriteViewModel
import com.ayteneve93.apexexplorer.view.main.fragments.filelist.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { EntryViewModel(get()) }

    viewModel { MainViewModel(get()) }

    viewModel { PathViewModel(get()) }

        viewModel { FileListViewModel(get()) }

            viewModel { FileViewModel(get()) }

        viewModel { FavoriteViewModel(get()) }

            viewModel { FavoriteFileViewModel(get()) }
}

val dataModelManager = module {
    single { AppTitleModelManager(get()) }
    single { UserAccountInfoModelManager() }
    single { FileModelManager(get(), get()) }
}

val promptManagerModule = module {
    single { FireBaseAuthManager() }
    single { BiometricAuthManager() }
}

val utilsModule  = module {
    single { PreferenceUtils(get()) }

}

val listRecyclerAdapter = module {
    single { FileListRecyclerAdapter(get(), get(), get()) }
    single { PathRecyclerAdapter(get()) }
    single { FavoriteListRecyclerAdapter(get(), get(), get()) }
}

val apexExplorerApplicationModule = listOf(
    viewModelModule,
    dataModelManager,
    promptManagerModule,
    utilsModule,
    listRecyclerAdapter
)