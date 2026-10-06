package com.example.oralpred

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    const val URL = "https://eaiyowsdnlqslovwwxgt.supabase.co"
    const val KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVhaXlvd3Nkbmxxc2xvdnd3eGd0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODAxMzI4MjgsImV4cCI6MjA5NTcwODgyOH0.eARiXqU0dtiT6R_aFGnctwhf_oRCB4d1dmG9tMW-zJ8"

    val client = createSupabaseClient(
        supabaseUrl = URL,
        supabaseKey = KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}
