package com.qbrainx.common.event;

import org.springframework.context.ApplicationEvent;

public class CustomApplicationEvent extends ApplicationEvent {

    private String eventName;

    private String eventType;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     * @param eventName eventName
     */
    public CustomApplicationEvent(Object source,
                                  final String eventName) {
        this(source, eventName, "Default Event Type");
    }

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     * @param eventName eventName
     * @param eventType eventType
     */
    public CustomApplicationEvent(Object source,
                                  final String eventName,
                                  final String eventType) {
        super(source);
        this.eventName = eventName;
        this.eventType = eventType;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventType() {
        return eventType;
    }

}
