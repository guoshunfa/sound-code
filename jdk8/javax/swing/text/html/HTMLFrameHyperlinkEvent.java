package javax.swing.text.html;

import java.awt.event.InputEvent;
import java.net.URL;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Element;

public class HTMLFrameHyperlinkEvent extends HyperlinkEvent {
   private String targetFrame;

   public HTMLFrameHyperlinkEvent(Object var1, HyperlinkEvent.EventType var2, URL var3, String var4) {
      super(var1, var2, var3);
      this.targetFrame = var4;
   }

   public HTMLFrameHyperlinkEvent(Object var1, HyperlinkEvent.EventType var2, URL var3, String var4, String var5) {
      super(var1, var2, var3, var4);
      this.targetFrame = var5;
   }

   public HTMLFrameHyperlinkEvent(Object var1, HyperlinkEvent.EventType var2, URL var3, Element var4, String var5) {
      super(var1, var2, var3, (String)null, var4);
      this.targetFrame = var5;
   }

   public HTMLFrameHyperlinkEvent(Object var1, HyperlinkEvent.EventType var2, URL var3, String var4, Element var5, String var6) {
      super(var1, var2, var3, var4, var5);
      this.targetFrame = var6;
   }

   public HTMLFrameHyperlinkEvent(Object var1, HyperlinkEvent.EventType var2, URL var3, String var4, Element var5, InputEvent var6, String var7) {
      super(var1, var2, var3, var4, var5, var6);
      this.targetFrame = var7;
   }

   public String getTarget() {
      return this.targetFrame;
   }
}
