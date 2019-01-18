package hr.foi.ints;

import io.jenetics.jpx.WayPoint;

public class City {
	private static int idIncrement = 0;


	private final int _id;
	private final int orderNumber;
	private double installationCost;

    public void setInstallationCost(double installationCost) {
        this.installationCost = installationCost;
    }
	private final String _name;
	private WayPoint locationData;

	public static int getIdIncrement() {
		return idIncrement;
	}

	public static void setIdIncrement(int idIncrement) {
		City.idIncrement = idIncrement;
	}

	public int get_id() {
		return _id;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public String get_name() {
		return _name;
	}

	public WayPoint getLocationData() {
		return locationData;
	}

	public void setLocationData(WayPoint locationData) {
		this.locationData = locationData;
	}

	public City(int orderNumber, String _name) {
		this._id = City.idIncrement++;
		this.orderNumber = orderNumber;
		this._name = _name;
	}

    public double getInstallationCost() {
        return installationCost;
    }
}
