package com.royalmanncreations.docscanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private Uri uriFilePath;
    private ImageView img_camera, img_result;
    public static final int PERMISSIONS_REQUEST_CODE = 1;
    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private File rootDirectory = Environment.getExternalStorageDirectory();
    private long timeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (uriFilePath == null && savedInstanceState.getString("uri_file_path") != null) {
                uriFilePath = Uri.parse(savedInstanceState.getString("uri_file_path"));
            }
        }
        setContentView(R.layout.activity_main);
        img_camera = findViewById(R.id.img_camera);
        img_result = findViewById(R.id.img_result);
        if (check_permission_granted()) {
            img_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    PackageManager packageManager = getPackageManager();
                    if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                        File mainDirectory = new File(rootDirectory, "DocScanner/img");
                        if (!mainDirectory.exists())
                            mainDirectory.mkdirs();

                        Calendar calendar = Calendar.getInstance();
                        timeInMillis = calendar.getTimeInMillis();
                        uriFilePath = Uri.fromFile(new File(mainDirectory, "IMG_" + timeInMillis + ".jpeg"));
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFilePath);
                        startActivityForResult(intent, 1);
                    }
                }
            });
        } else {
            check_permission_granted();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (uriFilePath != null)
            outState.putString("uri_file_path", uriFilePath.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String filePath = uriFilePath.getPath();
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriFilePath);
                    convertToPdf(filePath);
                    img_result.setImageBitmap(mImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void convertToPdf(String filePath) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile("/" + filePath);

            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(960, 1280, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(myPageInfo);

            page.getCanvas().drawBitmap(bitmap, 0, 0, null);
            pdfDocument.finishPage(page);
            File pdfRoot = new File(rootDirectory, "DocScanner/pdf");
            if (!pdfRoot.exists()) {
                pdfRoot.mkdirs();
            }
            File myPDFFile = new File(pdfRoot, "/IMG_" + timeInMillis + ".pdf");

            try {
                pdfDocument.writeTo(new FileOutputStream(myPDFFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    boolean check_permission_granted() {
        // check permission Are granted
        ArrayList<String> permission_list = new ArrayList<String>();
        for (String s : permission) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                permission_list.add(s);
            }
        }
        if (!permission_list.isEmpty()) {
            ActivityCompat.requestPermissions(this, permission_list.toArray(new String[permission_list.size()]), PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            HashMap<String, Integer> permissionResult = new HashMap<>();
            int denied_count = 0;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResult.put(permissions[i], grantResults[i]);
                    denied_count++;
                }
            }
            if (denied_count == 0) {
//                check_credential();
            } else {
                for (Map.Entry<String, Integer> entry : permissionResult.entrySet()) {
                    String prem_name = entry.getKey();
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, prem_name)) {
                        showDialog("'Troop Tracker' needs permissions to run properly.\nPlease allow them");
                    } else {
                        explainDialog("Permissions are required to run App.Go to app settings.");
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showDialog(String s) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(s)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        check_permission_granted();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                }).setCancelable(false);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#0037FF"));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#D81B60"));
            }
        });
        alertDialog.show();
    }

    private void explainDialog(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                }).setCancelable(false);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#0037FF"));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#D81B60"));
            }
        });
        alertDialog.show();
    }

}