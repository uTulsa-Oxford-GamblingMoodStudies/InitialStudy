package edu.utulsa.ibcb.moodstudy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.content.Context;
import android.util.Base64;

public class RpcClient {

	public static final String USERNAME_OPTION = "username";
	public static final String PASSWORD_OPTION = "password";

	private static final String configFile = ".settings";

	private static final String protocol = "https";
	private static final String RPCserver = "129.244.245.205";
	// private static final String RPCport = "80";
	
	/** name of the php file server side procedure calls are to**/
	public static String RPCscript = "service.php";

	private static RpcClient instance = new RpcClient();

	private boolean loaded = false;
	private XMLRPCClient client = null;
	private ConcurrentHashMap<String, String> options = null;

	private int session = -1;
	private int pid = -1;

	public void setSession(int session_id) {
		session = session_id;
	}

	public int getSessionId() {
		return session;
	}

	public static RpcClient getInstance(Context context) {
		instance.ensureLoaded(context);
		return instance;
	}

	/** never used, consider removing**/
	public void upload(byte[] data) throws XMLRPCException {
		String base64data = Base64.encodeToString(data, Base64.NO_WRAP);
		client.call("upload", options.get("username"), options.get("password"),
				base64data);
	}

	/** @param control integer value from 0-100 generated by asking the user how lucky the feel at the session start**/
	public Integer startSession(int lucky) throws XMLRPCException {
/*		Object ret = client.call("startSession", options.get("username"),
				options.get("password"), lucky);
		System.out.println((String) ret);
		session = Integer.parseInt((String) ret);
		return session;
*/	return 1;
	}
	/** @param control integer value from 0-100 generated by asking the user how much they had this session**/
	public Boolean finalizeSession(int control) throws XMLRPCException {
/*		return (Boolean) client.call("finalizeSession",
				options.get("username"), options.get("password"), session,
				control);
*/	return true;
	}

	public void uploadSurveyData(int surveyVersionNumber, int[] responses) throws XMLRPCException {
		//TODO upload pid and survey results
	}
	
	
	/**
	 * 
	 * @param luckyFeeling
	 * @param prompt
	 * @param actual
	 * @param timestamps of when each data point was collected
	 * @param ax accelerometer readings on the x-axis for each data point
	 * @param ay accelerometer readings on the y-axis for each data point
	 * @param az accelerometer readings on the z-axis for each data point
	 * @param hasGryo 
	 * @param gx only valid if hasGyro
	 * @param gy only valid if hasGyro
	 * @param gz only valid if hasGyro
	 */
	public void uploadSensorData(int luckyFeeling, int prompt, int actual,
			long[] timestamps, double[] ax, double[] ay, double[] az, boolean hasGryo,
			double[] gx, double[] gy, double[] gz) throws XMLRPCException {
		//TODO upload pid and parameters
	}
	
	private void uploadSensorData(int play_id, long time_stamp, double ax, double ay, double az, boolean has_gyro, double gx, double gy, double gz) throws XMLRPCException {
	//	client.call("post_accel_data", options.get("username", options.get("password"), play_id, time_stamp, ax, ay, az, has_gyro, gx, gy, gz));
	}

	public Boolean login(String user, String pass) throws XMLRPCException {
/*		return (Boolean) client.call("login", user, pass);
*/	return true;
	}

	public Integer score() throws XMLRPCException {
/*		Object ret = client.call("score", options.get("username"),
				options.get("password"));
		System.out.println((String) ret);
		return Integer.parseInt((String) ret);
*/	
		return 1;
	}

	public int[] play() throws XMLRPCException {
/*		String rval = (String) client.call("play", options.get("username"),
				options.get("password"), session);
		String[] items = rval.split(" ");
		pid = Integer.parseInt(items[2]);
		return new int[] { Integer.parseInt(items[0]),
				Integer.parseInt(items[1]) };
*/	return new int[]{1,1};
	}

	public Object[] requestCaptcha() throws XMLRPCException {
		return (Object[]) client.call("requestCode");
	}

	public boolean register(String user, String pass, String name,
			String captcha, String session) throws XMLRPCException {
		return (Boolean) client.call("register", user, pass, name, captcha,
				session);

	}

	private synchronized void ensureLoaded(Context context) {
		if (!loaded) {
			String url = protocol + "://" + RPCserver /* + ":" + RPCport */+ "/"
					+ RPCscript;
			client = new XMLRPCClient(url, context);
			options = new ConcurrentHashMap<String, String>();
			
			// temporary measure until a permanent login system is decided upon
			/**/setOptions(context,"username","matt.matlock@gmail.com","password","z38lives");
			
			load(context);
		}
	}

	public synchronized String getOption(String option) {
		if (options.containsKey(option))
			return options.get(option);
		return null;
	}

	public synchronized void deleteOptions(Context context, String... args) {
		for (String option : args) {
			options.remove(option);
		}

		save(context);
	}

	public synchronized void setOptions(Context context, String... args) {
		for (int i = 0; i < args.length; i += 2) {
			options.put(args[i], args[i + 1]);
		}

		save(context);
	}

	private void load(Context context) {
		try {
			FileInputStream fis = context.openFileInput(configFile);

			DataInputStream dataIn = new DataInputStream(fis);

			String line = "";
			while ((line = dataIn.readLine()) != null) {
				if (line.contains("=")) {
					String[] items = line.split("=");

					options.put(items[0], items[1]);
				}
			}

			dataIn.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		loaded = true;
	}

	private void save(Context context) {
		try {
			FileOutputStream fos = context.openFileOutput(configFile,
					Context.MODE_PRIVATE);
			DataOutputStream dataOut = new DataOutputStream(fos);

			for (String key : options.keySet()) {
				dataOut.writeChars(key + "=" + options.get(key) + "\n");
			}
			dataOut.close();

		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}


}
