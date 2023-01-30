package com.qbrainx.common.identity;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class EventListenerIntegrator implements Integrator {

  @Override
  public void integrate(final Metadata metadata,
                        final SessionFactoryImplementor sessionFactory,
                        final SessionFactoryServiceRegistry serviceRegistry) {
    final EventListenerRegistry service = serviceRegistry.getService(EventListenerRegistry.class);
    final CustomSequencePersistEventListener dSPersistEventListener = new CustomSequencePersistEventListener(metadata, sessionFactory, serviceRegistry);
    service.prependListeners(EventType.PERSIST, dSPersistEventListener);
    service.prependListeners(EventType.PRE_INSERT, dSPersistEventListener.getCustomSequenceEventListener());
  }

  @Override
  public void disintegrate(final SessionFactoryImplementor sessionFactory,
                           final SessionFactoryServiceRegistry serviceRegistry) {

  }
}
