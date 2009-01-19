/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package doc;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.log4j.Logger;

public class Validate {
   private static Logger logger = Logger.getLogger(Validate.class.getName());
   public static void main(String[] args) {
      try {
      	 File xmlFile = new File(args[0]);
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         builderFactory.setValidating(true); // Default is false
         DocumentBuilder builder = builderFactory.newDocumentBuilder();
         ErrorHandler errHandler = new ValidateErrorHandler();
         builder.setErrorHandler(errHandler);
         Document doc = builder.parse(xmlFile);
      } catch (ParserConfigurationException parseError) {
          Validate.logger.error(parseError.toString());
      } catch (SAXException saxExcep) {
         Validate.logger.error(saxExcep.toString());
      } catch (IOException ioExcep) {
         Validate.logger.error(ioExcep.toString());
      }
   }
   private static class ValidateErrorHandler implements ErrorHandler {
      public void warning(SAXParseException saxExcep) throws SAXException {
         Validate.logger.warn(getInfo(saxExcep));
      }
      public void error(SAXParseException saxExcep) throws SAXException {
	 Validate.logger.error(getInfo(saxExcep));
      }
      public void fatalError(SAXParseException saxExcep) throws SAXException {
	 Validate.logger.fatal(getInfo(saxExcep));
      }
      private String getInfo(SAXParseException e) {
	 String error; 
	 error ="\n   Public ID: "+e.getPublicId() +
		"\n   System ID: "+e.getSystemId() +
      	 	"\n   Line number: "+e.getLineNumber() +
      	 	"\n   Column number: "+e.getColumnNumber() + 
      	 	"\n   Message: "+e.getMessage();
        return error;
      }
   }
}

