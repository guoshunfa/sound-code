package jdk.management.resource;

@FunctionalInterface
public interface ResourceId {
   String getName();

   default ResourceAccuracy getAccuracy() {
      return null;
   }
}
