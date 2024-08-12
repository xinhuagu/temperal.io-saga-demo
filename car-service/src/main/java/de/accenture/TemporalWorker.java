package de.accenture;

import de.berlin.accenture.BookingWorkflow;
import de.berlin.accenture.BookingWorkflowImpl;
import de.berlin.accenture.activity.CarBookingActivity;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.WorkerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class TemporalWorker {

  WorkerFactory factory;

  @Inject
  WorkflowClient workflowClient;

  @Inject
  CarBookingActivity carActivity;

  void onStart(@Observes StartupEvent ev) {

    factory = WorkerFactory.newInstance(workflowClient);
    var worker = factory.newWorker(BookingWorkflow.CAR_SERVICE_TASK_QUEUE);

    worker.registerWorkflowImplementationTypes(BookingWorkflowImpl.class);

    worker.registerActivitiesImplementations(carActivity);
    factory.start();
    log.info("Temporal worker started.");
  }

  void onStop(@Observes ShutdownEvent ev) {
    if (factory != null) {
      factory.shutdown();
    }
    log.info("Temporal worker stopped.");
  }
}
