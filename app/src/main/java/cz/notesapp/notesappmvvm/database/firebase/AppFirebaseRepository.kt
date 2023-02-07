package cz.notesapp.notesappmvvm.database.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import cz.notesapp.notesappmvvm.database.DatabaseRepository
import cz.notesapp.notesappmvvm.model.Note
import cz.notesapp.notesappmvvm.utils.Constants
import cz.notesapp.notesappmvvm.utils.FIREBASE_ID
import cz.notesapp.notesappmvvm.utils.LOGIN
import cz.notesapp.notesappmvvm.utils.PASSWORD

class AppFirebaseRepository : DatabaseRepository {

    private val mAuth = FirebaseAuth.getInstance()
    private val  database = Firebase.database.reference
        .child(mAuth.currentUser?.uid.toString())

    override val readAll: LiveData<List<Note>> = AllNotesLiveData()

    override suspend fun create(note: Note, onSuccess: () -> Unit) {
        val noteId = database.push().key.toString()
        val mapNotes = hashMapOf<String, Any>()

        mapNotes[FIREBASE_ID] = noteId
        mapNotes[Constants.Keys.TITLE] = note.title
        mapNotes[Constants.Keys.SUBTITLE] = note.subtitle

        database.child(noteId)
            .updateChildren(mapNotes)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { Log.d("checkData", "Failed to add new note") }
    }

    override suspend fun update(note: Note, onSuccess: () -> Unit) {
        val noteId = note.firebaseId
        val mapNotes = hashMapOf<String, Any>()

        mapNotes[FIREBASE_ID] = noteId
        mapNotes[Constants.Keys.TITLE] = note.title
        mapNotes[Constants.Keys.SUBTITLE] = note.subtitle

        database.child(noteId)
            .updateChildren(mapNotes)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { Log.d("checkData", "Failed to update note") }
    }

    override suspend fun delete(note: Note, onSuccess: () -> Unit) {
        database.child(note.firebaseId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { Log.d("checkData", "Failed to delete note") }
    }

    override fun signOut() {
        mAuth.signOut()
    }

    override fun connectToDatabase(onSuccess: () -> Unit, onFail: (String) -> Unit) {
       mAuth.signInWithEmailAndPassword(LOGIN, PASSWORD)
           .addOnSuccessListener { onSuccess() }
           .addOnFailureListener {
               mAuth.createUserWithEmailAndPassword(LOGIN, PASSWORD)
                   .addOnSuccessListener { onSuccess() }
                   .addOnFailureListener { onFail(it.message.toString()) }
           }
    }
}