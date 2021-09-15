package jdk.management.resource;

public interface ResourceMeter {
   long getValue();

   long getAllocated();

   ResourceType getType();
}
