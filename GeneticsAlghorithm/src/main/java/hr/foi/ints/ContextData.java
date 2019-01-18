package hr.foi.ints;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Longitude;
import io.jenetics.jpx.WayPoint;
import io.jenetics.util.ISeq;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ContextData {

    private String CONFIG_FILE = "config.csv";

    private static final String transportationCostPerKilometerName = "transportationCostPerKilometer";
    private static final String retailStoresFileName = "retailStoresFile";
    private static final String warehouseFileName = "warehouseFile";

    private static String RETAILS_STORES_FILE = "";
    private static String BIG_CITIES_FILE = "";
    private double transportationCostPerKilometer;

    private static final int CITY_NAME_INDEX = 0;
    private static final int LATITUDE_DECIMAL_INDEX = 3;
    private static final int LONGITUDE_DECIMAL_INDEX = 4;

    private static final int ORDER_NUMBER_INDEX = 0;
    private static final int BIG_CITY_NAME_INDEX = 1;
    private static final int BIG_CITY_COST_INDEX = 2;

    private List<Warehouse> warehouseList = new ArrayList<Warehouse>();
    private List<RetailsStore> retailsStoreList = new ArrayList<RetailsStore>();
    private List<City> cityList = new ArrayList<City>();

    private double totalMaxiumCost = 0;

    public boolean saveBestSolution = false;
    public ISeq<Warehouse> bestSolution;

    public ContextData() {
        loadAllData();
    }

    private void loadAllData() {

        loadConfigDataFromCSV();

        loadRetailsStoreData();

        loadWarehouseData();
//        this.transportationCostPerKilometer = 250.50;

        this.totalMaxiumCost = SharedMethods.calculateMinimumCost(ISeq.of(this.warehouseList), this);

        printInfo();
    }

    private void loadConfigDataFromCSV() {
//TODO treba validirati
        BufferedReader br = null;
        String line = " ";
        String cvsSplitBy = ";";
        File file = new File(CONFIG_FILE);
        RetailsStore retailsStore;

        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF8"));

            int lineNo = 0;
            while ((line = br.readLine()) != null) {

                if (line.equalsIgnoreCase("")) {
                    continue;
                }
                // use comma as separator
                String[] data = line.split(cvsSplitBy);

                if (data.length != 2) {
                    System.out.println("Pogrešan zapis spremnika u datoteci!! Zapis: " + line);
                    continue;
                }

                String key = data[0].trim();
                String value = data[1].trim();

                if (key.equalsIgnoreCase(transportationCostPerKilometerName)) {
                    double valueDouble = Double.parseDouble(value);
                    this.transportationCostPerKilometer = valueDouble;
                } else if (key.equalsIgnoreCase(retailStoresFileName)) {
                    this.RETAILS_STORES_FILE = value;
                } else if (key.equalsIgnoreCase(warehouseFileName)) {
                    this.BIG_CITIES_FILE = value;
                }

            }

        } catch (FileNotFoundException e) {
            System.out.println("Pogrešan zapis spremnika u datoteci!!");

            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Pogrešan zapis spremnika u datoteci!!");

            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void printInfo() {
        double sum = 0;
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("Ukupna cijena izgradnje svih skladišta");
        System.out.println("+---------------------------------------------------------------------------+");

        for (Warehouse v
                : this.warehouseList) {
            sum = sum + v.get_installationCost();

            System.out.println(v.get_name() + "		|	" + v.get_installationCost());
        }

        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("UKUPNO					|	" + sum);
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("PROSJEK					|	" + (sum / this.warehouseList.size()));
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("+---------------------------------------------------------------------------+");

        System.out.println("Udaljenosti svih skladišta");
        System.out.println("+---------------------------------------------------------------------------+");

        for (Warehouse v
                : this.warehouseList) {
            System.out.println("SKLADIŠTE:		" + v.get_name() + "		CIJENA:	" + v.get_installationCost() + " kn");
            System.out.println("+---------------------------------------------------------------------------+");

            double cost = 0;
            for (RetailsStore store
                    : this.getRetailsStoreList()) {

                double distance = v.getLocationData().distance(store.getLocationData()).doubleValue();
                distance = distance / 1000; //meter -> km
                System.out.println("		" + v.get_locationName() + " -> " + store.get_name() + "	|	" + distance + " km	|	" + (distance * this.getTransportationCostPerKilometer()));
                System.out.println("------------------------------");
                cost = cost + (distance * this.getTransportationCostPerKilometer());
            }
            System.out.println("-----------------------------------------------------------------------------------------------------");
            System.out.println("------------------------------------- UKP. TROŠAK: " + cost + " kn");
            System.out.println("-----------------------------------------------------------------------------------------------------");
            System.out.println("------------------------------");

        }

        System.out.println("------------");
        System.out.println("	|	" + sum);

    }

    public double getTotalMaxiumCost() {
        return totalMaxiumCost;
    }

    public void setTotalMaxiumCost(double totalMaxiumCost) {
        this.totalMaxiumCost = totalMaxiumCost;
    }

    private void loadWarehouseData() {
        loadAllBigCities(BIG_CITIES_FILE);
        loadAllWarehouse();

    }

    private void loadAllWarehouse() {
        for (RetailsStore store
                : this.retailsStoreList) {
            for (City city
                    : this.cityList) {
                if (city.get_name().equalsIgnoreCase(store.get_locationName())) {

                    Warehouse warehouse = new Warehouse();
                    /*
					Warehouse warehouse = new Warehouse(random.nextDouble() * 1000000);
                     */
                    warehouse.set_locationName(store.get_locationName());
                    warehouse.set_name("Skladište " + city.get_name());
                    warehouse.setLocationData(store.getLocationData());
                    warehouse.setInstallationCost(city.getInstallationCost());
                    this.warehouseList.add(warehouse);
                }
            }
        }
    }

    private void loadAllBigCities(String path) {
        //TODO treba validirati
        BufferedReader br = null;
        String line = " ";
        String cvsSplitBy = ";";
        File file = new File(path);
        City city;

        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF8"));

            int lineNo = 0;
            while ((line = br.readLine()) != null) {

                if (line.equalsIgnoreCase("")) {
                    continue;
                }
                // use comma as separator
                String[] data = line.split(cvsSplitBy);

                if (data.length != 3) {
                    System.out.println("Pogrešan zapis spremnika u datoteci!! Zapis: " + line);
                    continue;
                }

                String orderString = data[ORDER_NUMBER_INDEX].trim();
                String bigCityString = data[BIG_CITY_NAME_INDEX].trim();//.substring(0, data[LONGITUDE_DECIMAL_INDEX].length() - 2);
                String costString = data[BIG_CITY_COST_INDEX].trim();//.substring(0, data[LONGITUDE_DECIMAL_INDEX].length() - 2);

                int order = Integer.parseInt(orderString);
                double cost = Double.parseDouble(costString);

                city = new City(order, bigCityString);
                city.setInstallationCost(cost);

                this.cityList.add(city);

            }

        } catch (FileNotFoundException e) {
            System.out.println("Pogrešan zapis spremnika u datoteci!!");

            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Pogrešan zapis spremnika u datoteci!!");

            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void loadRetailsStoreData() {
        loadAllRetailsStoresFromCSV(RETAILS_STORES_FILE);
    }

    private void loadAllRetailsStoresFromCSV(String path) {
//TODO treba validirati
        BufferedReader br = null;
        String line = " ";
        String cvsSplitBy = ";";
        File file = new File(path);
        RetailsStore retailsStore;

        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF8"));

            int lineNo = 0;
            while ((line = br.readLine()) != null) {

                if (line.equalsIgnoreCase("")) {
                    continue;
                }
                // use comma as separator
                String[] data = line.split(cvsSplitBy);

                if (data.length != 5) {
                    System.out.println("Pogrešan zapis spremnika u datoteci!! Zapis: " + line);
                    continue;
                }

                String latitudeString = data[LATITUDE_DECIMAL_INDEX].trim().substring(0, data[LATITUDE_DECIMAL_INDEX].length() - 2);
                String longitudeString = data[LONGITUDE_DECIMAL_INDEX].trim().substring(0, data[LONGITUDE_DECIMAL_INDEX].length() - 2);

                double latitude = Double.parseDouble(latitudeString);
                double longitude = Double.parseDouble(longitudeString);

                Latitude _latitude = Latitude.ofDegrees(latitude);
                Longitude _longitude = Longitude.ofDegrees(longitude);

                WayPoint location = WayPoint.of(_latitude, _longitude);

                retailsStore = new RetailsStore(
                        "Market d.d. " + data[CITY_NAME_INDEX],
                        data[CITY_NAME_INDEX],
                        location);

                this.retailsStoreList.add(retailsStore);

            }

        } catch (FileNotFoundException e) {
            System.out.println("Pogrešan zapis spremnika u datoteci!!");

            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Pogrešan zapis spremnika u datoteci!!");

            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static int getCityNameIndex() {
        return CITY_NAME_INDEX;
    }

    public static int getLatitudeDecimalIndex() {
        return LATITUDE_DECIMAL_INDEX;
    }

    public static int getLongitudeDecimalIndex() {
        return LONGITUDE_DECIMAL_INDEX;
    }

    public static int getOrderNumberIndex() {
        return ORDER_NUMBER_INDEX;
    }

    public static int getBigCityNameIndex() {
        return BIG_CITY_NAME_INDEX;
    }

    public static String getRetailsStoresFile() {
        return RETAILS_STORES_FILE;
    }

    public static String getBigCitiesFile() {
        return BIG_CITIES_FILE;
    }

    public List<Warehouse> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List<Warehouse> warehouseList) {
        this.warehouseList = warehouseList;
    }

    public List<RetailsStore> getRetailsStoreList() {
        return retailsStoreList;
    }

    public void setRetailsStoreList(List<RetailsStore> retailsStoreList) {
        this.retailsStoreList = retailsStoreList;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    public double getTransportationCostPerKilometer() {
        return transportationCostPerKilometer;
    }

    public void setTransportationCostPerKilometer(double transportationCostPerKilometer) {
        this.transportationCostPerKilometer = transportationCostPerKilometer;
    }

}
