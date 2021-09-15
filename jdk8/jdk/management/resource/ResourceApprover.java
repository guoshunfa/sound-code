package jdk.management.resource;

@FunctionalInterface
public interface ResourceApprover {
   long request(ResourceMeter var1, long var2, long var4, ResourceId var6);
}
