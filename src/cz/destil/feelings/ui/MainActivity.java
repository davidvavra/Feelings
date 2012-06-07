package cz.destil.feelings.ui;

import java.util.Date;

import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import cz.destil.feelings.R;
import cz.destil.feelings.data.Feeling.Feelings;
import cz.destil.feelings.notify.AlarmSetter;
import cz.destil.feelings.ui.widgets.FeelingsChart;

/**
 * 
 * Graph of recent feelings
 * 
 * @author Destil
 */

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main_menu)
public class MainActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = "FeelingsActivity";
	@ViewById
	LinearLayout chart;
	private GraphicalView chartView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateOnDataChanges();
		setPeriodicNotifications();
	}

	protected void onResume() {
		super.onResume();
		if (chartView == null) {
			buildChart();
		} else {
			chartView.repaint();
		}
	}

	@OptionsItem
	void newFeelingSelected() {
		startActivity(new Intent(this, NewFeelingActivity_.class));
	}

	@OptionsItem
	void settingsSelected() {
		startActivity(new Intent(this, SettingsActivity.class));
	}

	/**
	 * Builds chart from database data
	 */
	@Background
	void buildChart() {
		Cursor cursor = getContentResolver().query(Feelings.CONTENT_URI,
				new String[] { Feelings.CREATED, Feelings.FEELING }, null, null, null);
		TimeSeries series = new TimeSeries("Feeling");
		if (cursor.getCount() == 0) {
			cursor.close();
			return;
		}
		while (cursor.moveToNext()) {
			Date date = new Date(cursor.getLong(0));
			double feeling = cursor.getInt(1);
			series.add(date, feeling);
		}
		cursor.close();
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		displayChart(dataset);
	}

	/**
	 * Displays chart in UI
	 * 
	 * @param dataset
	 */
	@UiThread
	void displayChart(XYMultipleSeriesDataset dataset) {
		FeelingsChart feelingsChart = new FeelingsChart(dataset);
		chartView = new GraphicalView(this, feelingsChart);
		if (chart.getChildCount() > 0) {
			chart.removeAllViews();
		}
		chart.addView(chartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	/**
	 * Listen for data changes
	 */
	private void updateOnDataChanges() {
		getContentResolver().registerContentObserver(Feelings.CONTENT_URI, true, new ContentObserver(null) {
			@Override
			public void onChange(boolean selfChange) {
				buildChart();
			}
		});
	}

	/**
	 * Sets up notifications for every day.
	 */
	private void setPeriodicNotifications() {
		sendBroadcast(new Intent(this, AlarmSetter.class));
	}
}