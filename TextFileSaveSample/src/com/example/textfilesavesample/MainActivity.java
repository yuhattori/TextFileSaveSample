package com.example.textfilesavesample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			copySampleFileFromAssets();
			Toast.makeText(this, "copy", Toast.LENGTH_SHORT).show();
			TextView textView = (TextView) this.findViewById(R.id.text);
			textView.setText(Environment.getExternalStorageDirectory()
					.getPath() + "/test"+"にファイルをコピーしました");
		} catch (Exception e) {
			Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
		}
	}

	private void copySampleFileFromAssets() throws Exception {
		/* assets/textのなかにあるファイル名をすべて読み込む */
		String[] fileList = getResources().getAssets().list("text");
		if (fileList == null || fileList.length == 0) {
			return;
		}

		AssetManager as = getResources().getAssets();
		InputStream input = null;
		FileOutputStream output = null;
		int DEFAULT_BUFFER_SIZE = 1024 * 4;

		/* assets/textのなかにあるファイルをすべて内部ストレージの中の/testに保存する */

		// 保存場所が存在するかどうかを確認する
		File saveDirectory = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/test");
		if (!saveDirectory.exists()) {
			saveDirectory.mkdir();
		}

		for (String file : fileList) {
			// ファイルを保存する
			input = as.open("text" + "/" + file);
			Toast.makeText(this, saveDirectory.getPath(), Toast.LENGTH_SHORT)
					.show();
			// 内部ストレージの中にtestというフォルダを作ってその中に入れる
			File newFile = new File(saveDirectory.getPath(), file);
			output = new FileOutputStream(newFile);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
			// 端末に作成したファイルをアンドロイドデータベースに通知して表示させるようにする
			showFolder(newFile);
			output.close();
			input.close();
		}
	}

	// ContentProviderに新しいファイルが作られたことを通知する
	private void showFolder(File newFile) throws Exception {
		ContentValues values = new ContentValues();
		ContentResolver contentResolver = getApplicationContext()
				.getContentResolver();
		values.put(MediaStore.Files.FileColumns.MEDIA_TYPE, "external");// ファイルタイプ
		values.put(MediaStore.Files.FileColumns.DATE_MODIFIED,
				System.currentTimeMillis() / 1000);// 修正日
		values.put(MediaStore.Files.FileColumns.SIZE, newFile.length());// ファイルサイズ
		values.put(MediaStore.Files.FileColumns.TITLE, newFile.getName());// ファイルネーム
		values.put(MediaStore.Files.FileColumns.DATA, newFile.getPath());// ファイルパス
		contentResolver.insert(MediaStore.Files.getContentUri("external"),
				values);// 端末に作成したファイルをアンドロイドデータベースに登録
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
