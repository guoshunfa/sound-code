package javax.sql.rowset;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface WebRowSet extends CachedRowSet {
   String PUBLIC_XML_SCHEMA = "--//Oracle Corporation//XSD Schema//EN";
   String SCHEMA_SYSTEM_ID = "http://java.sun.com/xml/ns/jdbc/webrowset.xsd";

   void readXml(Reader var1) throws SQLException;

   void readXml(InputStream var1) throws SQLException, IOException;

   void writeXml(ResultSet var1, Writer var2) throws SQLException;

   void writeXml(ResultSet var1, OutputStream var2) throws SQLException, IOException;

   void writeXml(Writer var1) throws SQLException;

   void writeXml(OutputStream var1) throws SQLException, IOException;
}
