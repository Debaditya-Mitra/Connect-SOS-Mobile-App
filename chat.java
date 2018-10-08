package com.chatdemo;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.location.LocationManager.*;

public class chat extends AppCompatActivity implements OnItemClickInterface {
    private final String baseUrl = "https://androidchatapp-168d7.firebaseio.com/";
    LinearLayout layout;
    ImageView sendButton;
    ImageView attachButton, takePhoto;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    ArrayList<MessageVo> messageList;

    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    /////////////////////////////////////////////////////////////////////////
    private int REQUEST_MAP = 0;
    private String label;

    private String messageTableName = "messages/";
    private String userChoosenTask, TAG = "chat";
    static final int REQUEST_GALLERY_IMAGE = 100;
    static final int REQUEST_CODE_PICK_ACCOUNT = 101;
    static final int REQUEST_ACCOUNT_AUTHORIZATION = 102;
    static final int REQUEST_PERMISSIONS = 13;
    private static String accessToken;
    private TextView labelResults;
    private ImageView selectedImage;
    private int latitude;
    private int longitude;

    private ImageView receivedImage;
    private TextView textResults;
    ///////////////////////////////////////////////////////////////////////
    private MapView mapSend;
    private MapView mapReceived;
    private Account mAccount;
    File destination;
    private RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    private RecyclerView rvMessageList;

    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private OnItemClickInterface onItemClickInterface;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        messageList = new ArrayList<>();

        rvMessageList = findViewById(R.id.rvMessageList);

