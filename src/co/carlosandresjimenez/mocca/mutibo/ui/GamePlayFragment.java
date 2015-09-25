/* 
 **
 ** Copyright 2014, 
 ** Carlos Andres Jimenez
 ** apps@carlosandresjimenez.co
 ** 
 */
package co.carlosandresjimenez.mocca.mutibo.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import co.carlosandresjimenez.mocca.mutibo.R;
import co.carlosandresjimenez.mocca.mutibo.beans.Answer;
import co.carlosandresjimenez.mocca.mutibo.beans.QuestionSet;
import co.carlosandresjimenez.mocca.mutibo.beans.Session;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class GamePlayFragment extends Fragment implements OnClickListener {

	private final static String LOG_TAG = GamePlayFragment.class
			.getCanonicalName();

	public interface Listener {
		public void onShowAchievementsRequested();

		public void onShowLeaderboardsRequested();

		public void onShowMainActivityRequested();

		public void onSaveAnswerRequested(Answer answer);

		public Session onSessionRequested();

		public void onNewSessionRequested();

		public QuestionSet onQuestionSetRequested();

		public void onClearGameRequested();

		public void onSaveFinalScoreRequested(long score);

		public void onPlaySoundEffect(int soundType);

		public void onBackPressed();
	}

	Listener mListener = null;

	private final static float NUMBER_OF_STARS = 5;

	final static int FLIP_VERTICAL = 1;
	final static int FLIP_HORIZONTAL = 2;

	MainActivity mOpener;

	QuestionSet qset;
	Session session;

	LinearLayout llAnswer1;
	TextView tvAnswer1;
	ImageButton rbAnswer1;
	LinearLayout llAnswer2;
	TextView tvAnswer2;
	ImageButton rbAnswer2;
	LinearLayout llAnswer3;
	TextView tvAnswer3;
	ImageButton rbAnswer3;
	LinearLayout llAnswer4;
	TextView tvAnswer4;
	ImageButton rbAnswer4;

	TextView tvQuestion;
	TextView tvReason;
	TextView tvRate;

	ImageButton ibHeart1;
	ImageButton ibHeart2;
	ImageButton ibHeart3;

	TextView scorePoints;
	TextView scoreTotalQuestions;

	Button btNextQuestion;

	ImageButton btThumbsUp;
	ImageButton btThumbsDown;

	ImageButton btExit;

	Answer userAnswer;

	Dialog resultDialog;

	int[] randomMatrix;

	boolean endGame;
	boolean answerChosen;

	int heartsDisplayed;

	public void setListener(Listener l) {
		mListener = l;
	}

	/**
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOpener = (MainActivity) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_gameplay, container, false);

		endGame = false;
		answerChosen = false;
		heartsDisplayed = 3;
		randomMatrix = new int[4];

		llAnswer1 = (LinearLayout) v.findViewById(R.id.linearLayoutAnswer1);
		tvAnswer1 = (TextView) v.findViewById(R.id.textAnswer1);
		rbAnswer1 = (ImageButton) v.findViewById(R.id.roundButtonAnswer1);
		llAnswer2 = (LinearLayout) v.findViewById(R.id.linearLayoutAnswer2);
		tvAnswer2 = (TextView) v.findViewById(R.id.textAnswer2);
		rbAnswer2 = (ImageButton) v.findViewById(R.id.roundButtonAnswer2);
		llAnswer3 = (LinearLayout) v.findViewById(R.id.linearLayoutAnswer3);
		tvAnswer3 = (TextView) v.findViewById(R.id.textAnswer3);
		rbAnswer3 = (ImageButton) v.findViewById(R.id.roundButtonAnswer3);
		llAnswer4 = (LinearLayout) v.findViewById(R.id.linearLayoutAnswer4);
		tvAnswer4 = (TextView) v.findViewById(R.id.textAnswer4);
		rbAnswer4 = (ImageButton) v.findViewById(R.id.roundButtonAnswer4);

		ibHeart1 = (ImageButton) v.findViewById(R.id.imageHeart1);
		ibHeart2 = (ImageButton) v.findViewById(R.id.imageHeart2);
		ibHeart3 = (ImageButton) v.findViewById(R.id.imageHeart3);

		btExit = (ImageButton) v.findViewById(R.id.imageButtonBack);

		llAnswer1.setOnClickListener(this);
		tvAnswer1.setOnClickListener(this);
		rbAnswer1.setOnClickListener(this);
		llAnswer2.setOnClickListener(this);
		tvAnswer2.setOnClickListener(this);
		rbAnswer2.setOnClickListener(this);
		llAnswer3.setOnClickListener(this);
		tvAnswer3.setOnClickListener(this);
		rbAnswer3.setOnClickListener(this);
		llAnswer4.setOnClickListener(this);
		tvAnswer4.setOnClickListener(this);
		rbAnswer4.setOnClickListener(this);

		btExit.setOnClickListener(this);

		btNextQuestion = (Button) v.findViewById(R.id.next_question);
		btNextQuestion.setOnClickListener(this);

		tvQuestion = (TextView) v.findViewById(R.id.question);
		tvReason = (TextView) v.findViewById(R.id.reason);

		scorePoints = (TextView) v.findViewById(R.id.scoreValue_points);
		scoreTotalQuestions = (TextView) v.findViewById(R.id.scoreValue_total);

		tvRate = (TextView) v.findViewById(R.id.rate);

		btThumbsUp = (ImageButton) v.findViewById(R.id.imageButton1);
		btThumbsUp.setVisibility(LinearLayout.INVISIBLE);
		btThumbsUp.setClickable(false);
		btThumbsUp.setOnClickListener(this);

		btThumbsDown = (ImageButton) v.findViewById(R.id.imageButton2);
		btThumbsDown.setVisibility(LinearLayout.INVISIBLE);
		btThumbsDown.setClickable(false);
		btThumbsDown.setOnClickListener(this);

		session = mListener.onSessionRequested();

		getNextQSet();

		return v;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageButtonBack:
			mListener.onBackPressed();
			break;
		case R.id.linearLayoutAnswer1:
		case R.id.textAnswer1:
		case R.id.roundButtonAnswer1:
			validateAnwer(tvAnswer1, rbAnswer1, getRealAnswer(1));
			break;
		case R.id.linearLayoutAnswer2:
		case R.id.textAnswer2:
		case R.id.roundButtonAnswer2:
			validateAnwer(tvAnswer2, rbAnswer2, getRealAnswer(2));
			break;
		case R.id.linearLayoutAnswer3:
		case R.id.textAnswer3:
		case R.id.roundButtonAnswer3:
			validateAnwer(tvAnswer3, rbAnswer3, getRealAnswer(3));
			break;
		case R.id.linearLayoutAnswer4:
		case R.id.textAnswer4:
		case R.id.roundButtonAnswer4:
			validateAnwer(tvAnswer4, rbAnswer4, getRealAnswer(4));
			break;
		case R.id.next_question:
			saveAndGetNewSet();
			break;
		case R.id.imageButton1:
			setRating(1);
			break;
		case R.id.imageButton2:
			setRating(-1);
			break;
		}
	}

	public void saveAndGetNewSet() {
		if (!endGame) {
			if (answerChosen) {
				mListener.onSaveAnswerRequested(userAnswer);
				getNextQSet();
			} else {
				Toast.makeText(mOpener, "Please select a movie",
						Toast.LENGTH_LONG).show();
			}
		} else {
			endGame(Float.valueOf((String) scoreTotalQuestions.getText()),
					Float.valueOf((String) scorePoints.getText()));
		}
	}

	public void getNextQSet() {
		qset = mListener.onQuestionSetRequested();

		if (qset != null) {
			userAnswer = new Answer();
			userAnswer.setSetId(qset.getSetId());
			userAnswer.setUsername(session.getUsername());
			userAnswer.setSessionId(session.getSessionId());

			qset.setUserAnswer(userAnswer);

			setQSetValues();

		} else {
			Log.d(LOG_TAG, "No more QSets on the Cloud");
			btNextQuestion.setText("End Game");
			endGame = true;
		}
	}

	public void endGame(float totalQuestions, float totalCorrect) {
		// custom dialog
		resultDialog = new Dialog(mOpener);
		resultDialog.setContentView(R.layout.dialog_game_result);
		resultDialog.setTitle("Results");
		resultDialog.setCanceledOnTouchOutside(false);

		RatingBar ratingBar = (RatingBar) resultDialog
				.findViewById(R.id.ratingBar1);
		float rating = (totalCorrect * NUMBER_OF_STARS) / totalQuestions;
		ratingBar.setRating(rating);

		// set the custom dialog components - text, image and button
		TextView text = (TextView) resultDialog.findViewById(R.id.text);
		String dialogText = "";
		if (rating >= 4) {
			dialogText = "Congratulations!!\n";
		}
		text.setText(dialogText + "You've been awarded with");

		Button dialogButton = (Button) resultDialog
				.findViewById(R.id.dialogButtonOK);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resultDialog.dismiss();

				mListener.onSaveFinalScoreRequested(Long
						.valueOf((String) scoreTotalQuestions.getText()));
				mListener.onClearGameRequested();
				mListener.onNewSessionRequested();
				mListener.onShowMainActivityRequested();
			}
		});

		resultDialog.show();
	}

	public void setQSetValues() {
		
		randomMatrix = new int[4];
		
		resetButtonColor(rbAnswer1);
		tvAnswer1.setText(getRandomQuestion(1));

		resetButtonColor(rbAnswer2);
		tvAnswer2.setText(getRandomQuestion(2));

		resetButtonColor(rbAnswer3);
		tvAnswer3.setText(getRandomQuestion(3));

		resetButtonColor(rbAnswer4);
		tvAnswer4.setText(getRandomQuestion(4));

		tvQuestion.setText("By " + qset.getQuestionType()
				+ getResources().getString(R.string.question));
		tvReason.setText("");
		tvReason.setVisibility(TextView.INVISIBLE);
		tvRate.setVisibility(TextView.INVISIBLE);

		btThumbsUp.setVisibility(ImageButton.INVISIBLE);
		btThumbsDown.setVisibility(ImageButton.INVISIBLE);

		llAnswer1.setClickable(true);
		tvAnswer1.setClickable(true);
		rbAnswer1.setClickable(true);

		llAnswer2.setClickable(true);
		tvAnswer2.setClickable(true);
		rbAnswer2.setClickable(true);

		llAnswer3.setClickable(true);
		tvAnswer3.setClickable(true);
		rbAnswer3.setClickable(true);

		llAnswer4.setClickable(true);
		tvAnswer4.setClickable(true);
		rbAnswer4.setClickable(true);
		
		btThumbsUp.setColorFilter(getResources().getColor(R.color.accent));
		btThumbsDown.setColorFilter(getResources().getColor(R.color.accent));

		answerChosen = false;
	}

	public void resetButtonColor(ImageButton ib) {
		ib.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.rounded_button));
		ib.setImageResource(0);
	}

	public void validateAnwer(View tvAnswer, ImageButton rbAnswer,
			int selectedAnswer) {
		llAnswer1.setClickable(false);
		tvAnswer1.setClickable(false);
		rbAnswer1.setClickable(false);

		llAnswer2.setClickable(false);
		tvAnswer2.setClickable(false);
		rbAnswer3.setClickable(false);

		llAnswer3.setClickable(false);
		tvAnswer3.setClickable(false);
		rbAnswer3.setClickable(false);

		llAnswer4.setClickable(false);
		tvAnswer4.setClickable(false);
		rbAnswer4.setClickable(false);

		scoreTotalQuestions.setText(String.valueOf(Integer
				.valueOf((String) scoreTotalQuestions.getText()) + 1));

		if (qset.getWrongMovieNum() == selectedAnswer) {
			rbAnswer.setImageResource(R.drawable.ic_done_grey600_24dp);
			rbAnswer.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.rounded_correct_button));

			mListener.onPlaySoundEffect(MainActivity.SOUND_CORRECT);
			userAnswer.setAnswerPoint(Answer.CORRECT_ANSWER);
			scorePoints.setText(String.valueOf(Integer
					.valueOf((String) scorePoints.getText()) + 1));

		} else {
			rbAnswer.setImageResource(R.drawable.ic_clear_grey600_24dp);
			rbAnswer.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.rounded_wrong_button));
			hideHeart();

			userAnswer.setAnswerPoint(Answer.WRONG_ANSWER);
			mListener.onPlaySoundEffect(MainActivity.SOUND_WRONG);

			if (Integer.valueOf((String) scoreTotalQuestions.getText())
					- Integer.valueOf((String) scorePoints.getText()) >= 3) {
				btNextQuestion.setText("Game Over");
				endGame = true;
			}
		}

		tvReason.setVisibility(TextView.VISIBLE);
		tvRate.setVisibility(TextView.VISIBLE);

		tvReason.setText(qset.getReason());
		btThumbsUp.setVisibility(ImageButton.VISIBLE);
		btThumbsUp.setClickable(true);

		btThumbsDown.setVisibility(ImageButton.VISIBLE);
		btThumbsDown.setClickable(true);

		answerChosen = true;
	}

	public void setRating(int userRating) {

		if (userRating == 1)
			btThumbsUp.setColorFilter(getResources().getColor(R.color.green_light));

		if (userRating == -1)
			btThumbsDown.setColorFilter(getResources().getColor(R.color.red_light));

		btThumbsUp.setClickable(false);
		btThumbsDown.setClickable(false);

		userAnswer.setUserRating(userRating);
	}

	public void hideHeart() {
		switch (heartsDisplayed) {
		case 1:
			ibHeart1.setVisibility(ImageButton.INVISIBLE);
			break;
		case 2:
			ibHeart2.setVisibility(ImageButton.INVISIBLE);
			break;
		case 3:
			ibHeart3.setVisibility(ImageButton.INVISIBLE);
		}
		heartsDisplayed--;
	}

	public String getRandomQuestion(int pos) {

		Random rnd = new Random();

		int random = 0;
		String question = "";

		Method method;
		try {
			random = rnd.nextInt(4);

			while (randomMatrix[random] != 0)
				random = rnd.nextInt(4);

			randomMatrix[random] = pos;
			random++;

			method = qset.getClass().getDeclaredMethod("getMovieName" + random, new Class[] {});
			question = (String) method.invoke(qset, null);

		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return question;
	}
	
	public int getRealAnswer(int pos) {
		int i = 0;
		
		while (randomMatrix[i] != pos) {
			i++;
		}

		return i+1;
	}

}
