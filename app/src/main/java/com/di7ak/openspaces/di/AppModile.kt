package com.di7ak.openspaces.di

import android.content.Context
import com.di7ak.openspaces.data.Session
import com.di7ak.openspaces.data.local.AppDatabase
import com.di7ak.openspaces.data.repository.AssetsRepository
import com.di7ak.openspaces.data.converters.ConfigMapperConverterFactory
import com.di7ak.openspaces.data.local.AuthDao
import com.di7ak.openspaces.utils.AttachmentParser
import com.di7ak.openspaces.utils.HtmlImageGetter
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
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
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(remoteConfig: FirebaseRemoteConfig, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(remoteConfig.getString("base_url"))
            .client(client)
            .addConverterFactory(ConfigMapperConverterFactory.create(remoteConfig))
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
            listOf(
                ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                    .tlsVersions(TlsVersion.TLS_1_0)
                    .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                    )
                    .allEnabledTlsVersions()
                    .allEnabledCipherSuites()
                    .build(),
                ConnectionSpec.CLEARTEXT
            )
        ).build()

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideSession(authDao: AuthDao, @ApplicationContext context: Context) =
        Session(authDao, context)

    @Singleton
    @Provides
    fun provideConfig() = Firebase.remoteConfig

    @Singleton
    @Provides
    fun provideImageGetter(@ApplicationContext context: Context) = HtmlImageGetter(context)

    @Singleton
    @Provides
    fun provideAssets(@ApplicationContext context: Context) = AssetsRepository(context)

    @Singleton
    @Provides
    fun provideAttachmentParser(remoteConfig: FirebaseRemoteConfig) = AttachmentParser(remoteConfig)
}