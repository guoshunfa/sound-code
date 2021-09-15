package javax.swing.event;

import java.awt.event.InputEvent;
import java.net.URL;
import java.util.EventObject;
import javax.swing.text.Element;

public class HyperlinkEvent extends EventObject {
   private HyperlinkEvent.EventType type;
   private URL u;
   private String desc;
   private Element sourceElement;
   private InputEvent inputEvent;

   public HyperlinkEvent(Object var1, HyperlinkEvent.EventType var2, URL var3) {
      this(var1, var2, var3, (String)null);
   }

   public HyperlinkEvent(Object var1, HyperlinkEvent.EventType var2, URL var3, String var4) {
      this(var1, var2, var3, var4, (Element)null);
   }

   public HyperlinkEvent(Object var1, HyperlinkEvent.EventType var2, URL var3, String var4, Element var5) {
      super(var1);
      this.type = var2;
      this.u = var3;
      this.desc = var4;
      this.sourceElement = var5;
   }

   public HyperlinkEvent(Object var1, HyperlinkEvent.EventType var2, URL var3, String var4, Element var5, InputEvent var6) {
      super(var1);
      this.type = var2;
      this.u = var3;
      this.desc = var4;
      this.sourceElement = var5;
      this.inputEvent = var6;
   }

   public HyperlinkEvent.EventType getEventType() {
      return this.type;
   }

   public String getDescription() {
      return this.desc;
   }

   public URL getURL() {
      return this.u;
   }

   public Element getSourceElement() {
      return this.sourceElement;
   }

   public InputEvent getInputEvent() {
      return this.inputEvent;
   }

   public static final class EventType {
      public static final HyperlinkEvent.EventType ENTERED = new HyperlinkEvent.EventType("ENTERED");
      public static final HyperlinkEvent.EventType EXITED = new HyperlinkEvent.EventType("EXITED");
      public static final HyperlinkEvent.EventType ACTIVATED = new HyperlinkEvent.EventType("ACTIVATED");
      private String typeString;

      private EventType(String var1) {
         this.typeString = var1;
      }

      public String toString() {
         return this.typeString;
      }
   }
}
