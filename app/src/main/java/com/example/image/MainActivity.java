package com.example.image;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    Button ch,up;
    ImageView img;
    StorageReference mStorageRef;
    private StorageTask uploadTask;
    public Uri imguri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageRef= FirebaseStorage.getInstance().getReference("Images");


        ch=(Button)findViewById(R.id.button_choose_image);
        up=(Button)findViewById(R.id.button_upload);
        img=(ImageView)findViewById(R.id.image_view);
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileChooser();
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask!=null && uploadTask.isInProgress()){
                    Toast.makeText(MainActivity.this,"Upload in progress ",Toast.LENGTH_LONG).show();
                }else
                Fileuploader();
            }
        });

    }

private String getExtension(Uri uri)
{
    ContentResolver cr=getContentResolver();
    MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
    return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
}

private void Fileuploader()
{
StorageReference Ref=mStorageRef.child(System.currentTimeMillis()+"."+getExtension(imguri));

    uploadTask= Ref.putFile(imguri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    Toast.makeText(MainActivity.this,"Image Uploaded successfully ",Toast.LENGTH_LONG).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    // ...
                }
            });
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imguri=data.getData();
            img.setImageURI(imguri);

        }
    }

    private void FileChooser()
    {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
}
