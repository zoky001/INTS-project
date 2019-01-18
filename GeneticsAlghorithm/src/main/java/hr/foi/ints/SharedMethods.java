package hr.foi.ints;

import io.jenetics.util.ISeq;
import java.util.ArrayList;
import java.util.List;

public class SharedMethods {

    public static Double calculateMinimumCost(ISeq<Warehouse> items, ContextData contextData) {

        if (items.size() == 0) {

            return contextData.getTotalMaxiumCost();

        }
        double totalInstallationCost = SharedMethods.calculateTotalCostOfWarehouseInstallation(items);
        double totalTransportationCost = SharedMethods.calculateTotalTransportationCost(items, contextData);

        double totalCost = totalInstallationCost + totalTransportationCost;

        System.out.println("");
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("|    Fitness funkcija ispis       VELIČINA: " + items.size() + "                               |");
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("|   SKLADIŠTE             |   LOKACIJA    |   CIJENA GRADNJE                |");
        System.out.println("+---------------------------------------------------------------------------+");

        items.forEach(item -> {

            System.out.println("|   " + item.get_name() + "     |   " + item.get_locationName() + "    |   " + item.get_installationCost() + " kn |");

        });
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("|   Cijena gradnje ukupno:          |   " + totalInstallationCost + " kn    |");
        System.out.println("|   Cijena transporta ukupno:	|       " + totalTransportationCost + " kn  |");
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("|   Ukupan trošak:			|   " + totalCost + " kn            |");
        System.out.println("+---------------------------------------------------------------------------+");
        System.out.println("");

        if (contextData.saveBestSolution) {
            contextData.bestSolution = items;
        }

        return items.size() == 0 ? contextData.getTotalMaxiumCost() : totalCost;
    }

    private static Double calculateTotalTransportationCost(ISeq<Warehouse> items, ContextData contextData) {
        // the length in meter.
        double totalDistance = 0;
        Warehouse warehouse;

        for (RetailsStore store : contextData.getRetailsStoreList()) {
            warehouse = SharedMethods.getNearestWarehouse(items, store);
            if (warehouse == null) {
                System.out.println("Sranje");
            }
            totalDistance = totalDistance + warehouse.getLocationData().distance(store.getLocationData()).doubleValue();
        }

        double totalCost = (totalDistance / 1000) * contextData.getTransportationCostPerKilometer();

        return totalCost;
    }

    public static List<RetailsStore> getAllStoreByWarehouse(Warehouse item, ContextData contextData) {
        List<RetailsStore> retailsStores = new ArrayList<RetailsStore>();
        Warehouse warehouse;
        for (RetailsStore store : contextData.getRetailsStoreList()) {
            warehouse = SharedMethods.getNearestWarehouse(contextData.bestSolution, store);
            if (warehouse == null) {
                System.out.println("Sranje");
            }else if(warehouse.equals(item)){
                retailsStores.add(store);
            }
        }


        return retailsStores;
    }

    public static Warehouse getNearestWarehouse(ISeq<Warehouse> items, RetailsStore store) {
        final double[] minDistance = {Double.MAX_VALUE};
        final Warehouse[] nearWarehouse = new Warehouse[1];
        nearWarehouse[0] = items.get(0);
        items.forEach(item -> {
            double distance = item.getLocationData().distance(store.getLocationData()).doubleValue();
            if (distance < minDistance[0]) {
                minDistance[0] = distance;
                nearWarehouse[0] = item;
            }
        });

        if (nearWarehouse[0] == null) {
            System.out.println("Sranje");
        }
        return nearWarehouse[0];
    }

    private static Double calculateTotalCostOfWarehouseInstallation(ISeq<Warehouse> items) {
        final double[] sum = {0};

        items.forEach(item -> {
            sum[0] = sum[0] + item.get_installationCost();
        });

        return sum[0];

    }

}