        layout = (LinearLayout) findViewById(R.id.layout1);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        takePhoto = (ImageView) findViewById(R.id.takePhoto);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        attachButton = (ImageView) findViewById(R.id.attachButton);
        //selectedImage=findViewById(R.id.img_info);
        //labelResults=findViewById(R.id.txt_label_results);
        //textResults=findViewById(R.id.txt_texts_results);
        Firebase.setAndroidContext(this);
        reference1 = new Firebase(baseUrl + messageTableName + UserDetails.userId + "_" + UserDetails.chatWith);
        reference2 = new Firebase(baseUrl + messageTableName + UserDetails.chatWith + "_" + UserDetails.userId);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        messageAdapter = new MessageAdapter(this, messageList, this);
        rvMessageList.setLayoutManager(new LinearLayoutManager(this));
        rvMessageList.setAdapter(messageAdapter);


        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.checkPermission(chat.this))
                    selectImage();
            }


        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("userId", UserDetails.userId);
                    List<String> list = new ArrayList<String>(map.values());
                    // map.put("userName", UserDetails.userId);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                }
                messageArea.setText("");
            }
        });


        reference1.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                Log.d("Map", map.toString());
                String message = map.get("message").toString();


                MessageVo messageVo = new MessageVo();
                messageVo.setUserId(map.get("userId").toString());
                messageVo.setMessage(map.get("message").toString());

                messageList.add(messageVo);


                if (messageList != null && messageList.size() > 1)
                    rvMessageList.smoothScrollToPosition(messageList.size() - 1);


                messageAdapter.notifyDataSetChanged();


            /*    if(userId.equals(UserDetails.userId)){
                    addMessageBox("You:-\n" + message, 1);
                }
                else{
                    addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
                }*/
            }
                /*Set set = map.entrySet();
                Iterator i = set.iterator();
                while(i.hasNext()) {
                    Map.Entry me = (Map.Entry)i.next();
                    MessageVo msg = new MessageVo();
                    msg.setUserId(String.valueOf(me.getKey()));
                    msg.setMessageList(String.valueOf(me.getValue()));
                    arrayList.add(msg);
                }*/
               /* for(int j=0;j<arrayList.size();j++){
                    Log.d("ArrayList User Data : ",arrayList.get(j).getUserId());
                    Log.d("ArrayList User Message : ",arrayList.get(j).getMessageList());
                }*/
             /*   MessageAdapter msgAdapter = new MessageAdapter(getApplicationContext(), arrayList, new OnItemClickInterface() {
                    @Override
                    public void onItemClick(int position, UserVo userVo) {*/

            /*           }
                   });
               }
   */
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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(chat.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 5, 10, 5);
        textView.setPadding(10, 10, 10, 10);
        textView.setLayoutParams(lp);
        if (type == 1) {
            textView.setBackgroundResource(R.drawable.send_mgs);

        } else {
            textView.setBackgroundResource(R.drawable.receive_msg);
        }


        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);

    }


    private void selectImage() {
        final String[] items = {"Take Photo", "From Gallery", "View Map"};
        AlertDialog.Builder builder = new AlertDialog.Builder(chat.this);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(chat.this);
userChoosenTask="";
                if (items[item].equalsIgnoreCase("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equalsIgnoreCase("From Gallery")) {
                    userChoosenTask = "From Gallery";
                    if (result)
                        galleryIntent();
                } else if (items[item].equalsIgnoreCase("View Map")) {
                    userChoosenTask = "View Map";
                    if (result) {
                      mapIntent();

                    }

                }
            }
        });
        builder.show();
    }

    private void mapIntent() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=23.0534301,72.5195592");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        }




    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:/*
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }*/
                break;
            case Utility.MY_PERMISSIONS_REQUEST_CAMERA:
              /*  if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }*/
                break;
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAuthToken();
                } else {
                    Toast.makeText(chat.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

             if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
                if (resultCode == RESULT_OK) {
                    String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    AccountManager am = AccountManager.get(this);
                    Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                    for (Account account : accounts) {
                        if (account.name.equals(email)) {
                            mAccount = account;
                            break;
                        }
                    }
                    getAuthToken();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "No Account Selected", Toast.LENGTH_SHORT)
                            .show();

                }
            } else if (requestCode == REQUEST_ACCOUNT_AUTHORIZATION) {
                if (resultCode == RESULT_OK) {
                    Bundle extra = data.getExtras();
                    onTokenReceived(extra.getString("authtoken"));
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Authorization Failed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

        /*Uri uri = data.getData();
        try {
            Bitmap picture = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            MessageVo messageVo = new MessageVo.Builder();
            messageVo.setPicture(picture);
            messageVo.build();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
*/
    }


     /*   if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }*/

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

            Log.e("chat", destination.getPath());
            Log.e("chat", "" + data.getExtras().get("data"));

            Uri url = getImageUri(this, thumbnail);
            uploadImage(url);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ivImage.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                uploadImage(data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //   ivImage.setImageBitmap(bm);
    }


    private void uploadImage(Uri filepath) {
        if (filepath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/*" + UUID.randomUUID().toString());

            //uploading the image
            UploadTask uploadTask = ref.putFile(filepath);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        // Toast.makeText(chat.this, "Upload Failed -> " + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.e("Task Download Url :", task.getException().toString());
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    //Toast.makeText(chat.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    Log.e("Task Download Url :", ref.getDownloadUrl().toString());
                    progressDialog.dismiss();
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e("Url :", downloadUri.toString());

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("message", downloadUri.toString());
                        map.put("userId", UserDetails.userId);
                        List<String> list = new ArrayList<String>(map.values());
                        // map.put("userName", UserDetails.userId);
                        reference1.push().setValue(map);
                        reference2.push().setValue(map);

                        Toast.makeText(chat.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(chat.this, "Upload Failed -> " + task.getException(), Toast.LENGTH_SHORT).show();
                        // Handle failures
                        // ...
                    }
                }
            });


          /*  uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(chat.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    Uri downloaduri = taskSnapshot.getUploadSessionUri();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(chat.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });*/

           /* ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            StorageMetadata downloadUrl = taskSnapshot.getMetadata();


                            //Picasso.with(chat.this).load(String.valueOf(downloadUrl)).fit().centerCrop().into((Target) downloadUrl);
                            progressDialog.dismiss();
                            Toast.makeText(chat.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(chat.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            progressDialog.dismiss();

                        }
                    });*/
        }
    }

    @Override
    public void onItemClick(int position, UserVo userVo) {

    }

    String selectedImageUrl = "";

    String receivedImageUrl="";


    @Override
    public void onItemClick(String imageUrl) {
        selectedImageUrl = imageUrl;

        ActivityCompat.requestPermissions(chat.this,
                new String[]{Manifest.permission.GET_ACCOUNTS},
                REQUEST_PERMISSIONS);

    }
    Bitmap bitmapImage;
    public void performCloudVisionRequest(String uri) {
        if (uri != null) {

                Target mTarget = new Target() {
                    @Override
                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){


                        selectedImage.setImageBitmap(bitmap);
                        receivedImage.setImageBitmap(bitmap);

                        bitmapImage = bitmap;
                        try {
                            callCloudVision(bitmapImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                Picasso.with(this)
                        .load(uri)
                        .into(mTarget);


        }
    }




    @SuppressLint("StaticFieldLeak")
    private void callCloudVision(final Bitmap bitmap) throws IOException {
        mProgressDialog = ProgressDialog.show(this, null, "Scanning image using Cloud Vision API...",
                true) ;

        new AsyncTask<Object, Void, BatchAnnotateImagesResponse>() {
            @Override
            protected BatchAnnotateImagesResponse doInBackground(Object... params) {
                try {
                    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder
                            (httpTransport, jsonFactory, credential);
                    Vision vision = builder.build();

                    List<Feature> featureList = new ArrayList<>();
                    Feature labelDetection = new Feature();
                    labelDetection.setType("LABEL_DETECTION");
                    labelDetection.setMaxResults(10);
                    featureList.add(labelDetection);

                    Feature textDetection = new Feature();
                    textDetection.setType("TEXT_DETECTION");
                    textDetection.setMaxResults(10);
                    featureList.add(textDetection);

                    List<AnnotateImageRequest> imageList = new ArrayList<>();
                    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                    Image base64EncodedImage = getBase64EncodedJpeg(bitmap);
                    annotateImageRequest.setImage(base64EncodedImage);
                    annotateImageRequest.setFeatures(featureList);
                    imageList.add(annotateImageRequest);

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(imageList);

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "Sending request to Google Cloud");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return response;

                } catch (GoogleJsonResponseException e) {
                    Log.e(TAG, "Request error: " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "Request error: " + e.getMessage());
                }
                return null;
            }

            protected void onPostExecute(BatchAnnotateImagesResponse response) {
                mProgressDialog.dismiss();
                mProgressDialog.cancel();
                textResults.setText(getDetectedTexts(response));
                labelResults.setText(getDetectedLabels(response));

            }

        }.execute();
    }


    private String getDetectedLabels(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("");
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message.append(String.format(Locale.getDefault(),"%s: %s",
                        label.getScore(), label.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("Nil\n");
        }

        return message.toString();
    }

    private String getDetectedTexts(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("");
        List<EntityAnnotation> texts = response.getResponses().get(0)
                .getTextAnnotations();
        if (texts != null) {
            for (EntityAnnotation text : texts) {
                message.append(String.format(Locale.getDefault(), "%s: %s",
                        text.getLocale(), text.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("Nil\n");
        }

        return message.toString();
    }

    public Bitmap resizeBitmap(Bitmap bitmap) {

        int maxDimension = 1024;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }



    private void pickUserAccount() {
        String[] accountTypes = new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    private void getAuthToken() {
        String SCOPE = "oauth2:https://www.googleapis.com/auth/cloud-platform";
        if (mAccount == null) {
            pickUserAccount();
        } else {
            new GetOAuthToken(this, mAccount, SCOPE, REQUEST_ACCOUNT_AUTHORIZATION)
                    .execute();

           /* try {
                String token = fetchToken();
                if (token != null) {
                    this.onTokenReceived(token);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    Handler handler = new Handler();
    public void onTokenReceived(String token) {
        accessToken = token;
        showDialog();
        //galleryIntent();
        /*Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        showDialog();
                        //progress.setProgress("anything"); // Update the UI
                    }
                });
            }
        };
        new Thread(runnable).start();*/
    }


    public void showDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.img_details);
        selectedImage = dialog.findViewById(R.id.img_info);
        receivedImage=dialog.findViewById(R.id.img_info);
        labelResults = dialog.findViewById(R.id.txt_label_results);
        textResults = dialog.findViewById(R.id.txt_texts_results);
        dialog.show();
        performCloudVisionRequest(selectedImageUrl);

       // performCloudVisionRequest(receivedImageUrl);


    }

    protected String fetchToken() throws IOException {
        String mScope = "oauth2:https://www.googleapis.com/auth/cloud-platform";
        String accessToken;
        try {
            accessToken = GoogleAuthUtil.getToken(this, mAccount, mScope);
            GoogleAuthUtil.clearToken (this, accessToken);
            accessToken = GoogleAuthUtil.getToken(this, mAccount, mScope);
            return accessToken;
        } catch (UserRecoverableAuthException userRecoverableException) {
            this.startActivityForResult(userRecoverableException.getIntent(), REQUEST_ACCOUNT_AUTHORIZATION);
        } catch (GoogleAuthException fatalException) {
            fatalException.printStackTrace();
        }
        return null;
    }




}
