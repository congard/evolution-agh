package congard.agh.evolution.simulation;

import congard.agh.evolution.simulation.params.Params;
import congard.agh.evolution.simulation.world.AbstractWorld;
import congard.agh.evolution.simulation.world.PortalWorld;
import congard.agh.evolution.simulation.world.RoundWorld;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class SimulationEngine {
    private final Params params;
    private final AbstractWorld world;

    private boolean isRunning = false;
    private Thread thread;
    private final Semaphore semaphore = new Semaphore(1);

    private final LinkedList<OnEventListener> listeners = new LinkedList<>();
    private ErrorHandler errorHandler;

    private StatsRecorder statsRecorder;

    public SimulationEngine(Params params) {
        this.params = params;

        world = switch (params.getMapType()) {
            case ROUND -> new RoundWorld(params);
            case PORTAL -> new PortalWorld(params);
        };
    }

    public AbstractWorld getWorld() {
        return world;
    }

    public Params getParams() {
        return params;
    }

    public void addOnEventListener(OnEventListener listener) {
        listeners.add(listener);
    }

    public void removeOnEventListener(OnEventListener listener) {
        listeners.remove(listener);
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setStatsRecorder(StatsRecorder statsRecorder) {
        this.statsRecorder = statsRecorder;
    }

    public StatsRecorder getStatsRecorder() {
        return statsRecorder;
    }

    public void start() {
        isRunning = true;

        thread = new Thread(() -> {
            pushEvent(Event.STARTED);

            while (isRunning) {
                try {
                    Thread.sleep(params.getDayLength());
                } catch (InterruptedException e) {
                    System.out.println("Sleep was interrupted");
                }

                try {
                    semaphore.acquire();
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    handleError(e);
                }

                tick();

                pushEvent(Event.TICK);
            }
        });
        thread.start();
    }

    public void resume() {
        semaphore.release();
        pushEvent(Event.RESUMED);
    }

    public void pause() {
        if (isPaused())
            return;

        try {
            thread.interrupt();
            semaphore.acquire();
            pushEvent(Event.PAUSED);
        } catch (InterruptedException e) {
            e.printStackTrace();
            handleError(e);
        }
    }

    public void stop() {
        isRunning = false;

        if (isPaused())
            semaphore.release();

        pushEvent(Event.STOP);
    }

    public boolean isStarted() {
        return isRunning;
    }

    public boolean isPaused() {
        return semaphore.availablePermits() == 0;
    }

    private void handleError(Exception e) {
        if (errorHandler != null) {
            errorHandler.handle(e);
        }
    }

    private void pushEvent(Event event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }

    private void tick() {
        world.update();

        if (statsRecorder != null) {
            try {
                statsRecorder.writeDay();
            } catch (IOException e) {
                e.printStackTrace();
                handleError(e);
            }
        }
    }
}
