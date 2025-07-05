package org.asmus.digitizer;

import lombok.RequiredArgsConstructor;
import org.asmus.model.ELogicalEventType;
import org.asmus.model.EQualificationType;
import org.asmus.model.GamepadEvent;
import org.asmus.model.TriggerPosition;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

import static org.asmus.model.NamingConstants.MAX;
import static org.asmus.model.NamingConstants.MIN;

@RequiredArgsConstructor
public class EdgeTriggerDigitizer {

    private final long QUICK_MS = 210;

    private ELogicalEventType last;

    private final Sinks.Many<GamepadEvent> qualifiedEventStream;

    private TriggerState current = new Released();

    public Consumer<GamepadEvent> digitize() {
        return q -> {
            current = current.getNext(q.getPosition(), current);

            qualifiedEventStream.tryEmitNext(q.withLogicalEventType(last = current.getLogicalType()));
        };
    }

    boolean isQuickClick(TriggerState state) {
        return System.currentTimeMillis() - state.getTriggerTime() < QUICK_MS;
    }

    abstract class TriggerState {
        long triggerTime = System.currentTimeMillis();

        abstract TriggerState getNext(int pos, TriggerState prev);

        abstract ELogicalEventType getLogicalType();

        long getTriggerTime() {
            return triggerTime;
        }
    }

    class Released extends TriggerState {
        @Override
        TriggerState getNext(int pos, TriggerState prev) {
            if (pos == MAX && prev instanceof Released) return new Engaged();
            if (pos == MIN) return new StepDown();

            return this;
        }

        @Override
        ELogicalEventType getLogicalType() {
            return ELogicalEventType.RELEASE;
        }
    }

    class Engaged extends TriggerState {
        @Override
        TriggerState getNext(int pos, TriggerState prev) {
            if (pos == MAX) return new StepUp();
            if (pos == MIN) return new Released();

            return this;
        }

        @Override
        ELogicalEventType getLogicalType() {
            return ELogicalEventType.ENGAGE;
        }
    }

    class StepUp extends TriggerState {
        @Override
        TriggerState getNext(int pos, TriggerState prev) {
            if (pos == MAX) return new StepUp();
            if (pos == MIN) return new Released();

            return this;
        }

        @Override
        ELogicalEventType getLogicalType() {
            return ELogicalEventType.EDGING_POSITIVE;
        }
    }

    class StepDown extends TriggerState {
        @Override
        TriggerState getNext(int pos, TriggerState prev) {
            if (pos == MAX) return new Engaged();
            if (pos == MIN) return new StepDown();

            return this;
        }

        @Override
        ELogicalEventType getLogicalType() {
            return ELogicalEventType.EDGING_NEGATIVE;
        }
    }

    class Dull extends TriggerState {

        @Override
        TriggerState getNext(int pos, TriggerState prev) {
            if (pos == MAX) return new Engaged();
            if (pos == MIN) return new StepDown();

            return this;
        }

        @Override
        ELogicalEventType getLogicalType() {
            return ELogicalEventType.DULL;
        }
    }
}
