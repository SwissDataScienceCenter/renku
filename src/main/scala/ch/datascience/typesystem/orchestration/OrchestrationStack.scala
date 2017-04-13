package ch.datascience.typesystem.orchestration

/**
  * Created by johann on 13/04/17.
  */
abstract class OrchestrationStack extends ExecutionComponent with DatabaseComponent with GraphComponent with GraphDomainOrchestrator with PropertyKeyOrchestrator
