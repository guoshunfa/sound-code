package javax.xml.bind.helpers;

import javax.xml.bind.ParseConversionEvent;
import javax.xml.bind.ValidationEventLocator;

public class ParseConversionEventImpl extends ValidationEventImpl implements ParseConversionEvent {
   public ParseConversionEventImpl(int _severity, String _message, ValidationEventLocator _locator) {
      super(_severity, _message, _locator);
   }

   public ParseConversionEventImpl(int _severity, String _message, ValidationEventLocator _locator, Throwable _linkedException) {
      super(_severity, _message, _locator, _linkedException);
   }
}
