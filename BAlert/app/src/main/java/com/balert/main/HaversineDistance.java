/**
 * 
 */
package com.balert.main;

public class HaversineDistance {

	private static final double R = 6372.8; // In kilometers

	public static double getDistance(double sourceLatitude,
			double sourceLongitude, double destinationLatitude,
			double destinationLongitude) {
		double dLat = Math.toRadians(destinationLatitude - sourceLatitude);
		double dLon = Math.toRadians(destinationLongitude - sourceLongitude);
		sourceLatitude = Math.toRadians(sourceLatitude);
		destinationLatitude = Math.toRadians(destinationLatitude);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(sourceLatitude)
				* Math.cos(destinationLatitude);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}
}