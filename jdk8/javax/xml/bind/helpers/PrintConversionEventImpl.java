package javax.xml.bind.helpers;

import javax.xml.bind.PrintConversionEvent;
import javax.xml.bind.ValidationEventLocator;

public class PrintConversionEventImpl extends ValidationEventImpl implements PrintConversionEvent {
   public PrintConversionEventImpl(int _severity, String _message, ValidationEventLocator _locator) {
      super(_severity, _message, _locator);
   }

   public PrintConversionEventImpl(int _severity, String _message, ValidationEventLocator _locator, Throwable _linkedException) {
      super(_severity, _message, _locator, _linkedException);
   }
}
