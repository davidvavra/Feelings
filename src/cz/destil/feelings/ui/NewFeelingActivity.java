package cz.destil.feelings.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;

import cz.destil.feelings.R;
import cz.destil.feelings.data.Feeling.Feelings;

/**
 * 
 * Screen handling adding new feeling
 * 
 * @author Destil
 */

@EActivity(R.layout.activity_new_feeling)
public class NewFeelingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String action = getIntent().getAction();
		if (action != null && action.equals(Intent.ACTION_TIME_TICK)) {
			// called from notification
			getActionBar().setHomeButtonEnabled(true);
		} else {
			// called from main activity
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Click({ R.id.feeling0, R.id.feeling1, R.id.feeling2, R.id.feeling3, R.id.feeling4, R.id.feeling5, R.id.feeling6 })
	void feelingClicked(View view) {
		String id = getResources().getResourceEntryName(view.getId());
		String number = id.substring(id.length() - 1, id.length());
		addFeeling(Integer.parseInt(number));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, MainActivity_.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * Ads feeling into database or edits previously entered
	 */
	private void addFeeling(int feeling) {
		Calendar now = new GregorianCalendar();
		Calendar today = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH));
		ContentValues values = new ContentValues();
		values.put(Feelings.FEELING, feeling);
		values.put(Feelings.CREATED, today.getTimeInMillis());
		values.put(Feelings.NOTE, "");
		Cursor cursor = getContentResolver().query(Feelings.CONTENT_URI, null,
				Feelings.CREATED + "=" + today.getTimeInMillis(), null, null);
		if (cursor.getCount() > 0) {
			getContentResolver().update(Feelings.CONTENT_URI, values, Feelings.CREATED + "=" + today.getTimeInMillis(),
					null);
			Toast.makeText(this, R.string.feeling_changed, Toast.LENGTH_SHORT).show();
		} else {
			getContentResolver().insert(Feelings.CONTENT_URI, values);
			Toast.makeText(this, R.string.feeling_recorded, Toast.LENGTH_SHORT).show();
		}
		cursor.close();
		finish();
	}
}