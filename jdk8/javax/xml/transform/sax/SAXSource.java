package javax.xml.transform.sax;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SAXSource implements Source {
   public static final String FEATURE = "http://javax.xml.transform.sax.SAXSource/feature";
   private XMLReader reader;
   private InputSource inputSource;

   public SAXSource() {
   }

   public SAXSource(XMLReader reader, InputSource inputSource) {
      this.reader = reader;
      this.inputSource = inputSource;
   }

   public SAXSource(InputSource inputSource) {
      this.inputSource = inputSource;
   }

   public void setXMLReader(XMLReader reader) {
      this.reader = reader;
   }

   public XMLReader getXMLReader() {
      return this.reader;
   }

   public void setInputSource(InputSource inputSource) {
      this.inputSource = inputSource;
   }

   public InputSource getInputSource() {
      return this.inputSource;
   }

   public void setSystemId(String systemId) {
      if (null == this.inputSource) {
         this.inputSource = new InputSource(systemId);
      } else {
         this.inputSource.setSystemId(systemId);
      }

   }

   public String getSystemId() {
      return this.inputSource == null ? null : this.inputSource.getSystemId();
   }

   public static InputSource sourceToInputSource(Source source) {
      if (source instanceof SAXSource) {
         return ((SAXSource)source).getInputSource();
      } else if (source instanceof StreamSource) {
         StreamSource ss = (StreamSource)source;
         InputSource isource = new InputSource(ss.getSystemId());
         isource.setByteStream(ss.getInputStream());
         isource.setCharacterStream(ss.getReader());
         isource.setPublicId(ss.getPublicId());
         return isource;
      } else {
         return null;
      }
   }
}
