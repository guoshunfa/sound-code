package javax.xml.transform.stream;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.Source;

public class StreamSource implements Source {
   public static final String FEATURE = "http://javax.xml.transform.stream.StreamSource/feature";
   private String publicId;
   private String systemId;
   private InputStream inputStream;
   private Reader reader;

   public StreamSource() {
   }

   public StreamSource(InputStream inputStream) {
      this.setInputStream(inputStream);
   }

   public StreamSource(InputStream inputStream, String systemId) {
      this.setInputStream(inputStream);
      this.setSystemId(systemId);
   }

   public StreamSource(Reader reader) {
      this.setReader(reader);
   }

   public StreamSource(Reader reader, String systemId) {
      this.setReader(reader);
      this.setSystemId(systemId);
   }

   public StreamSource(String systemId) {
      this.systemId = systemId;
   }

   public StreamSource(File f) {
      this.setSystemId(f.toURI().toASCIIString());
   }

   public void setInputStream(InputStream inputStream) {
      this.inputStream = inputStream;
   }

   public InputStream getInputStream() {
      return this.inputStream;
   }

   public void setReader(Reader reader) {
      this.reader = reader;
   }

   public Reader getReader() {
      return this.reader;
   }

   public void setPublicId(String publicId) {
      this.publicId = publicId;
   }

   public String getPublicId() {
      return this.publicId;
   }

   public void setSystemId(String systemId) {
      this.systemId = systemId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public void setSystemId(File f) {
      this.systemId = f.toURI().toASCIIString();
   }
}
