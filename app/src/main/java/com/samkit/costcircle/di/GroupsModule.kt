package com.samkit.costcircle.di


import com.samkit.costcircle.data.group.Repository.GroupRepository
import com.samkit.costcircle.data.group.remote.groupApiService
import com.samkit.costcircle.ui.screens.groups.viewModels.GroupsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val groupsModule = module {

    // API
    single<groupApiService> {
        get<Retrofit>().create(groupApiService::class.java)
    }

    // Repository
    single {
        GroupRepository(
            api = get()
        )
    }

    // ViewModel (MVI)
    viewModel {
        GroupsViewModel(
            repository = get()
        )
    }
}