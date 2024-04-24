package org.k11techlab.framework.selenium.coreframework.commonUtil.fileHandler;

import com.google.gson.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONUtil {
    private static final Log logger = LogFactoryImpl.getLog(JSONUtil.class);

	public static Map<String, Object> toMap(String json) throws JSONException {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = new Gson().fromJson(json, Map.class);
		return map;
	}

	public static boolean isValidJsonString(String str) {
		try {
			new JSONObject(str);
			return true;
		} catch (JSONException e) {
			return false;
		}

	}

	/**
	 * This is specifically to work with GSON. for example even "some string" is
	 * not valid json but gson can considers as valid json
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isValidGsonString(String str) {
		try {
			new JsonParser().parse(str);
			return true;
		} catch (JsonParseException e) {
			return false;
		}

	}

	/**
	 * @param obj
	 * @return
	 */
	static Map<String, Object> toMap(JSONObject obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (obj != null) {
			Iterator<?> iter = obj.keys();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				try {
					map.put(key, obj.get(key));
				} catch (JSONException e) {

				}
			}
		}
		return map;
	}

	/**
	 * @param str
	 * @return JsonElement or null if not a valid json
	 */
	public static JsonElement getGsonElement(String str) {
		try {
			return new JsonParser().parse(str);
		} catch (JsonParseException e) {
			return null;
		}
	}

	public static <T> void writeJsonObjectToFile(final String file, final T obj) {

		File f = new File(file);
		try {
			Gson gson =
					new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
			String jsonStr = gson.toJson(obj, obj.getClass());

			FileUtil.writeStringToFile(f, jsonStr, "UTF-8");
		} catch (Throwable e) {
			System.err.println("Unable to write : " + obj.getClass().getCanonicalName()
					+ " in file: " + file + " :" + e.getMessage());
			logger.error("Unable to write : " + obj.getClass().getCanonicalName()
					+ " in file: " + file + " :" + e.getMessage());
		}
	}



}
