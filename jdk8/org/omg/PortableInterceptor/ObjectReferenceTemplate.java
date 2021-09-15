package org.omg.PortableInterceptor;

public interface ObjectReferenceTemplate extends ObjectReferenceFactory {
   String server_id();

   String orb_id();

   String[] adapter_name();
}
