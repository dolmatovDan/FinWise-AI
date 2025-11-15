package com.spbsu_team7.finwise.core.session

import android.util.Log
import com.spbsu_team7.finwise.core.repository.Repository
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val repositoryProvider: Provider<Repository>
) {
    private var currentRepository: Repository? = null
    private var refCount = 0

    @Synchronized
    fun getOrCreateRepository(): Repository {
        refCount++
        if (currentRepository == null) {
            currentRepository = repositoryProvider.get()
            Log.d("sessionManager", "Repository CREATED (refs: $refCount)")
        }
        return currentRepository!!
    }

    @Synchronized
    fun releaseRepository() {
        refCount--
        if (refCount <= 0) {
            currentRepository = null
            Log.d("sessionManager","Repository DESTROYED")
        }
    }
}