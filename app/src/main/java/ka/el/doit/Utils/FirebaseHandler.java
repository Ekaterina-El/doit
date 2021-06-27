package ka.el.doit.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHandler {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference db = FirebaseFirestore.getInstance().collection("todoLists");

    public CollectionReference getDb() {
        return db;
    }

    public DocumentReference getRefDocById(String id) {
        return db.document(id);
    }

    public FirebaseAuth getMAuth() {
        return mAuth;
    }

    public boolean isLogin() {
        FirebaseUser cu = mAuth.getCurrentUser();
        return cu != null;
    }




}
