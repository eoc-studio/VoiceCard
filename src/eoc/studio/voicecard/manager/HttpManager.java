package eoc.studio.voicecard.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.annotations.SerializedName;

import eoc.studio.voicecard.menu.Index;
import eoc.studio.voicecard.volley.toolbox.GsonListRequest;
import eoc.studio.voicecard.volley.toolbox.MultipartJsonObjectRequest;
import eoc.studio.voicecard.volley.toolbox.MultipartRequest;
import eoc.studio.voicecard.volley.toolbox.StringXORer;
import eoc.studio.voicecard.volley.toolbox.VolleySingleton;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

public class HttpManager
{
	private final static String TAG = "HttpManager";

	private final static String HASH_CODE = "nu84x61w";

	private static String hash_time;

	private static String facebookID;

	private static String deviceIMEI;

	private static String hash_auth;

	private static String mobile;

	private static boolean isPostImageOk = false;

	private static boolean isPostSpeechOk = false;

	private static boolean isPostSignatureOk = false;

	private static boolean isPostMailOk = false;

	private static boolean isPostMailErrored = false;

	private static boolean isPostListImageOk = false;

	private static boolean isPostListSpeechOk = false;

	private static boolean isPostListSignatureOk = false;

	private static boolean isPostListMailOk = false;

	private static boolean isPostListMailErrored = false;

	private static ArrayList<Boolean> isPostMailOkList = new ArrayList<Boolean>();

	private static int parserCount = 0;

	// LoginListener loginListener;
	// PostMailListener postMailListener;
	public HttpManager()
	{

	}

	public void init(Context context, String facebookID)
	{

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phone = tm.getLine1Number();
		HttpManager.mobile = phone;

		Log.e(TAG, "HttpManager()  init() phone:" + phone);

		HttpManager.facebookID = facebookID;
		HttpManager.hash_time = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance()
				.getTime());

		HttpManager.hash_auth = StringXORer.encode(facebookID + "_" + hash_time, HASH_CODE);

		if (tm.getDeviceId() != null)
		{
			HttpManager.deviceIMEI = tm.getDeviceId();
		}
		else
		{
			HttpManager.deviceIMEI = android.os.Build.SERIAL;
		}

