import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import com.facebook.AppEventsLogger;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AppEventsLogger;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class MainActivity extends Activity {
	private static final String APP_ID = "594082447295048";
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private UiLifecycleHelper uiHelper;
	private Button postStatusUpdateButton;
	private PendingAction pendingAction = PendingAction.NONE;
	int id;

	private static final Uri ATTRIBUTION_ID_CONTENT_URI = Uri
			.parse("content://com.facebook.katana.provider.AttributionIdProvider");
	private static final String ATTRIBUTION_ID_COLUMN_NAME = "aid";
	private static final String ATTRIBUTION_PREFERENCES = "com.facebook.sdk.attributionTracking";
	private static final String PUBLISH_ACTIVITY_PATH = "%s/activities";
	private static final String MOBILE_INSTALL_EVENT = "MOBILE_APP_INSTALL";
	private static final String ANALYTICS_EVENT = "event";
	private static final String ATTRIBUTION_KEY = "attribution";
	private static final String AUTO_PUBLISH = "auto_publish";

	String city;
	String region;
	String country;
	JSONArray fcast;
	String detailsLink;
	String detailsFeed;
	String fullForecast, cleanForecast, a, b, c, d, e, f, g, h, i, j, k;
	String text1;
	String degree;

	private enum PendingAction {
		NONE, POST_STATUS_UPDATE
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bitmap imgu = (Bitmap) msg.obj;
			ImageView imv = (ImageView) findViewById(R.id.imageView1);
			imv.setImageBitmap(imgu);
		}
	};

	
	}

	Handler zHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			try {
				JSONObject jsonObject = (JSONObject) msg.obj;
				final JSONObject weatherObservationItems = new JSONObject(
						jsonObject.getString("weather"));
				detailsLink = weatherObservationItems.getString("link");

				detailsFeed = weatherObservationItems.getString("feed");
				final JSONObject location = weatherObservationItems
						.getJSONObject("location");
				city = location.getString("city");
				region = location.getString("region");
				country = location.getString("country");
				final String units;
				JSONArray fore = weatherObservationItems
						.getJSONArray("forecast");
				fcast = fore;
				fullForecast = fore.toString();
				cleanForecast = fullForecast.toString().replace("[", "");
				a = cleanForecast.toString().replace("{", "");
				b = a.toString().replace("day", "");
				c = b.toString().replace("high", "");
				d = c.toString().replace("low", "");
				e = d.toString().replace("}", "");
				f = e.toString().replace("]", "");
				g = f.toString().replace("\"", "");
				h = g.toString().replace(":", "");
				i = h.toString().replace("text", "");

				final TableLayout tl = (TableLayout) findViewById(R.id.tableLayout1);
				tl.removeAllViews();
				final TableRow tt = new TableRow(MainActivity.this);
				final TextView title = new TextView(MainActivity.this); 
				title.setText("Forecast");
				title.setTextSize(18);
				title.setTextColor(Color.WHITE);
				tt.addView(title);
				tl.addView(tt, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				final TableRow th = new TableRow(MainActivity.this);
				final TextView h1 = new TextView(MainActivity.this); 
				h1.setText("Day");
				final TextView h2 = new TextView(MainActivity.this); 
				h2.setText("Weather");
				final TextView h3 = new TextView(MainActivity.this); 
				h3.setText("High");
				final TextView h4 = new TextView(MainActivity.this); 
				h4.setText("Low");
				h2.setLayoutParams(new LayoutParams(130,
						LayoutParams.WRAP_CONTENT, 0.4f));
				h1.setLayoutParams(new LayoutParams(50,
						LayoutParams.WRAP_CONTENT, 0.2f));
				h4.setGravity(Gravity.CENTER);
				h3.setGravity(Gravity.CENTER);
				h2.setGravity(Gravity.CENTER);
				h1.setGravity(Gravity.CENTER);
				th.addView(h1);
				th.addView(h2);
				th.addView(h3);
				th.addView(h4);
				h1.setBackgroundResource(R.drawable.hh1);
				h2.setBackgroundResource(R.drawable.hh1);
				h3.setBackgroundResource(R.drawable.hh1);
				h4.setBackgroundResource(R.drawable.hh1);
				tl.addView(th, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				for (int i = 0; i < fore.length(); i++) {
					String text = fore.getJSONObject(i).getString("text");
					String high = fore.getJSONObject(i).getString("high");
					String low = fore.getJSONObject(i).getString("low");
					String day = fore.getJSONObject(i).getString("day");

					final TableRow tR = new TableRow(MainActivity.this);
					final TextView tex = new TextView(MainActivity.this);
					tex.setText(text);
					final TextView hi = new TextView(MainActivity.this);
					if (tempSym == "f")
						hi.setText(high + "\u2109");
					else
						hi.setText(high + "\u2103");

					final TextView lw = new TextView(MainActivity.this);
					if (tempSym == "f")
						lw.setText(low + "\u2109");
					else
						lw.setText(low + "\u2103");

					final TextView dy = new TextView(MainActivity.this);
					dy.setText(day);
					hi.setTextColor(0xFFDEBD3D);
					lw.setTextColor(0xFF4699EE);
					if (i == 0 || i == 2 || i == 4) {
						dy.setBackgroundResource(R.drawable.line1);
						tex.setBackgroundResource(R.drawable.line1);
						hi.setBackgroundResource(R.drawable.line1);
						lw.setBackgroundResource(R.drawable.line1);
					} else if (i == 1 || i == 3) {
						dy.setBackgroundResource(R.drawable.line);
						tex.setBackgroundResource(R.drawable.line);
						hi.setBackgroundResource(R.drawable.line);
						lw.setBackgroundResource(R.drawable.line);
					}

					tex.setGravity(Gravity.CENTER);
					hi.setGravity(Gravity.CENTER);
					lw.setGravity(Gravity.CENTER);
					dy.setGravity(Gravity.CENTER);

					tR.setGravity(Gravity.CENTER);
					tR.addView(dy);
					tR.addView(tex);
					tR.addView(hi);
					tR.addView(lw);
					tl.addView(tR,
							new TableLayout.LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));
				}
				TextView t = (TextView) findViewById(R.id.textView1);
				t.setTextColor(Color.WHITE);
				t.setTextSize(30);

				t.setText(city);
				TextView re = (TextView) findViewById(R.id.reg);
				re.setTextColor(Color.WHITE);
				re.setTextSize(15);
				re.setText("   " + region + ", " + country);

				final JSONObject condi = weatherObservationItems
						.getJSONObject("condition");
				String text = condi.getString("text");
				TextView con = (TextView) findViewById(R.id.text1);
				con.setTextColor(Color.WHITE);
				con.setTextSize(15);
				con.setText(text);

				text1 = condi.getString("text");
				TextView cur = (TextView) findViewById(R.id.cur);
				cur.setTextColor(Color.WHITE);
				cur.setTextSize(15);
				cur.setText("Share Current Weather");
				cur.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Context context = MainActivity.this;
						final Dialog dialog = new Dialog(context);
						dialog.setContentView(R.layout.dialog);

						id = view.getId();

						dialog.setTitle("Post to Facebook");

						Button dialogButton1 = (Button) dialog
								.findViewById(R.id.dialogButton1);

						dialogButton1.setOnClickListener(new OnClickListener() {
							// @Override
							public void onClick(View v1) {
								try {
									publishFeedDialog();
								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								dialog.dismiss();
							}
						});

						Button dialogButton2 = (Button) dialog
								.findViewById(R.id.dialogButton2);

						dialogButton2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v1) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}

				});
				TextView forc = (TextView) findViewById(R.id.fore);
				forc.setTextColor(Color.WHITE);
				forc.setTextSize(15);
				forc.setText("Share Weather Forecast");
				forc.setMovementMethod(LinkMovementMethod.getInstance());
				forc.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Context context = MainActivity.this;
						final Dialog dialog = new Dialog(context);
						dialog.setContentView(R.layout.dialog1);

						id = view.getId();

						dialog.setTitle("Post to Facebook");

						Button dialogButton1 = (Button) dialog
								.findViewById(R.id.dialogButton1);

						dialogButton1.setOnClickListener(new OnClickListener() {
							// @Override
							public void onClick(View v1) {
								try {
									publishFeedDialog2();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								dialog.dismiss();

							}
						});

						Button dialogButton2 = (Button) dialog
								.findViewById(R.id.dialogButton2);

						dialogButton2.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v1) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}

				});

				degree = condi.getString("temp");
				TextView dg = (TextView) findViewById(R.id.dg1);
				dg.setTextColor(Color.WHITE);
				dg.setTextSize(15);
				if (tempSym == "f")
					dg.setText(degree + "\u2109");
				else
					dg.setText(degree + "\u2103");

				tl.setColumnShrinkable(1, true);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	Button srch;
	RadioButton radio1, radio2;
	String tempSym, loca, lType;
	String imgln;
	RadioGroup ts;

	public String readJSONFeed(String URL) {
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(URL);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				inputStream.close();
			} else {
				Log.d("JSON", "Failed to download file");
			}
		} catch (Exception e) {
			Log.d("readJSONFeed", e.getLocalizedMessage());
		}
		return stringBuilder.toString();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			String name = savedInstanceState
					.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}

		final TableLayout tl = (TableLayout) findViewById(R.id.tableLayout1);
		final TextView tv1, tv2, tv3, tv4, tv5, tv6;
		final ImageView iv;

		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.text1);
		tv3 = (TextView) findViewById(R.id.dg1);
		tv4 = (TextView) findViewById(R.id.reg);
		tv5 = (TextView) findViewById(R.id.cur);
		tv6 = (TextView) findViewById(R.id.fore);
		iv = (ImageView) findViewById(R.id.imageView1);

		srch = (Button) findViewById(R.id.button1);
		srch.setOnClickListener(new View.OnClickListener() {
			class ReadWeatherJSONFeedTask extends
					AsyncTask<String, Void, String> {
				protected String doInBackground(String... urls) {
					try {
						JSONObject jsonObject = new JSONObject(
								readJSONFeed(urls[0]));
						JSONObject weatherObservationItems = new JSONObject(
								jsonObject.getString("weather"));

						String image = weatherObservationItems.getString("img");
						imgln = image;

						Message msg2 = new Message();
						msg2.obj = jsonObject;
						zHandler.sendMessage(msg2);

						Bitmap bm = null;
						try {
							URL aURL = new URL(image);
							URLConnection conn = aURL.openConnection();
							conn.connect();
							InputStream is = conn.getInputStream();
							BufferedInputStream bis = new BufferedInputStream(
									is);
							bm = BitmapFactory.decodeStream(bis);

							Message msg = new Message();
							msg.obj = bm;

							mHandler.sendMessage(msg);
							bis.close();
							is.close();
						} catch (Exception e) {
							Log.v("EXCEPTION", "Error getting bitmap", e);
						}

						JSONArray fore = weatherObservationItems
								.getJSONArray("forecast");

					} catch (Exception e) {
						Log.d("ReadWeatherJSONFeedTask",
								e.getLocalizedMessage());
					}
					return null;
				}
			}

			@SuppressLint("NewApi")
			public void onClick(View arg0) {
				int cond = 0;
				EditText edit = (EditText) findViewById(R.id.edit_message);
				loca = edit.getText().toString();

				String inputEncoded = "";
				String[] words = loca.split(",");

				Bitmap bm1 = null;

				radio1 = (RadioButton) findViewById(R.id.radiof);
				radio2 = (RadioButton) findViewById(R.id.radioc);
				if (radio1.isChecked())
					tempSym = "f";
				if (radio2.isChecked())
					tempSym = "c";
				if (loca.isEmpty()) {
					Toast toast2 = Toast.makeText(getApplicationContext(),
							"Enter a City or a Zipcode !", Toast.LENGTH_SHORT);
					toast2.show();
					cond = 0;
					System.out.println("Invalid Input");
					tl.removeAllViews();
					tv1.setText(" ");
					tv2.setText(" ");
					tv3.setText(" ");
					tv4.setText(" ");
					tv5.setText(" ");
					tv6.setText(" ");
					iv.setImageBitmap(bm1);
				}

				String cityChk = "^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$";

				boolean retval0 = false;
				loca.trim();
				retval0 = loca.matches(cityChk);

				boolean retval1 = false;
				boolean retval2 = false;
				String zipCodePat = "\\d{5}(-\\d{4})?";
				String digs = "[0-9]+";
				retval1 = loca.matches(digs);
				retval2 = loca.matches(zipCodePat);

				if (retval1) {
					if (retval2) {
						cond = 1;
						lType = "zip";
						System.out.println(loca + " " + lType + " " + tempSym);
					} else {
						Toast toastdig = Toast
								.makeText(
										getApplicationContext(),
										"Invalid Zipcode :  Must Be 5 Digits !\nExample : 90008",
										Toast.LENGTH_LONG);
						toastdig.show();
						System.out.println("Invalid Input 1");
						tl.removeAllViews();
						tv1.setText(" ");
						tv2.setText(" ");
						tv3.setText(" ");
						tv4.setText(" ");
						tv5.setText(" ");
						tv6.setText(" ");
						iv.setImageBitmap(bm1);
						cond = 0;
					}
				}

				else if (words.length == 2 || words.length == 3) {
					lType = "city";
					cond = 1;
					try {
						loca = URLEncoder.encode(edit.getText().toString(),
								"UTF-8");
						System.out.println(loca);
					} catch (Exception e) {
						;
						Log.e("Encoding error", "URL Encoder " + e.toString());
					}
				} else {
					Toast.makeText(
							getApplicationContext(),
							"Invalid Location: must include state or country separated by a comma\nExample: Los Angeles, CA",
							Toast.LENGTH_SHORT).show();
					System.out.println("Invalid Input 2 ");
					tl.removeAllViews();
					tv1.setText(" ");
					tv2.setText(" ");
					tv3.setText(" ");
					tv4.setText(" ");
					tv5.setText(" ");
					tv6.setText(" ");
					iv.setImageBitmap(bm1);
					cond = 0;
				}

				if (cond == 1) {
					new ReadWeatherJSONFeedTask()
							.execute("http://cs-server.usc.edu:13597/examples/servlet/HelloWorldExample?loc="
									+ loca
									+ "&type="
									+ lType
									+ "&tempUnit="
									+ tempSym);
				}
			}
		});
	}

	private void updateUI() {
		Session session = Session.getActiveSession();
		boolean enableButtons = (session != null && session.isOpened());

	}

	private final String PENDING_ACTION_BUNDLE_KEY = "com.facebook.samples.hellofacebook:PendingAction";

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
		AppEventsLogger.activateApp(this);
		updateUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d("HelloFacebook", "Success!");
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
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

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			updateView();
		}
	}

	private void updateView() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			if (id == R.id.cur) {
				try {
					publishFeedDialog();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					publishFeedDialog2();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void publishFeedDialog() throws MalformedURLException, JSONException {
		if (hasPublishPermission()) {
			publishfeed1();
		} else {
			if (!Session.getActiveSession().isOpened()
					&& !Session.getActiveSession().isClosed()) {
				Session.getActiveSession().openForRead(
						new Session.OpenRequest(MainActivity.this)
								.setPermissions(Arrays.asList("basic_info"))
								.setCallback(statusCallback));
			} else if (Session.getActiveSession() == null) {
				Session.openActiveSession(MainActivity.this, true,
						statusCallback);
			} else {
				publishfeed1();
			}
		}

	}

	public void publishFeedDialog2() throws JSONException,
			MalformedURLException {

		if (hasPublishPermission()) {
			publishfeed2();
		} else {
			if (!Session.getActiveSession().isOpened()
					&& !Session.getActiveSession().isClosed()) {
				Session.getActiveSession().openForRead(
						new Session.OpenRequest(MainActivity.this)
								.setPermissions(Arrays.asList("basic_info"))
								.setCallback(statusCallback));
			} else if (Session.getActiveSession() == null) {
				Session.openActiveSession(MainActivity.this, true,
						statusCallback);
			} else {
				publishfeed2();
			}
		}
	
	public void publishfeed1() throws MalformedURLException, JSONException {
		Bundle params = new Bundle();
		URL dLink = new URL(detailsLink);
		String units1 = tempSym;
		System.out.println("FEED 1");
		String units;
		if (units1 == "f")
			units = "\u2109";
		else
			units = "\u2103";
		params.putString("name", city + ", " + region + ", " + country);
		params.putString("caption", "The current condition for " + city
				+ " is " + text1);
		params.putString("description", "Temperature is " + degree + units);

		JSONObject property = new JSONObject();
		property.put("text", "here");
		property.put("href", dLink);
		JSONObject properties = new JSONObject();
		properties.put("Look at details", property);
		params.putString("properties", properties.toString());

		params.putString("link", detailsFeed);
		params.putString("picture", imgln);

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
				MainActivity.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(MainActivity.this,
										"Posted story, id: " + postId,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(
										MainActivity.this
												.getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							Toast.makeText(
									MainActivity.this.getApplicationContext(),
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
						} else {
							Toast.makeText(
									MainActivity.this.getApplicationContext(),
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
						}
					}

				}).build();
		feedDialog.show();
	}

	public void publishfeed2() throws JSONException, MalformedURLException {
		Bundle params = new Bundle();
		System.out.println("FEED 2");
		URL dLink = new URL(detailsLink);
		String forcPic = "http://i40.tinypic.com/2as4s7.jpg";
		String units1 = tempSym;
		String units;
		if (units1 == "f")
			units = "\u2109";
		else
			units = "\u2103";
		params.putString("name", city + ", " + region + ", " + country);
		params.putString("caption", "Weather Forecast for " + city);
		// params.putString("description",i);
		String fullForecastData = "";

		for (int j = 0; j < fcast.length(); ++j) {
			JSONObject rec = fcast.getJSONObject(j);
			String strday = rec.getString("day");
			String stext = rec.getString("text");
			String shigh = rec.getString("high");
			String slow = rec.getString("low");
			fullForecastData += strday + ":" + stext + ", " + shigh + "/"
					+ slow + units;
			if (j != 4)
				fullForecastData += "; ";
		}
		params.putString("description", fullForecastData);

		JSONObject property = new JSONObject();
		property.put("text", "here");
		property.put("href", dLink);
		JSONObject properties = new JSONObject();
		properties.put("Look at details", property);
		params.putString("properties", properties.toString());

		params.putString("link", detailsFeed);
		params.putString("picture", forcPic);

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
				MainActivity.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(MainActivity.this,
										"Posted story, id: " + postId,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(
										MainActivity.this
												.getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if (error instanceof FacebookOperationCanceledException) {
							Toast.makeText(
									MainActivity.this.getApplicationContext(),
									"Publish cancelled", Toast.LENGTH_SHORT)
									.show();
						} else {
							Toast.makeText(
									MainActivity.this.getApplicationContext(),
									"Error posting story", Toast.LENGTH_SHORT)
									.show();
						}
					}

				}).build();
		feedDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null
				&& session.getPermissions().contains("publish_actions");
	}

	public static String getAttributionId(ContentResolver contentResolver) {
		try {
			String[] projection = { ATTRIBUTION_ID_COLUMN_NAME };
			Cursor c = contentResolver.query(ATTRIBUTION_ID_CONTENT_URI,
					projection, null, null, null);
			if (c == null || !c.moveToFirst()) {
				return null;
			}
			String attributionId = c.getString(c
					.getColumnIndex(ATTRIBUTION_ID_COLUMN_NAME));
			c.close();
			return attributionId;
		} catch (Exception e) {
			return null;
		}
	}

}