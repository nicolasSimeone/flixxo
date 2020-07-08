package com.flixxo.apps.flixxoapp.di

import com.flixxo.apps.flixxoapp.repositories.*
import com.flixxo.apps.flixxoapp.repositories.local.PreferencesManager
import com.flixxo.apps.flixxoapp.repositories.local.db.AppDatabase
import com.flixxo.apps.flixxoapp.repositories.remote.service.*
import com.flixxo.apps.flixxoapp.viewModel.*
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


val loginModule = module {
    viewModel { LoginViewModel(get(), get(USER_REPOSITORY), get()) }
    single(name = LOGIN_REPOSITORY) { LoginRepository(get()) }
    single(name = LOGIN_FACEBOOK_REPOSITORY) { LoginFacebookRepository(get()) }
    single(name = LOGIN_SERVICE) {
        LoginService(
            (get(API_CLIENT) as ApiClient),
            (get(PREFERENCE_MANAGER) as PreferencesManager)
        )
    }
    single(name = LOGIN_FACEBOOK_SERVICE) {
        LoginFacebookService(
            (get(API_CLIENT) as ApiClient),
            (get(PREFERENCE_MANAGER) as PreferencesManager)
        )
    }
}

val registerModule = module {
    viewModel { RegisterViewModel(get(REGISTER_REPOSITORY)) }
    single(name = REGISTER_REPOSITORY) { RegisterRepository(get()) }
    single(name = REGISTER_SERVICE) {
        RegisterService(
            (get(API_CLIENT) as ApiClient),
            (get(PREFERENCE_MANAGER) as PreferencesManager)
        )
    }
}

val contentModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { DetailViewModel(get(), get(), get(POPUP_REPOSITORY), get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { OBSecondStepViewModel(get()) }
    viewModel { AdPlayerViewModel(get()) }
    viewModel { MainViewModel(get(), get()) }
    single(name = CONTENT_REPOSITORY) { ContentRepository(get(), get()) }
    single(name = CATEGORY_REPOSITORY) { CategoriesRepository(get()) }
    single(name = ADVERTISEMENT_REPOSITORY) { AdvertisementRepository(get()) }
    single(name = MAIN_REPOSITORY) { MainRepository(get()) }
    single(name = CONTENT_SERVICE) { ContentService((get(API_CLIENT) as ApiClient)) }
    single(name = CATEGORY_SERVICE) { CategoriesService((get(API_CLIENT) as ApiClient)) }
    single(name = ADVERTISEMENT_SERVICE) {
        AdvertisementService(
            (get(API_CLIENT) as ApiClient),
            (get(PREFERENCE_MANAGER) as PreferencesManager)
        )
    }
    single(name = MAIN_SERVICE) {
        MainService(
            (get(API_CLIENT) as ApiClient),
            get(PREFERENCE_MANAGER) as PreferencesManager
        )
    }
}

val torrentModule = module {
    viewModel { TorrentStreamingViewModel(get(), get()) }
    single(name = TORRENT_REPOSITORY) { TorrentRepository(get()) }
    single(name = TORRENT_SERVICE) { TorrentService((get(API_CLIENT) as ApiClient)) }
}


val popupModule = module {
    viewModel { PopupViewModel(get(), get()) }
    single(name = POPUP_REPOSITORY) { PaymentsRepository(get()) }
    single(name = POPUP_SERVICE) { PaymentsContentService((get(API_CLIENT) as ApiClient)) }
}

val userModule = module {
    viewModel { AccountViewModel(get(), get(PREFERENCE_MANAGER)) }
    viewModel { ResetPasswordViewModel(get()) }
    viewModel { EditProfileViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { DepositViewModel(get()) }
    viewModel { FirstStepViewModel(get(), get()) }
    viewModel { UserProfileViewModel(get(), get(PREFERENCE_MANAGER), get(), get()) }
    single(name = USER_REPOSITORY) { UserRepository(get()) }
    single(name = COUNTRY_REPOSITORY) { CountryRepository(get()) }
    single(name = USER_SERVICE) {
        UserService(
            (get(API_CLIENT) as ApiClient),
            get(PREFERENCE_MANAGER) as PreferencesManager
        )
    }
    single(name = COUNTRY_SERVICE) { CountryService((get(API_CLIENT) as ApiClient)) }
}


val apiModule = module {
    single(name = API_CLIENT) { ApiClientService.create(get()) }
    single { AuthInterceptor(get()) }
    single(name = PREFERENCE_MANAGER) { PreferencesManager.getInstance(get()) }
}

val dbModule = module {
    single(name = ROOM_INSTANCE) { AppDatabase.getDatabase(get()) }
    single(name = CATEGORY_DAO) { (get(ROOM_INSTANCE) as AppDatabase).categoryDao() }
    single(name = LANGUAGE_DAO) { (get(ROOM_INSTANCE) as AppDatabase).languageDao() }
}

val mailModule = module {
    single(name = MAIL_REPOSITORY) { MailRepository(get(MAIL_SERVICE)) }
    single(name = MAIL_SERVICE) { MailService(get(API_CLIENT), get(PREFERENCE_MANAGER)) }
}

val codeModule = module {
    viewModel { ConfirmCodeViewModel(get(), get(PREFERENCE_MANAGER)) }
    single(name = CODE_REPOSITORY) { ConfirmCodeRepository(get()) }
    single(name = CODE_SERVICE) { ConfirmCodeService(get(API_CLIENT), get(PREFERENCE_MANAGER)) }
}

private const val LOGIN_REPOSITORY = "LOGIN_REPOSITORY"
private const val LOGIN_SERVICE = "LOGIN_SERVICE"
private const val PREFERENCE_MANAGER = "PREFERENCE_MANAGER"

private const val LOGIN_FACEBOOK_REPOSITORY = "LOGIN_FACEBOOK_REPOSITORY"
private const val LOGIN_FACEBOOK_SERVICE = "LOGIN_FACEBOOK_SERVICE"

private const val REGISTER_REPOSITORY = "REGISTER_REPOSITORY"
private const val REGISTER_SERVICE = "REGISTER_SERVICE"

private const val CONTENT_REPOSITORY = "CONTENT_REPOSITORY"
private const val CONTENT_SERVICE = "CONTENT_SERVICE"
private const val CATEGORY_REPOSITORY = "CATEGORY_REPOSITORY"
private const val CATEGORY_SERVICE = "CATEGORY_SERVICE"

private const val TORRENT_REPOSITORY = "TORRENT_REPOSITORY"
private const val TORRENT_SERVICE = "TORRENT_SERVICE"


private const val COUNTRY_REPOSITORY = "COUNTRY_REPOSITORY"
private const val COUNTRY_SERVICE = "COUNTRY_SERVICE"

private const val MAIN_REPOSITORY = "MAIN_REPOSITORY"
private const val MAIN_SERVICE = "MAIN_SERVICE"

private const val ADVERTISEMENT_REPOSITORY = "ADVERTISEMENT_REPOSITORY"
private const val ADVERTISEMENT_SERVICE = "ADVERTISEMENT_SERVICE"

private const val POPUP_REPOSITORY = "POPUP_REPOSITORY"
private const val POPUP_SERVICE = "POPUP_SERVICE"

private const val USER_REPOSITORY = "USER_REPOSITORY"
private const val USER_SERVICE = "USER_SERVICE"
private const val LOCAL_SOURCE = "LOCAL_SOURCE"

private const val API_CLIENT = "API_CLIENT"
private const val ROOM_INSTANCE = "ROOM_INSTANCE"
private const val CATEGORY_DAO = "CATEGORY_DAO"
private const val LANGUAGE_DAO = "LANGUAGE_DAO"

private const val MAIL_SERVICE = "MAIL_SERVICE"
private const val MAIL_REPOSITORY = "MAIL_REPOSITORY"

private const val CODE_REPOSITORY = "CODE_REPOSITORY"
private const val CODE_SERVICE = "CODE_SERVICE"

