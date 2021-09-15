package java.awt.im.spi;

import java.awt.AWTException;
import java.awt.Image;
import java.util.Locale;

public interface InputMethodDescriptor {
   Locale[] getAvailableLocales() throws AWTException;

   boolean hasDynamicLocaleList();

   String getInputMethodDisplayName(Locale var1, Locale var2);

   Image getInputMethodIcon(Locale var1);

   InputMethod createInputMethod() throws Exception;
}
