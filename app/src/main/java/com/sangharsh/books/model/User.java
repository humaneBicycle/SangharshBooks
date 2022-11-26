package com.sangharsh.books.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class User {
    String uid;
    String name;
    long createdOn;
    String referredBy;
    String referralId;
    String deviceId;

    private int points;

    public int getPoints(){
        if (points > 0){
            return points;
        } else {
            return 0;
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    ArrayList<String> purchasedQuiz = new ArrayList<>();
    ArrayList<String> purchasedDirectories= new ArrayList<>();
    ArrayList<String> purchasedPDFs= new ArrayList<>();


    public User(String uid, long createdOn) {
        this.uid = uid;
        this.createdOn = createdOn;
        points = 0;
    }

    private static User mUser;

    public interface UserListener{
        void onUserFound(User user);
        default void onError(String error){}
    }
    public static void getUser(UserListener listener){
        if (mUser != null) {
            listener.onUserFound(mUser);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isComplete()){
                            mUser = task.getResult().toObject(User.class);
                            listener.onUserFound(mUser);
                        } else {
                            listener.onError(task.getException().getMessage());
                        }
                    }
                });
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public ArrayList<String> getPurchasedQuiz() {
        return purchasedQuiz;
    }

    public void setPurchasedQuiz(ArrayList<String> purchasedQuiz) {
        this.purchasedQuiz = purchasedQuiz;
    }

    public ArrayList<String> getPurchasedDirectories() {
        return purchasedDirectories;
    }

    public void setPurchasedDirectories(ArrayList<String> purchasedDirectories) {
        this.purchasedDirectories = purchasedDirectories;
    }

    public ArrayList<String> getPurchasedPDFs() {
        return purchasedPDFs;
    }

    public void setPurchasedPDFs(ArrayList<String> purchasedPDFs) {
        this.purchasedPDFs = purchasedPDFs;
    }
}
