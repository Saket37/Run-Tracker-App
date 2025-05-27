package com.example.auth.data

import com.example.auth.domain.AuthRepository
import com.example.core.data.networking.post
import com.example.core.domain.AuthInfo
import com.example.core.domain.SessionStorage
import com.example.core.domain.util.DataError
import com.example.core.domain.util.EmptyResult
import com.example.core.domain.util.Result
import com.example.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
) : AuthRepository {
    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email,
                password = password
            )
        )
    }

    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        val result = Result.Success(
            LoginResponse(accessToken = "abc", refreshToken = "abc", userId = "test", accessTokenExpirationTimestamp = "")
        )

//            httpClient.post<LoginRequest, LoginResponse>(
//            route = "/login",
//            body = LoginRequest(
//                email = email,
//                password = password
//            )
//        )
            sessionStorage.set(
                AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId
                )
            )

        return result.asEmptyDataResult()
    }

}