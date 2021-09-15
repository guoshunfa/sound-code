package com.sun.org.apache.xerces.internal.util;

public class PropertyState {
   public final Status status;
   public final Object state;
   public static final PropertyState UNKNOWN;
   public static final PropertyState RECOGNIZED;
   public static final PropertyState NOT_SUPPORTED;
   public static final PropertyState NOT_RECOGNIZED;
   public static final PropertyState NOT_ALLOWED;

   public PropertyState(Status status, Object state) {
      this.status = status;
      this.state = state;
   }

   public static PropertyState of(Status status) {
      return new PropertyState(status, (Object)null);
   }

   public static PropertyState is(Object value) {
      return new PropertyState(Status.SET, value);
   }

   public boolean isExceptional() {
      return this.status.isExceptional();
   }

   static {
      UNKNOWN = new PropertyState(Status.UNKNOWN, (Object)null);
      RECOGNIZED = new PropertyState(Status.RECOGNIZED, (Object)null);
      NOT_SUPPORTED = new PropertyState(Status.NOT_SUPPORTED, (Object)null);
      NOT_RECOGNIZED = new PropertyState(Status.NOT_RECOGNIZED, (Object)null);
      NOT_ALLOWED = new PropertyState(Status.NOT_ALLOWED, (Object)null);
   }
}