		Log.e(TAG, "init(): facebookID:" + facebookID + ",hash_time:" + hash_time + ",deviceIMEI:"
				+ deviceIMEI + ",hash_auth:" + hash_auth + ",mobile=" + mobile);
	}

	public void getFacebookUserInformation(Context context,
			GetFacebookInfoListener getFacebookInfoListener)
	{

		String uriGetFacebookUserInfo = String.format(
				"http://www.charliefind.com/api.php?op=facebook&auth=%1$s&id=%2$s", hash_auth,
				facebookID);

		Log.e(TAG, "getFacebookUserInformation uriGetFacebookUserInfo:" + uriGetFacebookUserInfo);

		java.lang.reflect.Type typeSend = new com.google.gson.reflect.TypeToken<ArrayList<GsonFacebookUser>>()
		{
		}.getType();

		GsonListRequest<ArrayList<GsonFacebookUser>> getFacebookUserInfoGsonRequset = new GsonListRequest<ArrayList<GsonFacebookUser>>(
				Method.GET, uriGetFacebookUserInfo, typeSend,
				createGetFacebookUserInfoGsonReqSuccessListener(getFacebookInfoListener),
				createFacebookUserInfoGsonReqErrorListener());
		getFacebookUserInfoGsonRequset.setTag("getFacebookUserInformation");
		VolleySingleton.getInstance(context).getRequestQueue().add(getFacebookUserInfoGsonRequset);
	}

	public void facebookLogin(Context context, GsonFacebookUser user,
			final LoginListener loginListener)
	{

		// this.loginListener = loginListeners;
		String uriFacebookUpdate = String
				.format("http://www.charliefind.com/api.php?op=facebook_update");

		HashMap<String, String> paramsFacebookUpdate = new HashMap<String, String>();
		paramsFacebookUpdate.put("auth", hash_auth);
		paramsFacebookUpdate.put("fb_id", facebookID);

		paramsFacebookUpdate
				.put("birthDay", (user.getBirthday() != null) ? user.getBirthday() : "");
		paramsFacebookUpdate.put("img", (user.getImg() != null) ? user.getImg() : "");
		paramsFacebookUpdate.put("locale", (user.getLocale() != null) ? user.getLocale() : "");
		paramsFacebookUpdate.put("link", (user.getLink() != null) ? user.getLink() : "");
		paramsFacebookUpdate.put("country", (user.getCountry() != null) ? user.getCountry() : "");
		paramsFacebookUpdate
				.put("timezone", (user.getTimezone() != null) ? user.getTimezone() : "");
		paramsFacebookUpdate.put("title", (user.getTitle() != null) ? user.getTitle() : "");
		paramsFacebookUpdate.put("email", (user.getEmail() != null) ? user.getEmail() : "");
		paramsFacebookUpdate.put("name", (user.getName() != null) ? user.getName() : "");
		paramsFacebookUpdate.put("edu", (user.getEdu() != null) ? user.getEdu() : "");
		paramsFacebookUpdate.put("gender", (user.getGender() != null) ? user.getGender() : "");
		paramsFacebookUpdate
				.put("industry", (user.getIndustry() != null) ? user.getIndustry() : "");
		paramsFacebookUpdate.put("mobile", (user.getMobile() != null) ? user.getMobile() : "");

		Log.e(TAG, "fascebookLogin() uri_facebook_update" + uriFacebookUpdate);
		JsonObjectRequest req = new JsonObjectRequest(uriFacebookUpdate, new JSONObject(
				paramsFacebookUpdate), new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{

				try
				{
					Log.e(TAG, "fascebookLogin()  facebook_update Response:" + response.toString(4));

					if (loginListener != null) loginListener.onResult(true, response.toString());

				}
				catch (JSONException e)
				{
					e.printStackTrace();
					if (loginListener != null) loginListener.onResult(false, e.toString());
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{

				Log.e(TAG, "fascebookLogin()  Error: " + error.getMessage());
				if (loginListener != null) loginListener.onResult(false, error.getMessage());
			}
		});
		req.setTag("fascebookLogin");
		VolleySingleton.getInstance(context).getRequestQueue().add(req);
	}

	public void cancelFacebookLogin(Context context)
	{

		VolleySingleton.getInstance(context).getRequestQueue().cancelAll("fascebookLogin");
	}

	public void cancelpostMail(Context context)
	{

		VolleySingleton.getInstance(context).getRequestQueue().cancelAll("postMail");
	}

	public void postMail(Context context, String sendTo, Uri imageUri, Uri speechUri,
			String editTextMessage, Uri signatureUri, String fontSize, String fontColor,
			String cardID, final PostMailListener postMailListener) throws Exception
	{

		isPostImageOk = false;
		isPostSpeechOk = false;
		isPostSignatureOk = false;
		isPostMailOk = false;
		isPostMailErrored = false;

		File imageFile = new File(imageUri.getPath());

		Log.e(TAG, "postMail imageUri.getPath():" + imageUri.getPath());
		Log.e(TAG, "postMail imageFile.getName():" + imageFile.getName());
		String uriUploadImagefile = String.format(
				"http://www.charliefind.com/api.php?op=upload&auth=%1$s&id=%2$s", hash_auth,
				facebookID);

		Log.e(TAG, "postMail uriUploadImagefile:" + uriUploadImagefile);

		MultipartRequest imageFileResuest = new MultipartRequest(uriUploadImagefile,
				new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response)
					{

						Log.e(TAG, "postMail image fileResuest Response:" + response);
						isPostImageOk = true;

						if (postMailListener != null && isPostImageOk && isPostSpeechOk
								&& isPostSignatureOk && isPostMailOk)
						{
							postMailListener.onResult(true, response.toString());
						}
					}
				}, new Response.ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError error)
					{

						Log.e(TAG, "postMail image fileResuest Error: " + error.getMessage());

						if (!isPostMailErrored)
						{
							postMailListener.onResult(false, error.getMessage());
							isPostMailErrored = true;
						}
					}
				}, imageFile);
		imageFileResuest.setTag("postMail");
		VolleySingleton.getInstance(context).getRequestQueue().add(imageFileResuest);

		File speechFile = new File(speechUri.getPath());

		Log.e(TAG, "postMail speechUri.getPath():" + speechUri.getPath());
		Log.e(TAG, "postMail speechFile.getName():" + speechFile.getName());
		String uriUploadSpeechfile = String.format(
				"http://www.charliefind.com/api.php?op=upload&auth=%1$s&id=%2$s", hash_auth,
				facebookID);

		Log.e(TAG, "postMail uriUploadspeechfile:" + uriUploadSpeechfile);

		MultipartRequest speechFileResuest = new MultipartRequest(uriUploadSpeechfile,
				new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response)
					{

						isPostSpeechOk = true;
						Log.e(TAG, "postMail speech fileResuest Response:" + response);
						if (postMailListener != null && isPostImageOk && isPostSpeechOk
								&& isPostSignatureOk && isPostMailOk)
						{
							postMailListener.onResult(true, response.toString());
						}
					}
				}, new Response.ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError error)
					{

						Log.e(TAG, "postMail speech fileResuest Error: " + error.getMessage());

						if (!isPostMailErrored)
						{
							postMailListener.onResult(false, error.getMessage());
							isPostMailErrored = true;
						}
					}
				}, speechFile);
		speechFileResuest.setTag("postMail");
		VolleySingleton.getInstance(context).getRequestQueue().add(speechFileResuest);

		File signatureFile = new File(signatureUri.getPath());

		Log.e(TAG, "postMail signatureUri.getPath():" + signatureUri.getPath());
		Log.e(TAG, "postMail signatureFile.getName():" + signatureFile.getName());
		String uriUploadSignaturefile = String.format(
				"http://www.charliefind.com/api.php?op=upload&auth=%1$s&id=%2$s", hash_auth,
				facebookID);

		Log.e(TAG, "postMail uriUploadSignaturefile:" + uriUploadSignaturefile);

		MultipartRequest signatureFileResuest = new MultipartRequest(uriUploadSignaturefile,
				new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response)
					{

						isPostSignatureOk = true;
						Log.e(TAG, "postMail signature fileResuest Response:" + response);
						if (postMailListener != null && isPostImageOk && isPostSpeechOk
								&& isPostSignatureOk && isPostMailOk)
						{
							postMailListener.onResult(true, response.toString());
						}
					}
				}, new Response.ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError error)
					{

						Log.e(TAG, "postMail signature fileResuest Error: " + error.getMessage());

						if (!isPostMailErrored)
						{
							postMailListener.onResult(false, error.getMessage());
							isPostMailErrored = true;
						}
					}
				}, signatureFile);
		signatureFileResuest.setTag("postMail");
		VolleySingleton.getInstance(context).getRequestQueue().add(signatureFileResuest);

		String editTextBody = editTextMessage;

		String uriMailPost = String.format("http://www.charliefind.com/api.php?op=mailbox_post");

		HashMap<String, String> paramsFacebookMailPost = new HashMap<String, String>();
		paramsFacebookMailPost.put("card_id", cardID);
		paramsFacebookMailPost.put("send_from", facebookID);
		paramsFacebookMailPost.put("send_to", sendTo);
		paramsFacebookMailPost.put("subject", editTextBody);
		paramsFacebookMailPost.put("font_size", fontSize);
		paramsFacebookMailPost.put("font_color", fontColor);
		paramsFacebookMailPost.put("body", editTextBody);
		paramsFacebookMailPost.put("img", imageFile.getName());
		paramsFacebookMailPost.put("speech", speechFile.getName());
		paramsFacebookMailPost.put("sign", signatureFile.getName());
		paramsFacebookMailPost.put("auth", hash_auth);

		// {"send_from":"1475871733","send_to":"0960162183",
		// "subject":"%E9%80%81%E4%BD%A0%E4%B8%80%E5%80%8B%E8%AE%9A",
		// "body":"%E5%B0%B1%E6%98%AF%E8%AE%9A",
		// "img":"2014-01-28_224232.png"
		// "auth":"X0EPAUABAEBdRmcGSAcFR1xFCw=="}

		Log.e(TAG, "uriMailPost:" + uriMailPost);
		JsonObjectRequest mailPostRequest = new JsonObjectRequest(uriMailPost, new JSONObject(
				paramsFacebookMailPost), new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{

				try
				{
					isPostMailOk = true;
					Log.e(TAG, "postMail mailPost Response:" + response.toString(4));
					if (postMailListener != null && isPostImageOk && isPostSpeechOk
							&& isPostSignatureOk && isPostMailOk)
					{
						postMailListener.onResult(true, response.toString());
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
					if (!isPostMailErrored)
					{
						postMailListener.onResult(false, e.getMessage());
						isPostMailErrored = true;
					}
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{

				Log.e(TAG, "postMail mailPost Error: " + error.getMessage());

				if (!isPostMailErrored)
				{
					postMailListener.onResult(false, error.getMessage());
					isPostMailErrored = true;
				}
			}
		});
		VolleySingleton.getInstance(context).getRequestQueue().add(mailPostRequest);

	}

	public void postMailByList(Context context, ArrayList<String> sendToList, Uri imageUri,
			Uri speechUri, String editTextMessage, Uri signatureUri, String fontSize,
			String fontColor, String cardID, final PostMailListener postMailListener)
			throws Exception
	{

		isPostListImageOk = false;
		isPostListSpeechOk = false;
		isPostListSignatureOk = false;
		isPostListMailOk = false;
		isPostListMailErrored = false;
		File imageFile = new File(imageUri.getPath());

		Log.e(TAG, "postMailByList imageUri.getPath():" + imageUri.getPath());
		Log.e(TAG, "postMailByList imageFile.getName():" + imageFile.getName());
		String uriUploadImagefile = String.format(
				"http://www.charliefind.com/api.php?op=upload&auth=%1$s&id=%2$s", hash_auth,
				facebookID);

		Log.e(TAG, "postMailByList uriUploadImagefile:" + uriUploadImagefile);

		MultipartRequest imageFileResuest = new MultipartRequest(uriUploadImagefile,
				new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response)
					{

						Log.e(TAG, "postMailByList image fileResuest Response:" + response);
						isPostListImageOk = true;

						if (postMailListener != null && isPostListImageOk && isPostListSpeechOk
								&& isPostListSignatureOk)
						{
							boolean isAllMailDone = true;
							for (int index = 0; index < isPostMailOkList.size(); index++)
							{

								if (!isPostMailOkList.get(index))
								{
									isAllMailDone = false;
									break;
								}
							}

							if (isAllMailDone)
							{
								postMailListener.onResult(true, response.toString());
							}
						}
					}
				}, new Response.ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError error)
					{

						Log.e(TAG, "postMailByList image fileResuest Error: " + error.getMessage());

						if (!isPostListMailErrored)
						{
							postMailListener.onResult(false, error.getMessage());
							isPostListMailErrored = true;
						}
					}
				}, imageFile);
		imageFileResuest.setTag("postMailByList");
		VolleySingleton.getInstance(context).getRequestQueue().add(imageFileResuest);

		File speechFile = new File(speechUri.getPath());

		Log.e(TAG, "postMailByList speechUri.getPath():" + speechUri.getPath());
		Log.e(TAG, "postMailByList speechFile.getName():" + speechFile.getName());
		String uriUploadSpeechfile = String.format(
				"http://www.charliefind.com/api.php?op=upload&auth=%1$s&id=%2$s", hash_auth,
				facebookID);

		Log.e(TAG, "postMailByList uriUploadspeechfile:" + uriUploadSpeechfile);

		MultipartRequest speechFileResuest = new MultipartRequest(uriUploadSpeechfile,
				new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response)
					{

						isPostListSpeechOk = true;
						Log.e(TAG, "postMailByList speech fileResuest Response:" + response);
						if (postMailListener != null && isPostListImageOk && isPostListSpeechOk
								&& isPostListSignatureOk)
						{
							boolean isAllMailDone = true;
							for (int index = 0; index < isPostMailOkList.size(); index++)
							{

								if (!isPostMailOkList.get(index))
								{
									isAllMailDone = false;
									break;
								}
							}

							if (isAllMailDone)
							{
								postMailListener.onResult(true, response.toString());
							}
						}
					}
				}, new Response.ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError error)
					{

						Log.e(TAG, "postMail speech fileResuest Error: " + error.getMessage());

						if (!isPostListMailErrored)
						{
							postMailListener.onResult(false, error.getMessage());
							isPostListMailErrored = true;
						}
					}
				}, speechFile);
		speechFileResuest.setTag("postMailByList");
		VolleySingleton.getInstance(context).getRequestQueue().add(speechFileResuest);

		File signatureFile = new File(signatureUri.getPath());

		Log.e(TAG, "postMailByList signatureUri.getPath():" + signatureUri.getPath());
		Log.e(TAG, "postMailByList signatureFile.getName():" + signatureFile.getName());
		String uriUploadSignaturefile = String.format(
				"http://www.charliefind.com/api.php?op=upload&auth=%1$s&id=%2$s", hash_auth,
				facebookID);

		Log.e(TAG, "postMailByList uriUploadSignaturefile:" + uriUploadSignaturefile);

		MultipartRequest signatureFileResuest = new MultipartRequest(uriUploadSignaturefile,
				new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response)
					{

						isPostListSignatureOk = true;
						Log.e(TAG, "postMailByList signature fileResuest Response:" + response);
						if (postMailListener != null && isPostListImageOk && isPostListSpeechOk
								&& isPostListSignatureOk)
						{

							boolean isAllMailDone = true;
							for (int index = 0; index < isPostMailOkList.size(); index++)
							{
								Log.e(TAG,
										"postMailByList signature fileResuest Response:isPostMailOkList.size()"
												+ isPostMailOkList.size());
								if (!isPostMailOkList.get(index))
								{
									isAllMailDone = false;
									break;
								}
							}

							if (isAllMailDone)
							{
								postMailListener.onResult(true, response.toString());
							}

						}
					}
				}, new Response.ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError error)
					{

						Log.e(TAG,
								"postMailByList signature fileResuest Error: " + error.getMessage());

						if (!isPostListMailErrored)
						{
							postMailListener.onResult(false, error.getMessage());
							isPostListMailErrored = true;
						}
					}
				}, signatureFile);
		signatureFileResuest.setTag("postMailByList");
		VolleySingleton.getInstance(context).getRequestQueue().add(signatureFileResuest);

		String editTextBody = editTextMessage;

		String uriMailPost = String.format("http://www.charliefind.com/api.php?op=mailbox_post");

		isPostMailOkList.clear();
		for (int index = 0; index < sendToList.size(); index++)
		{
			isPostMailOkList.add(false);
		}
		Log.e(TAG, "isPostMailOkList.size()" + isPostMailOkList.size());
		parserCount = 0;
		for (int parserIndex = 0; parserIndex < sendToList.size(); parserIndex++)
		{
			HashMap<String, String> paramsFacebookMailPost = new HashMap<String, String>();

			paramsFacebookMailPost.put("card_id", cardID);
			paramsFacebookMailPost.put("send_from", facebookID);
			paramsFacebookMailPost.put("send_to", sendToList.get(parserIndex));
			paramsFacebookMailPost.put("subject", editTextBody);
			paramsFacebookMailPost.put("font_size", fontSize);
			paramsFacebookMailPost.put("font_color", fontColor);
			paramsFacebookMailPost.put("body", editTextBody);
			paramsFacebookMailPost.put("img", imageFile.getName());
			paramsFacebookMailPost.put("speech", speechFile.getName());
			paramsFacebookMailPost.put("sign", signatureFile.getName());
			paramsFacebookMailPost.put("auth", hash_auth);

			Log.e(TAG, "uriMailPost:" + uriMailPost);
			JsonObjectRequest mailPostRequest = new JsonObjectRequest(uriMailPost, new JSONObject(
					paramsFacebookMailPost), new Response.Listener<JSONObject>()
			{
				@Override
				public void onResponse(JSONObject response)
				{

					try
					{

						isPostMailOkList.set(parserCount, true);
						parserCount++;
						Log.e(TAG, "postMailByList mailPostList Response:" + response.toString(4));
						if (postMailListener != null && isPostListImageOk && isPostListSpeechOk
								&& isPostListSignatureOk)
						{

							boolean isAllMailDone = true;
							for (int index = 0; index < isPostMailOkList.size(); index++)
							{

								if (!isPostMailOkList.get(index))
								{
									isAllMailDone = false;
									break;
								}
							}

							if (isAllMailDone)
							{
								postMailListener.onResult(true, response.toString());
							}

						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
						if (!isPostListMailErrored)
						{
							postMailListener.onResult(false, e.getMessage());
							isPostListMailErrored = true;
						}
					}
				}
			}, new Response.ErrorListener()
			{
				@Override
				public void onErrorResponse(VolleyError error)
				{

					Log.e(TAG, "postMailByList mailPost Error: " + error.getMessage());

					if (!isPostListMailErrored)
					{
						postMailListener.onResult(false, error.getMessage());
						isPostListMailErrored = true;
					}
				}
			});
			VolleySingleton.getInstance(context).getRequestQueue().add(mailPostRequest);
		}
	}

	public void getUnreadMailCount(Context context, final MailCountListener mailCountListener)
	{

		String uriMailReceiveCount;
		if (mobile != null)
		{
			uriMailReceiveCount = String
					.format("http://www.charliefind.com/api.php?op=mailbox_rece&auth=%1$s&id=%2$s&mobile=%3$s&imei=%4$s",
							hash_auth, facebookID, mobile, deviceIMEI);
		}
		else
		{
			uriMailReceiveCount = String
					.format("http://www.charliefind.com/api.php?op=mailbox_rece&auth=%1$s&id=%2$s&imei=%3$s",
							hash_auth, facebookID, deviceIMEI);
		}

		// HashMap<String, String> paramsMailReceiveCount = new HashMap<String,
		// String>();
		// paramsMailReceiveCount.put("auth", auth);
		// paramsMailReceiveCount.put("id", fbID);
		// paramsMailReceiveCount.put("mobile", "0939918739");
		// paramsMailReceiveCount.put("imei", "123456789");

		Log.e(TAG, "uriMailReceiveCount:" + uriMailReceiveCount);
		JsonObjectRequest mailReceiveCountRequest = new JsonObjectRequest(Method.GET,
				uriMailReceiveCount, null, new Response.Listener<JSONObject>()
				{
					@Override
					public void onResponse(JSONObject response)
					{

						try
						{
							Log.e(TAG,
									"getUnreadMailCount MailReceiveCount Response:"
											+ response.toString(4));

							Log.e(TAG,
									"getUnreadMailCount MailReceiveCount Response:"
											+ response.get("mailbox_rece"));
							if (mailCountListener != null)
								mailCountListener.onResult(true,
										Integer.valueOf(response.get("mailbox_rece").toString()));

						}
						catch (JSONException e)
						{
							e.printStackTrace();
							if (mailCountListener != null) mailCountListener.onResult(false, -1);
						}
					}
				}, new Response.ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError error)
					{

						Log.e(TAG,
								"getUnreadMailCount MailReceiveCount Error: " + error.getMessage());

						if (mailCountListener != null) mailCountListener.onResult(false, -1);
					}
				});
		VolleySingleton.getInstance(context).getRequestQueue().add(mailReceiveCountRequest);
	}

	public void getMails(Context context, final GetMailListener getMailListener)
	{

		java.lang.reflect.Type typeSend = new com.google.gson.reflect.TypeToken<ArrayList<GsonSend>>()
		{
		}.getType();

		String uri_getMail;
		if (mobile != null)
		{
			uri_getMail = String
					.format("http://www.charliefind.com/api.php?op=mailbox&auth=%1$s&id=%2$s&mobile=%3$s&imei=%4$s",
							hash_auth, facebookID, mobile, deviceIMEI);
		}
		else
		{
			uri_getMail = String.format(
					"http://www.charliefind.com/api.php?op=mailbox&auth=%1$s&id=%2$s&imei=%3$s",
					hash_auth, facebookID, deviceIMEI);
		}

		Log.e(TAG, "uri_getMail:" + uri_getMail);
		GsonListRequest<ArrayList<GsonSend>> getMailGsonRequset = new GsonListRequest<ArrayList<GsonSend>>(
				Method.GET, uri_getMail, typeSend,
				createGetMailReqSuccessListener(getMailListener), createGetMailReqErrorListener());
		getMailGsonRequset.setTag("getMails");
		VolleySingleton.getInstance(context).getRequestQueue().add(getMailGsonRequset);
	}

	public void notifyMailsRead(Context context, final NotifyMailReadListener notifyMailReadListener)
	{

		String uriMailUpate = String.format("http://www.charliefind.com/api.php?op=mailbox_update");

		HashMap<String, String> paramsMailUpdate = new HashMap<String, String>();

		paramsMailUpdate.put("send_to", facebookID);
		paramsMailUpdate.put("imei", deviceIMEI);
		paramsMailUpdate.put("auth", hash_auth);
		if (mobile != null)
		{
			paramsMailUpdate.put("mobile", mobile);
		}
		// {"send_to":"1475871733","mobile":"0960162183","imei":"359614040330792",
		// ,"auth":"X0EPAUABAEBdRmcGSAcFR1xFCw=="}

		Log.e(TAG, "uriMailUpate:" + uriMailUpate);
		JsonObjectRequest mailUpdateRequest = new JsonObjectRequest(uriMailUpate, new JSONObject(
				paramsMailUpdate), new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response)
			{

				try
				{
					Log.e(TAG, "mailUpdate Response:" + response.toString(4));
					if (notifyMailReadListener != null)
						notifyMailReadListener.onResult(true, response.toString());
				}
				catch (JSONException e)
				{
					if (notifyMailReadListener != null)
						notifyMailReadListener.onResult(false, e.toString());
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{

				if (notifyMailReadListener != null)
					notifyMailReadListener.onResult(false, error.getMessage());
				Log.e(TAG, "mailUpdate Error: " + error.getMessage());
			}
		});
		mailUpdateRequest.setTag("notifyMailsRead");
		VolleySingleton.getInstance(context).getRequestQueue().add(mailUpdateRequest);
	}

	public void getRecommend(Context context, GetRecommendListener getRecommendListener)
	{

		java.lang.reflect.Type typeSend = new com.google.gson.reflect.TypeToken<ArrayList<GsonRecommend>>()
		{
		}.getType();

		String uriGetRecommend = "http://www.charliefind.com/api.php?op=recomand";

		Log.e(TAG, "uriGetRecommend:" + uriGetRecommend);
		GsonListRequest<ArrayList<GsonRecommend>> getRecommendGsonRequset = new GsonListRequest<ArrayList<GsonRecommend>>(
				Method.GET, uriGetRecommend, typeSend,
				createGetRecommendGsonReqSuccessListener(getRecommendListener),
				createGetRecommendGsonReqErrorListener());
		getRecommendGsonRequset.setTag("getRecommend");
		VolleySingleton.getInstance(context).getRequestQueue().add(getRecommendGsonRequset);
	}
	
	
	public void getCategory(Context context, GetCategoryListener getCategoryListener)
	{

		java.lang.reflect.Type typeSend = new com.google.gson.reflect.TypeToken<ArrayList<GsonCategory>>()
		{
		}.getType();

		String uriGetCategory = "http://www.charliefind.com/api.php?op=card_cat";

		Log.e(TAG, "uriGetCategory:" + uriGetCategory);
		GsonListRequest<ArrayList<GsonCategory>> getCategoryGsonRequset = new GsonListRequest<ArrayList<GsonCategory>>(
				Method.GET, uriGetCategory, typeSend,
				createGetCategoryGsonReqSuccessListener(getCategoryListener),
				createGetCategoryGsonReqErrorListener());
		getCategoryGsonRequset.setTag("getCategory");
		VolleySingleton.getInstance(context).getRequestQueue().add(getCategoryGsonRequset);
	}
	
	public void getCard(Context context, GetCardListener getCardListener)
	{

		java.lang.reflect.Type typeSend = new com.google.gson.reflect.TypeToken<ArrayList<GsonCard>>()
		{
		}.getType();

		String uriGetCard = "http://www.charliefind.com/api.php?op=card";

		Log.e(TAG, "uriGetCard:" + uriGetCard);
		GsonListRequest<ArrayList<GsonCard>> getCardGsonRequset = new GsonListRequest<ArrayList<GsonCard>>(
				Method.GET, uriGetCard, typeSend,
				createGetCardGsonReqSuccessListener(getCardListener),
				createGetCardGsonReqErrorListener());
		getCardGsonRequset.setTag("getCard");
		VolleySingleton.getInstance(context).getRequestQueue().add(getCardGsonRequset);
	}

	public void uploadDIY(Context context, Uri diyUri, final UploadDiyListener uploadDiyListener)
	{

		File diyFile = new File(diyUri.getPath());

		Log.e(TAG, "uploadDIY diyUri.getPath():" + diyUri.getPath());
		Log.e(TAG, "uploadDIY diyFile.getName():" + diyFile.getName());
		String uriUploadDIYfile = String.format(
				"http://www.charliefind.com/api.php?op=upload_diy&auth=%1$s&id=%2$s", hash_auth,
				facebookID);

		Log.e(TAG, "uploadDIY uriUploadDIYfile:" + uriUploadDIYfile);

		MultipartJsonObjectRequest diyFileResuest = new MultipartJsonObjectRequest(
				uriUploadDIYfile, new Response.Listener<String>()
				{
					@Override
					public void onResponse(String response)
					{

						try
						{

							response = URLDecoder.decode(URLDecoder.decode(response));

							JSONObject jsonObj = new JSONObject(response);

							uploadDiyListener.onResult(true, jsonObj.getString("upload_diy"));
						}
						catch (JSONException e)
						{
							uploadDiyListener.onResult(false, e.getMessage());
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError error)
					{

						Log.e(TAG, "uploadDIY diyFileResuest Error: " + error.getMessage());

						uploadDiyListener.onResult(false, error.getMessage());

					}
				}, diyFile);
		diyFileResuest.setTag("uploadDIY");
		VolleySingleton.getInstance(context).getRequestQueue().add(diyFileResuest);

	}

	public ImageLoader getImageLoader(Context context)
	{

		return VolleySingleton.getInstance(context).getImageLoader();
	}

	public void getBitmapFromWeb(Context context, String url,
			ImageLoader.ImageListener imageListener)
	{

		ImageLoader imageLoader = VolleySingleton.getInstance(context).getImageLoader();
		imageLoader.get(url, imageListener);
	}

	private Response.Listener<ArrayList<GsonSend>> createGetMailReqSuccessListener(
			final GetMailListener getMailListener)
	{

		return new Response.Listener<ArrayList<GsonSend>>()
		{
			@Override
			public void onResponse(ArrayList<GsonSend> response)
			{

				Log.e(TAG, "GsonSend response: " + response.toString());

				if (getMailListener != null) getMailListener.onResult(true, response);
			}
		};
	}

	private Response.ErrorListener createGetMailReqErrorListener()
	{

		return new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{

				Log.e(TAG, "GsonSend error: " + error.toString());
			}
		};

	}

	private Response.Listener<ArrayList<GsonCard>> createGetCardGsonReqSuccessListener(
			final  GetCardListener getCardListener)
	{

		return new Response.Listener<ArrayList<GsonCard>>()
		{
			@Override
			public void onResponse(ArrayList<GsonCard> response)
			{

				Log.e(TAG, "GsonCard response: " + response.toString());

				if (getCardListener != null) getCardListener.onResult(true, response);
			}
		};
	}
	
	private Response.Listener<ArrayList<GsonCategory>> createGetCategoryGsonReqSuccessListener(
			final  GetCategoryListener getCategoryListener)
	{

		return new Response.Listener<ArrayList<GsonCategory>>()
		{
			@Override
			public void onResponse(ArrayList<GsonCategory> response)
			{

				Log.e(TAG, "GsonCategory response: " + response.toString());

				if (getCategoryListener != null) getCategoryListener.onResult(true, response);
			}
		};
	}
	
	
	private Response.Listener<ArrayList<GsonRecommend>> createGetRecommendGsonReqSuccessListener(
			final GetRecommendListener getRecommendListener)
	{

		return new Response.Listener<ArrayList<GsonRecommend>>()
		{
			@Override
			public void onResponse(ArrayList<GsonRecommend> response)
			{

				Log.e(TAG, "GsonRecommend response: " + response.toString());

				if (getRecommendListener != null) getRecommendListener.onResult(true, response);
			}
		};
	}

	private Response.Listener<ArrayList<GsonFacebookUser>> createGetFacebookUserInfoGsonReqSuccessListener(
			final GetFacebookInfoListener getFacebookInfoListener)
	{

		return new Response.Listener<ArrayList<GsonFacebookUser>>()
		{
			@Override
			public void onResponse(ArrayList<GsonFacebookUser> response)
			{

				Log.e(TAG,
						"GetFacebookUserInfoGsonReqSuccessListener response: "
								+ response.toString());

				if (getFacebookInfoListener != null)
					getFacebookInfoListener.onResult(true, response);
			}
		};
	}

	private Response.ErrorListener createFacebookUserInfoGsonReqErrorListener()
	{

		return new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{

				Log.e(TAG, "FacebookUserInfoGsonReqErrorListener error: " + error.toString());
			}
		};

	}

	private Response.ErrorListener createGetCardGsonReqErrorListener()
	{

		return new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{

				Log.e(TAG, "GsonCard error: " + error.toString());
			}
		};

	}
	
	private Response.ErrorListener createGetCategoryGsonReqErrorListener()
	{

		return new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{

				Log.e(TAG, "GsonCategory error: " + error.toString());
			}
		};

	}
	
	private Response.ErrorListener createGetRecommendGsonReqErrorListener()
	{

		return new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{

				Log.e(TAG, "GsonRecommend error: " + error.toString());
			}
		};

	}

	private String getStringFromFile(String filePath) throws Exception
	{

		File fl = new File(filePath);
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);

		fin.close();
		return ret;
	}

	private String convertStreamToString(InputStream is) throws Exception
	{

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	public static String getFacebookID()
	{

		return facebookID;
	}

	public static void setFacebookID(String facebookID)
	{

		HttpManager.facebookID = facebookID;
	}
}
