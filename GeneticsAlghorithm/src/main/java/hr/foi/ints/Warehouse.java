package hr.foi.ints;

import io.jenetics.internal.util.require;
import io.jenetics.jpx.Point;
import io.jenetics.jpx.WayPoint;

import java.io.Serializable;
import java.util.Random;
import java.util.stream.Collector;

import static java.lang.String.format;

public final class Warehouse implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int idIncrement = 0;

    private double _installationCost;
    private final int _id;
    private WayPoint locationData;
    private String _name;
    private String _locationName;

    public Warehouse() {
        _id = Warehouse.idIncrement++;// require.nonNegative(id);
    }

    public Point getLocationData() {
        return locationData;
    }

    public void setLocationData(WayPoint locationData) {
        this.locationData = locationData;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_locationName() {
        return _locationName;
    }

    public void set_locationName(String _locationName) {
        this._locationName = _locationName;
    }

    public int get_id() {
        return _id;
    }

    public double get_installationCost() {
        return _installationCost;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        long bits = Double.doubleToLongBits(_installationCost);
        hash = 31 * hash + (int) (bits ^ (bits >>> 32));

        bits = Double.doubleToLongBits(_id);
        return 31 * hash + (int) (bits ^ (bits >>> 32));
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Warehouse
                && Double.compare(_installationCost, ((Warehouse) obj)._installationCost) == 0
                && Double.compare(_id, ((Warehouse) obj)._id) == 0;
    }

    @Override
    public String toString() {
        return format("Warehouse[size=%f, value=%f]", _installationCost, _id);
    }

    public static Warehouse of(  ) {
        return new Warehouse();
    }

    /**
     * @param random
     * @return
     */
    public static Warehouse random(final Random random) {
        return new Warehouse();
    }

    /**
     * @return
     */
    public static Collector<Warehouse, ?, Warehouse> toSum() {
        return Collector.of(
                () -> new int[2],
                (a, b) -> {
                    a[0] += b._installationCost;
                    a[1] += b._id;
                },
                (a, b) -> {
                    a[0] += b[0];
                    a[1] += b[1];
                    return a;
                },
                r -> new Warehouse()
        );
    }

    public double getInstallationCost() {
        return _installationCost;
    }

    public void setInstallationCost(double _installationCost) {
        this._installationCost = _installationCost;
    }
}
