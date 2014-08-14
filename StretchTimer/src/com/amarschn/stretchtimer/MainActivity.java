package com.amarschn.stretchtimer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button startButton;
	private TextView timerTextView;
	private TextView stretchText;
	private String[] allStretches;
	private Iterator<String> stretchesToDo;
	private long startTime = 0;
	private TextToSpeech tts;

	/* The timer thread and handler */
	Handler timerHandler = new Handler();
	Runnable timerRunner = new Runnable() {

		@Override
		public void run() {
			// Run the timer based on system time
			long timeSinceStart = System.currentTimeMillis() - startTime;
			int seconds = (int) (timeSinceStart / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;
			// Set the text view
			timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
			// Add the timer to the run queue
			timerHandler.postDelayed(this, 100);

			/*
			 * If the time is over the set amount of time for the given stretch,
			 * move on to the next stretch. If there is no stretch, set the
			 * clock back to zero.
			 */
			if (seconds > 45) {
				if (stretchesToDo.hasNext()) {
					startTime = System.currentTimeMillis();
					String nextStretch = stretchesToDo.next();
					/* Speak the next stretch */
					tts.speak(nextStretch, TextToSpeech.QUEUE_FLUSH, null);
					stretchText.setText(nextStretch);
				} else {
					timerHandler.removeCallbacks(timerRunner);
					timerTextView.setText(R.string.zero_time);
					stretchText.setText(R.string.stretch_type);
					/*
					 * Let the user know they are done with their stretch
					 * routine
					 */
					tts.speak(
							getResources().getString(
									R.string.finished_stretches),
							TextToSpeech.QUEUE_FLUSH, null);
				}
			}
			/* Tell the user to switch sides if necessary */
			if (seconds == 45 / 2) {
				tts.speak(getResources().getString(R.string.switch_sides),
						TextToSpeech.QUEUE_FLUSH, null);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Set the screen to stay on while app is running */
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		/* Set the stretches array, and set the number of stretches to do */
		allStretches = getResources().getStringArray(R.array.stretches_array);

		/* Set the timer */
		timerTextView = (TextView) findViewById(R.id.timerView);
		timerTextView.setText(R.string.zero_time);

		/* Set the stretch text view */
		stretchText = (TextView) findViewById(R.id.stretchName);
		stretchText.setText(R.string.stretch_type);

		/* Text to speech implementation, sets the language to English */
		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

			@Override
			public void onInit(int status) {
				tts.setLanguage(Locale.US);
			}
		});

		/* Set the button listener */
		startButton = (Button) findViewById(R.id.button1);
		startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				/* Reset the stretches to perform */
				stretchesToDo = getRandomStretchList(8).iterator();
				/*
				 * Reset the text in the text field below the button to the
				 * first stretch, and then speak it aloud
				 */
				String firstStretch = stretchesToDo.next();
				stretchText.setText(firstStretch);
				tts.speak(firstStretch, TextToSpeech.QUEUE_FLUSH, null);

				/* Start the timer */
				startTime = System.currentTimeMillis();
				timerHandler.postDelayed(timerRunner, 0);
			}
		});

	}

	/**
	 * @return a random string from the stretches array resource
	 */
	private String getRandomStretch() {
		/* Get random integer for index */
		Random rand = new Random();
		int range = allStretches.length;
		int randIndex = rand.nextInt(range);
		return allStretches[randIndex];
	}

	/**
	 * @param numStretches
	 *            : The number of stretches that will be returned
	 * @returna list of (semi)random stretches
	 */
	private Set<String> getRandomStretchList(int numStretches) {
		Set<String> stretches = new HashSet<String>();
		/* Add random stretches to the set until you have the number requested */
		while (stretches.size() < numStretches) {
			String stretch = getRandomStretch();
			stretches.add(stretch);
		}
		return stretches;
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
