package com.support.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser {

	/**
	 * Receives a JSONObject and returns a list of lists containing latitude and longitude
	 */
	public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;

		try {

			jRoutes = jObject.getJSONArray("routes");

			/** Traversing all routes */
			for (int i = 0; i < jRoutes.length(); i++) {
				jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
				List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

				/** Traversing all legs */
				for (int j = 0; j < jLegs.length(); j++) {
					jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

					/** Traversing all steps */
					for (int k = 0; k < jSteps.length(); k++) {
						String polyline = "";
						polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
						List<LatLng> list = decodePoly(polyline);

						/** Traversing all points */
						for (int l = 0; l < list.size(); l++) {
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
							hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
							path.add(hm);
						}
					}
					routes.add(path);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return routes;
	}

	public String parseEstimate(JSONObject jObject) {

		JSONArray jRoutes = null;
		JSONArray jLegs = null;

		String ret = "";
		try {

			jRoutes = jObject.getJSONArray("routes");

			/** Traversing all routes */
			if (jRoutes != null && jRoutes.length() > 0) {
				jLegs = ((JSONObject) jRoutes.get(0)).getJSONArray("legs");
				if (jLegs != null && jLegs.length() > 0) {
					int durationInt = 0;
					int distanceInt = 0;
					for (int i = 0; i < jLegs.length(); i++) {
						JSONObject leg = (JSONObject) jLegs.get(i);
						JSONObject distance = leg.getJSONObject("distance");
						JSONObject duration = leg.getJSONObject("duration");

						durationInt += duration.getInt("value");
						distanceInt += distance.getInt("value");
					}
					float miles = convertToMiles(distanceInt);
					return "Your estimated journey time is " + convertToHours(durationInt / 60) + " and total journey is " + String.format("%.2f", miles)
							+ " miles.";
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	private float convertToMiles(int meters) {
		return meters * 0.000621371f;
	}

	private String convertToHours(int minutes) {
		if (minutes >= 60) {
			int hours = minutes / 60;
			String ret = "";
			if (hours > 1) {
				ret += hours + " hours";
			} else {
				ret += hours + " hour";
			}
			if (minutes % 60 != 0)
				ret += " and " + convertToHours(minutes % 60);

			return ret;
		} else {
			if (minutes > 1)
				return minutes + " mins";
			else
				return minutes + " min";
		}
	}

	/**
	 * Method to decode polyline points Courtesy : http://jeffreysambells.com/2010 /05/27/decoding-polylines-from-google-maps-direction-api-with-java
	 * */
	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}
}
