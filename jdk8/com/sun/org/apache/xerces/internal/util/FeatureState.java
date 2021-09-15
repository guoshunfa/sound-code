package com.sun.org.apache.xerces.internal.util;

public class FeatureState {
   public final Status status;
   public final boolean state;
   public static final FeatureState SET_ENABLED;
   public static final FeatureState SET_DISABLED;
   public static final FeatureState UNKNOWN;
   public static final FeatureState RECOGNIZED;
   public static final FeatureState NOT_SUPPORTED;
   public static final FeatureState NOT_RECOGNIZED;
   public static final FeatureState NOT_ALLOWED;

   public FeatureState(Status status, boolean state) {
      this.status = status;
      this.state = state;
   }

   public static FeatureState of(Status status) {
      return new FeatureState(status, false);
   }

   public static FeatureState is(boolean value) {
      return new FeatureState(Status.SET, value);
   }

   public boolean isExceptional() {
      return this.status.isExceptional();
   }

   static {
      SET_ENABLED = new FeatureState(Status.SET, true);
      SET_DISABLED = new FeatureState(Status.SET, false);
      UNKNOWN = new FeatureState(Status.UNKNOWN, false);
      RECOGNIZED = new FeatureState(Status.RECOGNIZED, false);
      NOT_SUPPORTED = new FeatureState(Status.NOT_SUPPORTED, false);
      NOT_RECOGNIZED = new FeatureState(Status.NOT_RECOGNIZED, false);
      NOT_ALLOWED = new FeatureState(Status.NOT_ALLOWED, false);
   }
}
