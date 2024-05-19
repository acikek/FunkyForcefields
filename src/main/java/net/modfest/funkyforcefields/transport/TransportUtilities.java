package net.modfest.funkyforcefields.transport;

public class TransportUtilities {

	private TransportUtilities() {
	}

	public static float NOMINAL_PRESSURE = 200;
	public static float NOMINAL_TEMPERATURE = 290;
	public static float ENVIRONMENT_DIFFUSIVITY = 0.0003F;
	public static float ENVIRONMENT_DEPRESSURISATION = 0.00005F;
	public static float NEGLIGIBILITY = 1F;

	public static float tickTemperature(float thermalDiffusivity, float currTemperature, float... neighbourTemperatures) {
		if (neighbourTemperatures.length > 0) {
			float avgNeighbourTemp = 0;
			for (float temp : neighbourTemperatures) {
				avgNeighbourTemp += temp;
			}
			avgNeighbourTemp /= neighbourTemperatures.length;
			currTemperature += (avgNeighbourTemp - currTemperature) * thermalDiffusivity;
		}
		currTemperature += (NOMINAL_TEMPERATURE - currTemperature) * ENVIRONMENT_DIFFUSIVITY;
		return currTemperature;
	}

	public static float tickPressure(float volume, float currPressure, float... neighbourPressures) {
		if (neighbourPressures.length > 0) {
			float avgNeighbourPressure = 0;
			for (float pressure : neighbourPressures) {
				avgNeighbourPressure += pressure;
			}
			avgNeighbourPressure /= neighbourPressures.length;
			float volRate;
			if (volume == 0) {
				volRate = 1;
			}
			else {
				volRate = (float) (10 * Math.pow(volume, -2));
				if (volRate > 1) {
					volRate = 1;
				}
			}
			currPressure += (avgNeighbourPressure - currPressure) * volRate;
		}
		currPressure += (NOMINAL_PRESSURE - currPressure) * ENVIRONMENT_DEPRESSURISATION;
		return currPressure;
	}
}
