package com.example.weatherdresser;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.github.sendgrid.SendGrid;
//import com.thinkingserious.sendgrid.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.facebook.*;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
//import com.twilio.sdk.TwilioRestException;

import android.annotation.SuppressLint;
//import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainFragment extends Fragment {
	private String LOCATION;
	private String EMAIL;
	private String NAME;
	private static final String TAG = "MainFragment";
	private UiLifecycleHelper uiHelper;
	private TextView userInfoTextView;
	private String username = "bettkd";
	private String password = "FBHacks";
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.main, container, false);
		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("email", "user_location"));

		userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);

		return view;
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
			userInfoTextView.setVisibility(View.VISIBLE);

			Request.executeMeRequestAsync(session,
					new Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							if (user != null) {
								// Build URL
								URL url = null;
								int temp = 20;
								/*
								 * String URL =
								 * "http://api.worldweatheronline.com/free/v2/weather.ashx?q="
								 * + getLocation(user) +
								 * "&format=xml&num_of_days=1&key=87d42f18cf1734ef07e13821679de"
								 * ; // Display the parsed user info try { url =
								 * new URL(URL); HttpURLConnection con =
								 * (HttpURLConnection) url .openConnection();
								 * DocumentBuilderFactory docBuilderFactory =
								 * DocumentBuilderFactory .newInstance();
								 * DocumentBuilder docBuilder =
								 * docBuilderFactory .newDocumentBuilder();
								 * Document doc = docBuilder.parse(con
								 * .getInputStream());
								 * doc.getDocumentElement().normalize();
								 * NodeList currentCondition = doc
								 * .getElementsByTagName("current_condition");
								 * 
								 * Element currentConditionElement = (Element)
								 * currentCondition .item(0); temp = ((Element)
								 * currentConditionElement
								 * .getElementsByTagName("temp_C")
								 * .item(0)).getChildNodes().item(0)
								 * .getNodeValue().trim();
								 * 
								 * } catch (MalformedURLException e) { // TODO
								 * Auto-generated catch block //
								 * Log.d(LocationUtils.APPTAG, //
								 * e.getMessage()); } catch (IOException ex) {
								 * ex.printStackTrace(); } catch (SAXException
								 * ex) { ex.printStackTrace(); } catch
								 * (ParserConfigurationException ex) {
								 * ex.printStackTrace(); }
								 */
								if (temp < 50) {
									try {
										// SendSms send = new SendSms();
										// send.sendSms("It is cold, dress warm.",
										// "+12095072194");
										sendEmail("fasholaide@gmail.com", getName(user), getEmail(user), "Weather Dresser", "Dress Warm");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									try {
										// SendSms send = new SendSms();
										// send.sendSms("It is warm. dress light!",
										// "+12095072194");
										sendEmail("fasholaide@gmail.com", getName(user), getEmail(user), "Weather Dresser", "Dress Light");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

								userInfoTextView
										.setText(buildUserInfoDisplay(user));
							}
						}
					});

		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
			userInfoTextView.setVisibility(View.INVISIBLE);
		}
	}

	private String buildUserInfoDisplay(GraphUser user) {
		StringBuilder userInfo = new StringBuilder("");

		NAME = user.getName();
		LOCATION = user.getLocation().getName();
		EMAIL = (String) user.getProperty("email");

		userInfo.append(NAME);
		userInfo.append(LOCATION);
		userInfo.append(EMAIL);

		return userInfo.toString();
	}

	private String getName(GraphUser user) {
		return user.getName();
	}

	private String getLocation(GraphUser user) {
		return user.getLocation().getName();
	}

	private String getEmail(GraphUser user) {
		return (String) user.getProperty("email");
	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		// uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private class SendEmailWithSendGrid extends
			AsyncTask<Hashtable<String, String>, Void, String> {
		@Override
		protected String doInBackground(Hashtable<String, String>... params) {
			Hashtable<String, String> h = params[0];
			// Utils creds = new Utils();
			SendGrid sendgrid = new SendGrid(username, password);
			sendgrid.addTo(h.get("to"));
			sendgrid.setFrom(h.get("from"));
			sendgrid.setSubject(h.get("subject"));
			sendgrid.setText(h.get("text"));
			String response = sendgrid.send();
			return response;
		}
	}

	@SuppressWarnings("unchecked")
	public void sendEmail(String from, String to_name, String to_email, String subject,
			String text) {
		Hashtable<String, String> params = new Hashtable<String, String>();
		String result = null;
		// Get the values from the form
		String textSent = to_name + "/n" + text;

		params.put("to", to_email);

		params.put("from", from);

		params.put("subject", subject);

		params.put("text", textSent);
		// Send the Email
		SendEmailWithSendGrid email = new SendEmailWithSendGrid();
		try {
			result = email.execute(params).get();
		} catch (InterruptedException e) {
			// TODO Implement exception handling
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Implement exception handling
			e.printStackTrace();
		}
		// Display the result of the email send
		// Intent intent = new Intent(this, DisplayMessageActivity.class);
		// intent.putExtra(EXTRA_MESSAGE, result);
		// startActivity(intent);
	}
}
