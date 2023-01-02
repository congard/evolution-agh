package congard.agh.evolution.simulation.world;

import congard.agh.evolution.simulation.math.Point2d;
import congard.agh.evolution.simulation.WeightedRandom;
import congard.agh.evolution.simulation.params.Params;
import congard.agh.evolution.simulation.world.plant.ForestedEquatorsGenerator;
import congard.agh.evolution.simulation.world.plant.Plant;
import congard.agh.evolution.simulation.world.plant.PlantGenerator;
import congard.agh.evolution.simulation.world.plant.ToxicCorpsesPlantGenerator;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public abstract class AbstractWorld {
    protected final Params params;
    protected final List<List<WorldCell>> elements;
    protected final List<Animal> deadAnimals;
    protected final Set<Animal> aliveAnimals;
    private long currId = 0;

    private int aliveAnimalsCount = 0;
    private int plantsCount = 0;
    private int freeFieldsCount;
    private int day = 0;
    private float avgEnergy = 0.0f;

    private final PlantGenerator plantGenerator;

    public AbstractWorld(Params params) {
        this.params = params;

        var size = params.getMapSize();

        freeFieldsCount = size.getX() * size.getY();
        elements = new ArrayList<>(size.getY());

        for (int yi = 0; yi < size.getY(); ++yi) {
            List<WorldCell> y = new ArrayList<>();
            elements.add(y);

            for (int xi = 0; xi < size.getX(); ++xi) {
                y.add(new WorldCell(xi, yi));
            }
        }

        deadAnimals = new LinkedList<>();
        aliveAnimals = new HashSet<>();

        plantGenerator = switch (params.getPlantGrowType()) {
            case FORESTED_EQUATORS -> new ForestedEquatorsGenerator(this);
            case TOXIC_CORPSES -> new ToxicCorpsesPlantGenerator(this);
        };

        initAnimals();
        plantGenerator.init();
    }

    private void initAnimals() {
        var mapSize = params.getMapSize();

        WeightedRandom.getInstance().nextPermutation(
                IntStream.range(0, mapSize.getX() * mapSize.getY()).boxed().toList(),
                params.getAnimalsStartCount()
        ).forEach(index -> {
            int x = index % mapSize.getX();
            int y = index / mapSize.getX();

            var animal = new Animal(new Point2d(x, y), this);
            add(animal);
        });
    }

    /* package */
    abstract void proposePos(Point2d pos, Animal animal);

    public synchronized void forEachCell(Consumer<WorldCell> action) {
        elements.forEach(row -> row.forEach(action));
    }

    public synchronized void forEachAlive(Consumer<Animal> action) {
        aliveAnimals.forEach(action);
    }

    public synchronized void forEachDead(Consumer<Animal> action) {
        deadAnimals.forEach(action);
    }

    // steps 1, 2: remove dead & update alive – "life goes on" step
    private void lifeGoesOnStep() {
        var changed = new LinkedList<Animal>();

        elements.forEach(row -> row.forEach(cell -> {
            var iter = cell.get(WorldElementType.ANIMAL).listIterator();

            while (iter.hasNext()) {
                var element = iter.next();

                if (element.getType() != WorldElementType.ANIMAL)
                    continue;

                var animal = (Animal) element;

                int prevX = animal.getX();
                int prevY = animal.getY();

                animal.live();

                if (!animal.isAlive()) {
                    // step 1: remove dead animals
                    iter.remove();
                    deadAnimals.add(animal);
                    aliveAnimals.remove(animal);
                    cell.changeNumberOfDeaths(1);
                } else {
                    // step 2: update animals
                    if (prevX != animal.getX() || prevY != animal.getY()) {
                        // animal has changed its position, and will be added
                        // to another cell, so remove it from this cell
                        iter.remove();
                        changed.add(animal);
                    }
                }
            }
        }));

        // changed animals must be added at the end to
        // prevent multiple alive() per day calling
        changed.forEach(this::add);
    }

    // steps 3, 4: consumption & reproduction – "vitality" step
    private void vitalityStep() {
        elements.forEach(row -> row.forEach(cell -> {
            if (cell.isEmpty())
                return;

            var animals = cell.get(WorldElementType.ANIMAL).stream().map(e -> (Animal) e).toList();
            var plants = cell.get(WorldElementType.PLANT);

            assert plants.size() <= 1;

            // step 3: consumption
            if (!plants.isEmpty() && !animals.isEmpty()) {
                var plant = (Plant) plants.get(0);
                plant.remove();

                // 1st priority: healthiest animals
                var healthiestAnimal = animals.stream().max(Comparator.comparing(Animal::getHealth)).get();
                var candidates = animals.stream()
                        .filter(animal -> animal.getHealth() == healthiestAnimal.getHealth()).toList();

                // 2nd priority: the oldest animals
                var oldestAnimal =  candidates.stream().max(Comparator.comparing(Animal::getAge)).get();
                candidates = candidates.stream()
                        .filter(animal -> animal.getAge() == oldestAnimal.getAge()).toList();

                // 3rd priority: the animals having the most children
                var largestFamilyAnimal =  candidates.stream().max(Comparator.comparing(Animal::getChildrenCount)).get();
                candidates = candidates.stream()
                        .filter(animal -> animal.getChildrenCount() == largestFamilyAnimal.getChildrenCount()).toList();

                if (candidates.size() == 1) {
                    candidates.get(0).eat(plant);
                } else {
                    // select a winner randomly
                    var index = WeightedRandom.getInstance().nextInt(candidates.size());
                    candidates.get(index).eat(plant);
                }
            }

            // step 4: reproduction
            reproduction:
            if (animals.size() > 1) {
                // 1st priority: healthiest animals able to reproduce
                var candidates = animals.stream()
                        .filter(Animal::isWellFed)
                        .sorted((a1, a2) -> Integer.compare(a2.getHealth(), a1.getHealth())).toList();

                if (candidates.size() < 2)
                    break reproduction;

                var healthiestAnimal = candidates.get(0);

                if (candidates.get(0).getHealth() > candidates.get(1).getHealth()) {
                    add(Animal.reproduce(candidates.get(0), candidates.get(1)));
                    break reproduction;
                } // else ==

                // 2nd priority: the oldest animals
                candidates = candidates.stream()
                        .filter(a -> a.getHealth() == healthiestAnimal.getHealth())
                        .sorted((a1, a2) -> Integer.compare(a2.getAge(), a1.getAge()))
                        .toList();
                var oldestAnimal = candidates.get(0);

                if (candidates.get(0).getAge() > candidates.get(1).getAge()) {
                    add(Animal.reproduce(candidates.get(0), candidates.get(1)));
                    break reproduction;
                } // else ==

                // 3rd priority: the animals having the most children
                candidates = candidates.stream()
                        .filter(a -> a.getAge() == oldestAnimal.getAge())
                        .sorted((a1, a2) -> Integer.compare(a2.getChildrenCount(), a1.getChildrenCount()))
                        .toList();
                var largestFamilyAnimal = candidates.get(0);

                if (candidates.get(0).getChildrenCount() > candidates.get(1).getChildrenCount()) {
                    add(Animal.reproduce(candidates.get(0), candidates.get(1)));
                    break reproduction;
                } // else ==

                // otherwise select a winners randomly
                candidates = WeightedRandom.getInstance().nextPermutation(candidates, 2);
                add(Animal.reproduce(candidates.get(0), candidates.get(1)));
            }
        }));
    }

    // step 5
    private void plantStep() {
        // Zgodnie z zasadą Pareto, istnieje 80% szansy, że nowa roślina wyrośnie na preferowanym polu,
        // a tylko 20% szans, że wyrośnie na polu drugiej kategorii. Preferowanych jest około 20% wszystkich
        // miejsc na mapie, 80% miejsc jest uznawane za nieatrakcyjne
        plantGenerator.generate();
    }

    private void collectStatistics() {
        aliveAnimalsCount = 0;
        plantsCount = 0;
        freeFieldsCount = 0;
        avgEnergy = 0.0f;

        elements.forEach(row -> row.forEach(cell -> {
            if (cell.isEmpty()) {
                ++freeFieldsCount;
            } else {
                var animals = cell.get(WorldElementType.ANIMAL);
                var plants = cell.get(WorldElementType.PLANT);
                animals.forEach(animal -> avgEnergy += ((Animal) animal).getHealth());
                aliveAnimalsCount += animals.size();
                plantsCount += plants.size();
            }
        }));

        avgEnergy /= aliveAnimalsCount;
    }

    public synchronized void update() {
        lifeGoesOnStep();
        vitalityStep();
        plantStep();
        collectStatistics();

        ++day;
    }

    public synchronized float calcAvgLifespan() {
        return deadAnimals.parallelStream().reduce(0.0f,
                (acc, animal) -> acc + animal.getAge(), Float::sum) / deadAnimals.size();
    }

    public WorldCell getCell(int x, int y) {
        return elements.get(y).get(x);
    }

    public void add(WorldAbstractElement element) {
        if (element.getType() == WorldElementType.ANIMAL)
            aliveAnimals.add((Animal) element);
        getCell(element.getX(), element.getY()).add(element);
    }

    public void remove(WorldAbstractElement element) {
        getCell(element.getX(), element.getY()).remove(element);
    }

    public Params getParams() {
        return params;
    }

    public List<List<WorldCell>> getElements() {
        return elements;
    }

    public synchronized int getAliveAnimalsCount() {
        return aliveAnimalsCount;
    }

    public synchronized int getPlantsCount() {
        return plantsCount;
    }

    public synchronized int getFreeFieldsCount() {
        return freeFieldsCount;
    }

    public synchronized int getDay() {
        return day;
    }

    public synchronized float getAvgEnergy() {
        return avgEnergy;
    }

    public long nextId() {
        return currId++;
    }
}
