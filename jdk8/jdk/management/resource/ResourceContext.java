package jdk.management.resource;

import java.util.stream.Stream;
import jdk.management.resource.internal.SimpleResourceContext;

public interface ResourceContext extends AutoCloseable {
   void close();

   String getName();

   default ResourceContext bindThreadContext() {
      throw new UnsupportedOperationException("bind not supported by " + this.getName());
   }

   static ResourceContext unbindThreadContext() {
      return SimpleResourceContext.unbindThreadContext();
   }

   default Stream<Thread> boundThreads() {
      throw new UnsupportedOperationException("boundThreads not supported by " + this.getName());
   }

   ResourceRequest getResourceRequest(ResourceType var1);

   default void addResourceMeter(ResourceMeter var1) {
      throw new UnsupportedOperationException("addResourceMeter not supported by " + this.getName());
   }

   default boolean removeResourceMeter(ResourceMeter var1) {
      throw new UnsupportedOperationException("removeResourceMeter not supported by " + this.getName());
   }

   ResourceMeter getMeter(ResourceType var1);

   Stream<ResourceMeter> meters();

   default void requestAccurateUpdate(ResourceAccuracy var1) {
      throw new UnsupportedOperationException("requestAccurateUpdate not supported by " + this.getName());
   }
}
