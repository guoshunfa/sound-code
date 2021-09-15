package com.sun.jmx.snmp.tasks;

public interface Task extends Runnable {
   void cancel();
}
