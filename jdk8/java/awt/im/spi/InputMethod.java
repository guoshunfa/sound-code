package java.awt.im.spi;

import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.util.Locale;

public interface InputMethod {
   void setInputMethodContext(InputMethodContext var1);

   boolean setLocale(Locale var1);

   Locale getLocale();

   void setCharacterSubsets(Character.Subset[] var1);

   void setCompositionEnabled(boolean var1);

   boolean isCompositionEnabled();

   void reconvert();

   void dispatchEvent(AWTEvent var1);

   void notifyClientWindowChange(Rectangle var1);

   void activate();

   void deactivate(boolean var1);

   void hideWindows();

   void removeNotify();

   void endComposition();

   void dispose();

   Object getControlObject();
}
