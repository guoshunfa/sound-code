package java.awt;

interface EventFilter {
   EventFilter.FilterAction acceptEvent(AWTEvent var1);

   public static enum FilterAction {
      ACCEPT,
      REJECT,
      ACCEPT_IMMEDIATELY;
   }
}
