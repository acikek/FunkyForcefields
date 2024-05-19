package net.modfest.funkyforcefields.transport;

import net.modfest.funkyforcefields.regions.ForcefieldFluid;

import java.util.Objects;

public class FluidContainerImpl implements FluidContainer {

	final float containerVolume;
	float pressure = TransportUtilities.NOMINAL_PRESSURE;
	final float thermalDiffusivity;
	float temperature = TransportUtilities.NOMINAL_TEMPERATURE;
	ForcefieldFluid containedFluid;

	public FluidContainerImpl(float containerVolume, float thermalDiffusivity) {
		this.containerVolume = containerVolume;
		this.thermalDiffusivity = thermalDiffusivity;
	}

	@Override
	public float getContainerVolume() {
		return containerVolume;
	}

	@Override
	public float getPressure() {
		return pressure;
	}

	public void setPressure(float pressure) {
		this.pressure = pressure;
	}

	@Override
	public float getThermalDiffusivity() {
		return thermalDiffusivity;
	}

	@Override
	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	@Override
	public ForcefieldFluid getContainedFluid() {
		return containedFluid;
	}

	public void setContainedFluid(ForcefieldFluid containedFluid) {
		this.containedFluid = containedFluid;
	}

	@Override
	public void tick(FluidContainer... neighbors) {
		if (containedFluid == null) {
			FluidContainer biggestNeighbor = null;
			for (FluidContainer neighbor : neighbors) {
				if (neighbor.getContainedFluid() != null) {
					if (biggestNeighbor == null) {
						biggestNeighbor = neighbor;
					}
					else if (neighbor.getPressure() > biggestNeighbor.getPressure()) {
						biggestNeighbor = neighbor;
					}
				}
			}
			if (biggestNeighbor != null && biggestNeighbor.getPressure() > pressure) {
				containedFluid = biggestNeighbor.getContainedFluid();
			}
		}
		float[] neighborValues = new float[neighbors.length];
		for (int i = 0; i < neighbors.length; i++) {
			neighborValues[i] = neighbors[i].getPressure();
		}
		pressure = TransportUtilities.tickPressure(containerVolume, pressure, neighborValues);
		for (int i = 0; i < neighbors.length; i++) {
			neighborValues[i] = neighbors[i].getTemperature();
		}
		temperature = TransportUtilities.tickTemperature(thermalDiffusivity, temperature, neighborValues);
		if (Math.abs(pressure - TransportUtilities.NOMINAL_PRESSURE) <= TransportUtilities.NEGLIGIBILITY) {
			containedFluid = null;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FluidContainerImpl that = (FluidContainerImpl) o;
		return Float.compare(that.getContainerVolume(), getContainerVolume()) == 0 &&
				Float.compare(that.getPressure(), getPressure()) == 0 &&
				Float.compare(that.getThermalDiffusivity(), getThermalDiffusivity()) == 0 &&
				Float.compare(that.getTemperature(), getTemperature()) == 0 &&
				Objects.equals(getContainedFluid(), that.getContainedFluid());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getContainerVolume(), getPressure(), getThermalDiffusivity(), getTemperature(), getContainedFluid());
	}
}
