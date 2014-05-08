package com.example.takephoto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.MediaStore;

@SuppressLint("ValidFragment")
public class MainActivity extends ActionBarActivity {

	private static final int REQUEST_CODE_PHOTO = 566;
	private ImageView imageView;
	private TextView textView;
	private Uri outputFileUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		Parse.initialize(this, "lUQbf1G96D88I6KY4h5mFT9vN5v4PZjLBBJrPzp1", "cZKXe4pLBZ9wNHk8k4WreS7n2Mj1ZVjeOSj3hCId");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*if (id == R.id.action_settings) {
			return true;
		}*/
		if (id == R.id.action_photo) {
			Log.d("debug", "action photo");
			
			outputFileUri = Uri.fromFile(getTargetFile());
			
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			//startActivity(intent);
			startActivityForResult(intent, REQUEST_CODE_PHOTO);
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d("debug", "onActivityResult");
		
		if (requestCode == REQUEST_CODE_PHOTO) {
			if (resultCode == RESULT_OK) {
				// 當intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)時，intent.getParcelableExtra("data")不可使用，data會無資料
				/*Bitmap bitmap = intent.getParcelableExtra("data");
				imageView.setImageBitmap(bitmap);
				saveImage(bitmap);*/
				
				imageView.setImageURI(outputFileUri);
				textView.setText(outputFileUri.getPath());
				saveToParse();
				Log.d("debug", "OK");
			} else if (resultCode == RESULT_CANCELED) {
				Log.d("debug", "CANCELED");
			} else {
				Log.d("debug", "ELSE");
			}
		}
	}
	
	private File getTargetFile() {
		File imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (!imageDir.exists())
			imageDir.mkdirs();
		
		return new File(imageDir, "photo.png");
	}
	
	private void saveToParse() {
		File file = getTargetFile();
		byte[] data = new byte[(int) file.length()];
		
		try {
			FileInputStream fis = new FileInputStream(file);
			fis.read(data);
			
			int byteOffset = 0;
			int byteCount = 0;
			while ((byteCount = fis.read(data, byteOffset, data.length-byteOffset)) != -1)
				byteOffset += byteCount;
			
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final ParseFile parseFile = new ParseFile("photo.png", data);
		ParseObject parseObject = new ParseObject("photos");
		parseObject.put("file", parseFile);
		parseFile.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				Log.d("debug", parseFile.getUrl());
			}
		});
	}
	
	private void saveImage(Bitmap bitmap) {		
		File imageFile = getTargetFile();
		try {
			FileOutputStream fos = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		textView.setText(imageFile.getPath());
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			imageView = (ImageView) rootView.findViewById(R.id.imageView1);
			textView = (TextView) rootView.findViewById(R.id.textView1);
			return rootView;
		}
	}

}
