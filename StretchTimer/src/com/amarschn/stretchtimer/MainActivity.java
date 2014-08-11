package com.amarschn.stretchtimer;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button startButton;
	private TextView timerTextView;
	private String[] stretches;
	private long startTime = 0;

	/* The timer thread and handler */
	Handler timerHandler = new Handler();
	Runnable timerRunner = new Runnable() {

		@Override
		public void run() {
			// Run the timer based on system time
			long milliseconds = System.currentTimeMillis() - startTime;
			int seconds = (int) (milliseconds / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;
			// Set the text view
			timerTextView.setText(String.format("%d:%02d", minutes, seconds));
			// Add the timer to the run queue
			timerHandler.postDelayed(this, 0);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Set the stretches array */
		stretches = getResources().getStringArray(R.array.stretches_array);

		/* Set the timer */
		timerTextView = (TextView) findViewById(R.id.timerView);
		timerTextView.setText("0:00");

		/* Set the button listener */
		startButton = (Button) findViewById(R.id.button1);
		startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Reset the text in the text field below the button to
				 * something
				 */
				TextView stretchText = (TextView) findViewById(R.id.stretchName);
				stretchText.setText(getRandomStretch());

				startTime = System.currentTimeMillis();
				timerHandler.postDelayed(timerRunner, 0);
				int i = 1;
			}
		});

	}

	/**
	 * @return a random string from the stretches array resource
	 */
	private String getRandomStretch() {
		/* Get random integer for index */
		Random rand = new Random();
		int range = stretches.length;
		int randIndex = rand.nextInt(range);
		return stretches[randIndex];
	}

	/**
	 * @return a list of (semi)random stretches
	 */
	private List<String> getRandomStretchList() {
		return null;
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
