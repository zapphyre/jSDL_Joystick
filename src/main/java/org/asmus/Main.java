package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.behaviour.ActuationBehaviour;
import org.asmus.builder.AxisEventFactory;
import org.asmus.builder.AxisEventProcessorFactory;
import org.asmus.builder.EventProducer;
import org.asmus.builder.IntrospectedEventFactory;
import org.asmus.builder.closure.button.OsDevice;
import org.asmus.builder.closure.button.RawArrowSource;
import org.asmus.introspect.impl.ReleaseIntrospector;
import org.asmus.model.Node;
import org.asmus.qualifier.impl.MultiplicityQualifier;


@Slf4j
public class Main {

    static Node root, next;

    public static void main(String[] args) throws InterruptedException {
        EventProducer eventProducer = new EventProducer();

        eventProducer.watchForDevices(0, 1);
        ActuationBehaviour behaviour = ActuationBehaviour.builder()
                .introspector(new ReleaseIntrospector())
                .qualifier(new MultiplicityQualifier())
                .build();

        IntrospectedEventFactory gamepadEventSourceBuilder = new IntrospectedEventFactory();

        OsDevice buttonProcessor = gamepadEventSourceBuilder.getButtonStream();
        RawArrowSource arrowsStream = gamepadEventSourceBuilder.getArrowsStream();
        RawArrowSource triggerStream = gamepadEventSourceBuilder.rightTriggerDigitizedProcessor();
        RawArrowSource triggerLeft = gamepadEventSourceBuilder.leftTriggerDigitizedProcessor();
        RawArrowSource triggerRangeDigi = gamepadEventSourceBuilder.leftDigitizedRangeTriggerStream();
        RawArrowSource rightTriggerContinuousProcessor = gamepadEventSourceBuilder.rightTriggerContinuousProcessor();

        eventProducer.getWorker().getButtonStream()
                .subscribe(buttonProcessor::processButtonEvents);

        eventProducer.getWorker().getAxisStream()
                .subscribe(arrowsStream::processArrowEvents);

        eventProducer.getWorker().getAxisStream()
                .subscribe(triggerStream::processArrowEvents);
//
//        eventProducer.getWorker().getAxisStream()
//                        .subscribe(triggerRangeDigi::processArrowEvents);

        eventProducer.getWorker().getAxisStream()
                .subscribe(triggerLeft::processArrowEvents);
//
//        eventProducer.getWorker().getAxisStream()
//                .subscribe(triggerLeft::processArrowEvents);

//        eventProducer.getWorker().getAxisStream()
//                .subscribe(rightTriggerContinuousProcessor::processArrowEvents);

        AxisEventProcessorFactory polarFactory = new AxisEventProcessorFactory();
        eventProducer.getWorker().getAxisStream()
                        .subscribe(q -> polarFactory.leftStickStream().processArrowEvents(q));
        eventProducer.getWorker().getAxisStream()
                        .subscribe(q -> polarFactory.reightStickStream().processArrowEvents(q));


        polarFactory.rightPolarFlux()
                .log("Left stick stream")
                .subscribe();
        polarFactory.leftPolarFlux()
                .log("Right stick stream")
                .subscribe();


//        eventProducer.getWorker().getAxisStream()
//                .subscribe(gamepadEventSourceBuilder.leftStickStream()::processArrowEvents);
//        eventProducer.getWorker().getAxisStream()
//                .subscribe(gamepadEventSourceBuilder.rightStickStream()::processArrowEvents);

        gamepadEventSourceBuilder.getButtonEventStream()
                .distinctUntilChanged()
                .log()
                .subscribe();

    }
}
