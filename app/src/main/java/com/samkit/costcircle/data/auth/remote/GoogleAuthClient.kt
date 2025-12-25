package com.samkit.costcircle.data.auth.remote

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleAuthClient(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Configure the Google Sign-In Request
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false) // Show all Google accounts, not just previously signed-in ones
                    .setServerClientId("1074495736310-cp1o1obftp2d8i0obplq4999o2cmoml9.apps.googleusercontent.com") // <--- PASTE WEB CLIENT ID HERE
                    .setAutoSelectEnabled(true) // Auto-select if only one account exists
                    .build()

                // 2. Build the generic Credential Request
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // 3. Launch the selector and wait for user input
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                // 4. Extract the ID Token from the result
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                    // This is the token you send to your backend!
                    googleIdTokenCredential.idToken
                } else {
                    Log.e("GoogleAuth", "Unexpected credential type")
                    null
                }
            } catch (e: GetCredentialException) {
                Log.e("GoogleAuth", "Sign in failed: ${e.message}")
                e.printStackTrace()
                null
            } catch (e: Exception) {
                Log.e("GoogleAuth", "Unknown error: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }
}