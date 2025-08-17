package org.asmus;

import lombok.extern.slf4j.Slf4j;
import org.asmus.behaviour.ActuationBehaviour;
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


        eventProducer.getWorker().getAxisStream()
                .subscribe(gamepadEventSourceBuilder.leftStickStream()::processArrowEvents);
        eventProducer.getWorker().getAxisStream()
                .subscribe(gamepadEventSourceBuilder.rightStickStream()::processArrowEvents);

        gamepadEventSourceBuilder.getButtonEventStream()
                .log()
                .subscribe();

//        Gesturizer motionMapper = Gesturizer.withDefaults();

//        PolarCoordsMapper polarMapper = Mappers.getMapper(PolarCoordsMapper.class);

//        Disposable control = motionMapper.captor(
//                AxisEventFactory.leftStickStream().polarProducer(eventProducer.getWorker())
//                        .map(polarMapper::map)Millis(2100))
//                .flatMap(Flux::collectList)
//                .filter(List::isEmpty)
//                .subscribe(q -> next = root = defaultNode(
//        );


//        ArrayList<String> knownValues = new ArrayList<>();
//        GestureSupplier rightStickGestSupplier = motionMapper.pathComposeqa(
//                AxisEventFactory.rightStickStream().polarProducer(eventProducer.getWorker())
//                        .map(polarMapper::map)
//        );//                .map(q -> q.getPath())
//
//        rightStickGestSupplier.gestureCb(gesture -> {
////            knownValues.clear();
//            System.out.println("adding Gesture: " + gesture);
//            knownValues.add(gesture);
//        });
//
//        // subscribe to all events
//        gamepadEventSourceBuilder.getButtonEventStream()
////                .log()
//                .filter(q -> q.getType() == EButtonAxisMapping.A && q.getQualified() == EQualificationType.PUSH)
////                .subscribe(q -> {
////                    System.out.println("have it: " + motionMapper.snap());
////                    knownValues.add(motionMapper.snap().getPath());
////                })
//        ;
//
//
//        MatchDef<String> matchDef = MatchDef.<String>builder()
//                .key("totok")
//                .knownValues(knownValues)
//                .build();
//
//        ToleranceConfig toleranceConfig = ToleranceConfig.builder()
//                .frequencyTolerancePercent(10.0)
//                .orderEditDistanceRatio(0.3)
//                .maxConsecutiveDrop(2)
//                .build();
//
//        ToleranceConfigurer<String> forKnownValuesMatcher = Matcher.create(List.of(matchDef));
//        Matcher<String> stringMatcher = forKnownValuesMatcher.withTolerances(toleranceConfig);
//
//        System.out.println("go");
//
//        GestureSupplier gestureSupplier = motionMapper.pathComposeqa(
//                AxisEventFactory.leftStickStream().polarProducer(eventProducer.getWorker())
//                        .map(polarMapper::map)
//        );//                .map(q -> q.getPath())
////                .subscribe(q -> {
////                    System.out.println("path: " + q);
////                })
//
//        gestureSupplier.gestureCb(gesture -> {
//            List<MatchResult<String>> match = stringMatcher.match(gesture);
//            if (!match.isEmpty()) {
//                System.out.print("matching: " + gesture);
//                MatchResult<String> first = match.getFirst();
//                System.out.println(" first match: " + first.getMatchPercentage());
//            }
//        });

//        AxisEventFactory.leftStickStream().polarProducer(eventProducer.getWorker())
//                .subscribe(x -> System.out.println("left " + x));

//        osConnector.getButtonStream()
//                .subscribe(System.out::println);


    }
}
