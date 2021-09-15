package com.sun.corba.se.spi.monitoring;

public interface MonitoringConstants {
   String DEFAULT_MONITORING_ROOT = "orb";
   String DEFAULT_MONITORING_ROOT_DESCRIPTION = "ORB Management and Monitoring Root";
   String CONNECTION_MONITORING_ROOT = "Connections";
   String CONNECTION_MONITORING_ROOT_DESCRIPTION = "Statistics on inbound/outbound connections";
   String INBOUND_CONNECTION_MONITORING_ROOT = "Inbound";
   String INBOUND_CONNECTION_MONITORING_ROOT_DESCRIPTION = "Statistics on inbound connections";
   String OUTBOUND_CONNECTION_MONITORING_ROOT = "Outbound";
   String OUTBOUND_CONNECTION_MONITORING_ROOT_DESCRIPTION = "Statistics on outbound connections";
   String CONNECTION_MONITORING_DESCRIPTION = "Connection statistics";
   String CONNECTION_TOTAL_NUMBER_OF_CONNECTIONS = "NumberOfConnections";
   String CONNECTION_TOTAL_NUMBER_OF_CONNECTIONS_DESCRIPTION = "The total number of connections";
   String CONNECTION_NUMBER_OF_IDLE_CONNECTIONS = "NumberOfIdleConnections";
   String CONNECTION_NUMBER_OF_IDLE_CONNECTIONS_DESCRIPTION = "The number of idle connections";
   String CONNECTION_NUMBER_OF_BUSY_CONNECTIONS = "NumberOfBusyConnections";
   String CONNECTION_NUMBER_OF_BUSY_CONNECTIONS_DESCRIPTION = "The number of busy connections";
   String THREADPOOL_MONITORING_ROOT = "threadpool";
   String THREADPOOL_MONITORING_ROOT_DESCRIPTION = "Monitoring for all ThreadPool instances";
   String THREADPOOL_MONITORING_DESCRIPTION = "Monitoring for a ThreadPool";
   String THREADPOOL_CURRENT_NUMBER_OF_THREADS = "currentNumberOfThreads";
   String THREADPOOL_CURRENT_NUMBER_OF_THREADS_DESCRIPTION = "Current number of total threads in the ThreadPool";
   String THREADPOOL_NUMBER_OF_AVAILABLE_THREADS = "numberOfAvailableThreads";
   String THREADPOOL_NUMBER_OF_AVAILABLE_THREADS_DESCRIPTION = "Number of available threads in the ThreadPool";
   String THREADPOOL_NUMBER_OF_BUSY_THREADS = "numberOfBusyThreads";
   String THREADPOOL_NUMBER_OF_BUSY_THREADS_DESCRIPTION = "Number of busy threads in the ThreadPool";
   String THREADPOOL_AVERAGE_WORK_COMPLETION_TIME = "averageWorkCompletionTime";
   String THREADPOOL_AVERAGE_WORK_COMPLETION_TIME_DESCRIPTION = "Average elapsed time taken to complete a work item by the ThreadPool";
   String THREADPOOL_CURRENT_PROCESSED_COUNT = "currentProcessedCount";
   String THREADPOOL_CURRENT_PROCESSED_COUNT_DESCRIPTION = "Number of Work items processed by the ThreadPool";
   String WORKQUEUE_MONITORING_DESCRIPTION = "Monitoring for a Work Queue";
   String WORKQUEUE_TOTAL_WORK_ITEMS_ADDED = "totalWorkItemsAdded";
   String WORKQUEUE_TOTAL_WORK_ITEMS_ADDED_DESCRIPTION = "Total number of Work items added to the Queue";
   String WORKQUEUE_WORK_ITEMS_IN_QUEUE = "workItemsInQueue";
   String WORKQUEUE_WORK_ITEMS_IN_QUEUE_DESCRIPTION = "Number of Work items in the Queue to be processed";
   String WORKQUEUE_AVERAGE_TIME_IN_QUEUE = "averageTimeInQueue";
   String WORKQUEUE_AVERAGE_TIME_IN_QUEUE_DESCRIPTION = "Average time a work item waits in the work queue";
}
