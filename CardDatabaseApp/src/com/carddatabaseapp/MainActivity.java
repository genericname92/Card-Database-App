package com.carddatabaseapp;

import java.io.File;
import java.io.FileOutputStream;

import com.example.carddatabaseapp.R;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity implements OnClickListener, PictureCallback
{
	CameraSurfaceView cameraSurfaceView;
	Button shutterButton;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		// set up our preview surface
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		cameraSurfaceView = new CameraSurfaceView(this);
		preview.addView(cameraSurfaceView);

		// grab out shutter button so we can reference it later
		shutterButton = (Button) findViewById(R.id.shutter_button);
		shutterButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_camera, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		takePicture();
	}

	private void takePicture()
	{
		shutterButton.setEnabled(false);
		cameraSurfaceView.takePicture(this);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera)
	{
		// Create folder in external storage for us to store things in
		// Check if SD card is mounted
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		{
			File Dir = new File(android.os.Environment.getExternalStorageDirectory(), "CardDatabaseApp");
			if (!Dir.exists()) // if directory is not here
			{
				Dir.mkdirs(); // make directory
			}
		}
		
		// Save image to external storage as JPEG
		File photo = new File(Environment.getExternalStorageDirectory() + "/CardDatabaseApp/", "photo.jpg");

		if (photo.exists())
		{
			photo.delete();
		}

		try
		{
			FileOutputStream fos = new FileOutputStream(photo.getPath());

			fos.write(data);
			fos.close();
		}
		catch (java.io.IOException e)
		{
			Log.d(Constants.LOG, "Error saving image.");
		}

		// Restart the preview and re-enable the shutter button so that we can take another picture
		camera.startPreview();
		shutterButton.setEnabled(true);
	}
}