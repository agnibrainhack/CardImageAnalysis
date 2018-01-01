package com.example.root.firebasetest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.firebasetest.data.model.Post;
import com.example.root.firebasetest.data.model.SendData;
import com.example.root.firebasetest.data.remote.APIService;
import com.example.root.firebasetest.data.remote.ApiUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private Button mPhotoPickerButton;
    CoordinatorLayout coordinatorLayout;
    RelativeLayout relativeLayout;

    private Button logbutton;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    private String mUsername;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStoragerefeence;
    private APIService mAPIService;
    private ProgressDialog progressDialog;
    private String DataTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = (RelativeLayout)findViewById(R.id.relative);
        mUsername = ANONYMOUS;
        DataTransfer = " ";
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        // Initialize references to views
        mChatPhotosStoragerefeence = mFirebaseStorage.getReference().child("chat_photos");
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        logbutton = (Button) findViewById(R.id.login);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (Button) findViewById(R.id.photoPickerButton);


        mPhotoPickerButton.setVisibility(View.INVISIBLE);




        // Initialize message ListView and its adapter
        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete Action Using "), RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there's text to send

        // Send button sends a message and clears the EditText



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    //Toast.makeText(MainActivity.this,"You signed In! wow",Toast.LENGTH_LONG).show();
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().
                            setIsSmartLockEnabled(false).setProviders(AuthUI.GOOGLE_PROVIDER)
                            .build(), RC_SIGN_IN);
                }
            }
        };


    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        attachDatabaseReadListener();

    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseReadListener();

    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {

            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    mMessageAdapter.add(friendlyMessage);
                    mMessageListView.post(new Runnable() {
                        @Override
                        public void run() {
                            mMessageListView.setSelection(mMessageAdapter.getCount() - 1);
                        }
                    });
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "You signed in Successfully", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "You couldn't sign in", Toast.LENGTH_LONG).show();
                finish();
            }
        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Log.d("MainActivity", "Wanted to check if this is fired");
            StorageReference photoRef = mChatPhotosStoragerefeence.child(selectedImageUri.getLastPathSegment());
            mProgressBar.setVisibility(View.VISIBLE);
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressBar.setVisibility(View.GONE);
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //Toast.makeText(MainActivity.this, downloadUrl.toString(), Toast.LENGTH_LONG).show();
   //hey insert here SendData sendObject = new SendData();
                    //init();
                    FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUrl.toString());
                    mMessagesDatabaseReference.push().setValue(friendlyMessage);
                    calltheapi(downloadUrl.toString());                //do something here
                }
            });

        }

    }
    void calltheapi(String download){
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mAPIService = ApiUtils.getAPIService();
        SendData sendObject = new SendData();
        sendObject.setProductTitle("title");
        sendObject.setProductDiscount(download);
        sendObject.setProductStore("Amazon");
        sendPost(sendObject);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null && isNetworkAvailable()) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

        detachDatabaseReadListener();
        mMessageAdapter.clear();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkAvailable()) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
            mPhotoPickerButton.setVisibility(View.VISIBLE);
            logbutton.setVisibility(View.GONE);

        } else
            Toast.makeText(MainActivity.this, "No Connection Available", Toast.LENGTH_LONG).show();
    }

    public void sendPost(SendData title) {
        mAPIService.savePost(title).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if(response.isSuccessful()) {

                    Log.i(TAG, "post submitted to API." + response.body().toString());
                  //show the respose here by calling showresponse func
                    mProgressBar.setVisibility(View.GONE);
                    DataTransfer = response.body().getImageResult();
                    //showResponse(response.body().getImageResult());
                    FriendlyMessage friendlyMessage = new FriendlyMessage(DataTransfer,null,null);
                    mMessageAdapter.add(friendlyMessage);
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e(TAG, "Unable to get response from Server.");
                mProgressBar.setVisibility(View.GONE);
                showResponse("Unable to get response from Server.");
            }
        });
    }

    public void showResponse(String response) {
        Snackbar snackbar = Snackbar.make(relativeLayout, response, Snackbar.LENGTH_LONG);
        snackbar.show();
        //Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
        mProgressBar.setVisibility(View.GONE);

    }



    public void startprocess(View view) {
        onResume();
    }


}