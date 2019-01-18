package hr.foi.ints;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.ISeq;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.function.Function;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;
import java.text.DecimalFormat;
import static java.util.Objects.requireNonNull;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import static jdk.nashorn.internal.objects.NativeMath.round;

public final class WarehouseLocation implements Problem<ISeq<Warehouse>, BitGene, Double> {

    private final Codec<ISeq<Warehouse>, BitGene> _codec;
    private final ContextData condexData;

    public WarehouseLocation(final ISeq<Warehouse> items, ContextData contextData) {
        _codec = Codecs.ofSubSet(items);
        condexData = contextData;
    }

    @Override
    public Function<ISeq<Warehouse>, Double> fitness() {
        return items -> {
            return SharedMethods.calculateMinimumCost(items, condexData);
        };
    }

    @Override
    public Codec<ISeq<Warehouse>, BitGene> codec() {
        return _codec;
    }

    public static WarehouseLocation of(ContextData contextData) {

        return new WarehouseLocation(
                ISeq.of(contextData.getWarehouseList()),
                contextData
        );
    }

    public static void main(final String[] args) {

        ContextData contextData = new ContextData();
        final WarehouseLocation warehouseLocation = WarehouseLocation.of(contextData);

        // Configure and build the evolution engine.
        final Engine<BitGene, Double> engine = Engine.builder(warehouseLocation)
                .populationSize(500)
                .optimize(Optimize.MINIMUM)
                .survivorsSelector(new TournamentSelector<>(5))
                .offspringSelector(new RouletteWheelSelector<>())
                .alterers(
                        new Mutator<>(0.115),
                        new SinglePointCrossover<>(0.16))
                .build();

        // Create evolution statistics consumer.
        final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

        final Phenotype<BitGene, Double> best = engine.stream()
                // Truncate the evolution stream after 7 "steady"
                // generations.
                .limit(bySteadyFitness(7))
                // The evolution will stop after maximal 100
                // generations.
                .limit(100)
                // Update the evaluation statistics after
                // each generation
                .peek(statistics)
                // Collect (reduce) the evolution stream to
                // its best phenotype.
                .collect(toBestPhenotype());
        System.out.println();
        System.out.println();
        System.out.println();

        contextData.saveBestSolution = true;
        System.out.println("#############################################################################");
        System.out.println("#############################################################################");
        System.out.println("			ISPIS NAJBOLJEG RIJEŠENJA                                ");
        System.out.println("#############################################################################");

        System.out.println("#############################################################################");
        final double bestPrice = warehouseLocation.fitness(best.getGenotype()) / 1_000.0;
        System.out.println();
        System.out.println("Best price: " + best);
        System.out.println("#############################################################################");
        System.out.println("                  KRAJ ISPISA NAJBOLJEG RIJEŠENJA                             ");
        System.out.println("#############################################################################");
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("#############################################################################");
        System.out.println("#############################################################################");
        System.out.println("			ISPIS SKLADIŠTA I MALOPRODAJA                            ");
        System.out.println("#############################################################################");

        System.out.println("#############################################################################");
        DecimalFormat df = new DecimalFormat("####0.00");

        contextData.bestSolution.forEach(value -> {
            System.out.println("");
            System.out.println("+-----------------------------------------------------------------------------+");
            System.out.println("|   SKLADIŠTE:  " + value.get_locationName() + "                                        |");
            System.out.println("+-----------------------------------------------------------------------------+");
            for (RetailsStore object : SharedMethods.getAllStoreByWarehouse(value, contextData)) {
                double distance = (value.getLocationData().distance(object.getLocationData()).doubleValue() / 1000);
                //distance = round(2, distance);
                System.out.println("|       " + object.get_name() + "   -->     " + value.get_name() + "    |   " + df.format(distance) + " km     |");
                System.out.println("+-----------------------------------------------------------------------------+");
            }
            System.out.println("");

        });

        System.out.println("#############################################################################");
        System.out.println("                  KRAJ ISPISA ISPIS SKLADIŠTA I MALOPRODAJA                  ");
        System.out.println("#############################################################################");
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println(statistics);
        System.out.println(best);
    }

}
