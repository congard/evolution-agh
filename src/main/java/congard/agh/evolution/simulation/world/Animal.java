package congard.agh.evolution.simulation.world;

import congard.agh.evolution.simulation.math.Point2d;
import congard.agh.evolution.simulation.WeightedRandom;
import congard.agh.evolution.simulation.world.plant.Plant;

import java.util.LinkedList;
import java.util.stream.IntStream;

public class Animal extends WorldAbstractElement {
    protected Orientation orientation;
    private final Animal[] parents = {null, null};
    private final LinkedList<Animal> children = new LinkedList<>();
    private final Genotype genotype;
    private final long id;
    private final int birthDay;
    private int deathDay = -1;
    private int health;
    private int eatenPlantsCount = 0;

    public Animal(Point2d pos, AbstractWorld world) {
        this(new Genotype(world.getParams()), pos, world);
    }

    private Animal(Genotype genotype, Point2d pos, AbstractWorld world) {
        super(WorldElementType.ANIMAL, pos, world);
        orientation = WeightedRandom.getInstance().next(Orientation.values());
        health = world.getParams().getAnimalsStartEnergy();
        this.genotype = genotype;
        id = world.nextId();
        birthDay = world.getDay();
    }

    // for further use
    /* package */
    void changeHealth(int delta) {
        health += delta;
    }

    public void eat(Plant plant) {
        ++eatenPlantsCount;
        changeHealth(plant.getNutritionalValue());
        plant.remove();
    }

    private void die() {
        deathDay = world.getDay();
    }

    public void live() throws IllegalStateException {
        if (deathDay >= 0)
            throw new IllegalStateException("Method Animal#live cannot be called for a dead animal");

        health -= world.getParams().getAnimalLifeCostPerDay();

        if (!isAlive()) {
            die();
            return;
        }

        orientation = orientation.change(genotype.getActive().getDeltaOrientation());
        world.proposePos(pos.add(orientation.toDeltaVector()), this);
        genotype.next();
    }

    public Animal[] getParents() {
        return parents;
    }

    public int getChildrenCount() {
        return children.size();
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Genotype getGenotype() {
        return genotype;
    }

    public long getId() {
        return id;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public int getDeathDay() {
        return deathDay;
    }

    public int getAge() {
        return isAlive() ? world.getDay() - birthDay : deathDay - birthDay;
    }

    public int getHealth() {
        return health;
    }

    public int getEatenPlantsCount() {
        return eatenPlantsCount;
    }

    public boolean isWellFed() {
        return health >= world.getParams().getWellFedThreshold();
    }

    public boolean isAlive() {
        return health >= 0;
    }

    public static Animal reproduce(Animal p1, Animal p2)  {
        if (!p1.isWellFed() || !p2.isWellFed())
            throw new IllegalStateException("Not well-fed animals cannot reproduce");

        // swap
        if (p2.getHealth() > p1.getHealth()) {
            var tmp = p1;
            p1 = p2;
            p2 = tmp;
        }

        // p1.health > p2.health
        // which means that p1 is stronger than p2

        // construct genotype

        int totalHealth = p1.getHealth() + p2.getHealth();
        float pos = (float) p1.getHealth() / totalHealth;

        var leftGenotype = p1.getGenotype();
        var rightGenotype = p2.getGenotype();

        // l: ABCD
        // r: EFGH
        // pos: 0.75
        // -> ABCH      left side of the stronger parent
        // swap(l, r), pos = 1 - pos
        // l: EFGH
        // r: ABCD
        // pos: 0.25
        // -> EABC      right side of the stronger parent

        var random = WeightedRandom.getInstance();
        var params = p1.world.getParams();

        enum Side {
            LEFT, RIGHT
        }

        if (random.next(Side.values()) == Side.RIGHT) {
            var tmp = leftGenotype;
            leftGenotype = rightGenotype;
            rightGenotype = tmp;
            pos = 1 - pos;
        }

        var genotype = Genotype.mix(leftGenotype, rightGenotype, pos);

        // mutations
        var mutGenesIndexes = random.nextPermutation(
                IntStream.range(0, genotype.size()).boxed().toList(),
                random.nextInt(params.getMinMutationCount(), params.getMaxMutationCount() + 1));

        for (var index : mutGenesIndexes) {
            switch (params.getMutationType()) {
                case RANDOM -> genotype.set(index, random.next(Gene.values()));
                case CORRECTION -> {
                    int newIndex = Math.floorMod(genotype.get(index).ordinal() + random.next(-1, 1), Gene.values().length);
                    genotype.set(index, Gene.fromIndex(newIndex));
                }
            }
        }

        var childEnergyCost = params.getChildEnergyCost();

        p1.changeHealth(-childEnergyCost);
        p2.changeHealth(-childEnergyCost);

        var child = new Animal(genotype, new Point2d(p1.getX(), p1.getY()), p1.world);
        child.health = childEnergyCost * 2;
        child.parents[0] = p1;
        child.parents[1] = p2;

        p1.children.add(child);
        p2.children.add(child);

        return child;
    }
}
