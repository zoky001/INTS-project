package hr.foi.ints;


import io.jenetics.jpx.Point;
import io.jenetics.jpx.WayPoint;

public class RetailsStore {
	private static int idIncrement = 0;


	private final int _id;
	private final String _name;
	private final String _locationName;
	private WayPoint locationData;

	public RetailsStore(String _name, String _locationName, WayPoint locationData) {
		this._id = RetailsStore.idIncrement++;
		this._name = _name;
		this._locationName = _locationName;
		this.locationData = locationData;
	}

	public static int getIdIncrement() {
		return idIncrement;
	}

	public int get_id() {
		return _id;
	}

	public String get_name() {
		return _name;
	}

	public String get_locationName() {
		return _locationName;
	}

	public WayPoint getLocationData() {
		return locationData;
	}
}
