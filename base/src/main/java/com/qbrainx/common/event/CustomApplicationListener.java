package com.qbrainx.common.event;

import org.springframework.context.ApplicationListener;

public interface CustomApplicationListener<E extends CustomApplicationEvent> extends ApplicationListener {

    void onApplicationEvent(E event);

}
