package com.di7ak.openspaces.di

import android.content.Context
import com.di7ak.openspaces.data.local.AppDatabase
import com.di7ak.openspaces.data.local.AuthDao
import com.di7ak.openspaces.data.remote.AuthDataSource
import com.di7ak.openspaces.data.remote.AuthService
import com.di7ak.openspaces.data.remote.LentaDataSource
import com.di7ak.openspaces.data.remote.LentaService
import com.di7ak.openspaces.data.repository.AuthRepository
import com.di7ak.openspaces.data.repository.LentaRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://spaces.im")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()

            val newRequest = request.newBuilder()
                .addHeader("X-Proxy", "spaces")
                .build()
            chain.proceed(newRequest)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectionSpecs(
            Collections.singletonList(
                ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                    .tlsVersions(TlsVersion.TLS_1_0)
                    .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                    )
                    .allEnabledTlsVersions()
                    .allEnabledCipherSuites()
                    .build()
            )
        ).build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun provideAuthDataSource(authService: AuthService) = AuthDataSource(authService)

    @Provides
    fun provideLentaService(retrofit: Retrofit): LentaService =
        retrofit.create(LentaService::class.java)

    @Singleton
    @Provides
    fun provideLentaDataSource(lentaService: LentaService) = LentaDataSource(lentaService)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideAuthDao(db: AppDatabase) = db.authDao()

    @Singleton
    @Provides
    fun provideAuthRepository(
        remoteDataSource: AuthDataSource,
        localDataSource: AuthDao
    ) =
        AuthRepository(remoteDataSource, localDataSource)

    @Singleton
    @Provides
    fun provideLentaRepository(
        remoteDataSource: LentaDataSource
    ) =
        LentaRepository(remoteDataSource)
}