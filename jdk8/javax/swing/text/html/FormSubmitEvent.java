package javax.swing.text.html;

import java.net.URL;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Element;

public class FormSubmitEvent extends HTMLFrameHyperlinkEvent {
   private FormSubmitEvent.MethodType method;
   private String data;

   FormSubmitEvent(Object var1, HyperlinkEvent.EventType var2, URL var3, Element var4, String var5, FormSubmitEvent.MethodType var6, String var7) {
      super(var1, var2, var3, var4, var5);
      this.method = var6;
      this.data = var7;
   }

   public FormSubmitEvent.MethodType getMethod() {
      return this.method;
   }

   public String getData() {
      return this.data;
   }

   public static enum MethodType {
      GET,
      POST;
   }
}
