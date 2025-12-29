package com.samkit.costcircle.di


import com.samkit.costcircle.data.group.remote.GroupApiService
import com.samkit.costcircle.data.group.repository.GroupRepository
import com.samkit.costcircle.ui.screens.GlobalActivityFeed.GlobalActivityViewModel
import com.samkit.costcircle.ui.screens.addExpense.AddExpenseViewModel
import com.samkit.costcircle.ui.screens.groupdetails.GroupDetailsViewModel
import com.samkit.costcircle.ui.screens.groupsList.GroupsViewModel
import com.samkit.costcircle.ui.screens.newGroupAddition.viewModels.NewGroupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val groupsModule = module {

    // API
    single<GroupApiService> {
        get<Retrofit>().create(GroupApiService::class.java)
    }

    // Repository
    single {
        GroupRepository(
            api = get(),
            sessionManager = get()
        )
    }

    // ViewModel
    viewModel {
        GroupsViewModel(
            repository = get()
        )
    }
    viewModel { (groupId: Long) ->
        GroupDetailsViewModel(
            groupId = groupId,
            repository = get(),
            sessionManager = get()
        )
    }
    viewModel {
        NewGroupViewModel(
            repository = get()
        )
    }
    viewModel {
        AddExpenseViewModel(
            get(),get()
        )
    }

    viewModel{
        GlobalActivityViewModel(
            repository = get(),
            sessionManager = get()
        )
    }


}