package com.femi.e_class.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference

interface HomeActivityRepository {
    fun getAuthReference(): FirebaseAuth
    fun getCollectionReference(): CollectionReference
}