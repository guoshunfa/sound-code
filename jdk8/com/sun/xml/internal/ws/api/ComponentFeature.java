package com.sun.xml.internal.ws.api;

import javax.xml.ws.WebServiceFeature;

public class ComponentFeature extends WebServiceFeature implements ServiceSharedFeatureMarker {
   private final Component component;
   private final ComponentFeature.Target target;

   public ComponentFeature(Component component) {
      this(component, ComponentFeature.Target.CONTAINER);
   }

   public ComponentFeature(Component component, ComponentFeature.Target target) {
      this.enabled = true;
      this.component = component;
      this.target = target;
   }

   public String getID() {
      return ComponentFeature.class.getName();
   }

   public Component getComponent() {
      return this.component;
   }

   public ComponentFeature.Target getTarget() {
      return this.target;
   }

   public static enum Target {
      CONTAINER,
      ENDPOINT,
      SERVICE,
      STUB;
   }
}
