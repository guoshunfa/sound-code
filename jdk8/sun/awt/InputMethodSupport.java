package sun.awt;

import java.awt.AWTException;
import java.awt.Window;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;
import sun.awt.im.InputContext;

public interface InputMethodSupport {
   InputMethodDescriptor getInputMethodAdapterDescriptor() throws AWTException;

   Window createInputMethodWindow(String var1, InputContext var2);

   boolean enableInputMethodsForTextComponent();

   Locale getDefaultKeyboardLocale();
}
