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
package doc.snippets;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;



/**
 * This class is responsible with creating the
 * appropriate snippet parser depending on the file.
 *
 * How the tags look like is also defined here.
 * @author The ProActive Team
 *
 */
public final class SnippetExtractorFactory {
    private static Logger logger = Logger.getLogger(SnippetExtractorFactory.class.getName());

    static String startAnnotationFractal = "@snippet-start";
    static String endAnnotationFractal = "@snippet-end";

    static String breakAnnotationFractal = "@snippet-break";
    static String resumeAnnotationFractal = "@snippet-resume";

    static String startAnnotationJava = "@snippet-start";
    static String endAnnotationJava = "@snippet-end";

    static String breakAnnotationJava = "@snippet-break";
    static String resumeAnnotationJava = "@snippet-resume";

    static String startAnnotationXML = "@snippet-start";
    static String endAnnotationXML = "@snippet-end";

    static String breakAnnotationXML = "@snippet-break";
    static String resumeAnnotationXML = "@snippet-resume";

    /**
     * This class will not be instantiated
     */
    private SnippetExtractorFactory() {
    };

    /**
     * Returns an appropriate parser chosen according to the file type.
     *
     * @param file file to be parsed
     * @param targetDir where the snippets will be placed
     * @return a parser for the file
     * @throws IOException
     */
    public static SnippetExtractor getExtractor(final File file, final File targetDir) throws IOException {
        //TODO configure externally
        SnippetExtractorFactory.logger.setLevel(Level.INFO);
        //return a JavaSnippetExtractor for parsing java files
        if (file.toString().endsWith(".java")) {
            SnippetExtractorFactory.logger.debug("Java snippet parser started for file: " + file +
                " and target directory " + targetDir + ". The annotations used are: " +
                SnippetExtractorFactory.startAnnotationJava + ", " +
                SnippetExtractorFactory.endAnnotationJava + ", " +
                SnippetExtractorFactory.breakAnnotationJava + " and " +
                SnippetExtractorFactory.resumeAnnotationJava);
            return new JavaSnippetExtractor(file, targetDir,
			SnippetExtractorFactory.startAnnotationJava,
			SnippetExtractorFactory.endAnnotationJava,
			SnippetExtractorFactory.breakAnnotationJava,
			SnippetExtractorFactory.resumeAnnotationJava);
        }
        //return a XMLSnippetExtractor for parsing XML files
        if (file.toString().endsWith(".xml")) {

            SnippetExtractorFactory.logger.debug("XML snippet parser started for file: " + file +
                " and target directory " + targetDir + ". The annotations used are: " +
                SnippetExtractorFactory.startAnnotationXML + ", " +
                SnippetExtractorFactory.endAnnotationXML+ ", " +
                SnippetExtractorFactory.breakAnnotationXML + " and " +
                SnippetExtractorFactory.resumeAnnotationXML);
            return new XMLSnippetExtractor(file, targetDir,
			SnippetExtractorFactory.startAnnotationXML,
			SnippetExtractorFactory.endAnnotationXML,
			SnippetExtractorFactory.breakAnnotationXML,
			SnippetExtractorFactory.resumeAnnotationXML);
        }
        //return a XMLSnippetExtractor for parsing fractal files (XML format)
        if (file.toString().endsWith(".fractal")) {
            SnippetExtractorFactory.logger.debug("Fractal snippet parser started for file: " + file +
                " and target directory " + targetDir + ". The annotations used are: " +
                SnippetExtractorFactory.startAnnotationFractal + ", " +
                SnippetExtractorFactory.endAnnotationFractal+ ", " +
                SnippetExtractorFactory.breakAnnotationFractal + " and " +
                SnippetExtractorFactory.resumeAnnotationFractal);
            return new XMLSnippetExtractor(file, targetDir,
			SnippetExtractorFactory.startAnnotationFractal,
			SnippetExtractorFactory.endAnnotationFractal,
			SnippetExtractorFactory.breakAnnotationFractal,
			SnippetExtractorFactory.resumeAnnotationFractal);
        }
        //return a JavaSnippetExtractor for parsing C files (java comments)
        if (file.toString().endsWith(".c")) {
            SnippetExtractorFactory.logger.debug("C snippet parser started for file: " + file +
                " and target directory " + targetDir + ". The annotations used are: " +
                SnippetExtractorFactory.startAnnotationFractal + ", " +
                SnippetExtractorFactory.endAnnotationFractal+ ", " +
                SnippetExtractorFactory.breakAnnotationFractal + " and " +
                SnippetExtractorFactory.resumeAnnotationFractal);
            return new JavaSnippetExtractor(file, targetDir,
			SnippetExtractorFactory.startAnnotationFractal,
			SnippetExtractorFactory.endAnnotationFractal,
			SnippetExtractorFactory.breakAnnotationFractal,
			SnippetExtractorFactory.resumeAnnotationFractal);
        }
        throw new IOException();
    }

    /**
     * @return the startAnnotationJava
     */
    public static String getStartAnnotationJava() {
        return SnippetExtractorFactory.startAnnotationJava;
    }

    /**
     * @param startAnnotationJava the startAnnotationJava to set
     */
    public static void setStartAnnotationJava(final String startAnnotationJava) {
        SnippetExtractorFactory.startAnnotationJava = startAnnotationJava;
    }

    /**
     * @return the endAnnotationJava
     */
    public static String getEndAnnotationJava() {
        return SnippetExtractorFactory.endAnnotationJava;
    }

    /**
     * @param endAnnotationJava the endAnnotationJava to set
     */
    public static void setEndAnnotationJava(final String endAnnotationJava) {
        SnippetExtractorFactory.endAnnotationJava = endAnnotationJava;
    }

    /**
     * @return the startAnnotationXML
     */
    public String getStartAnnotationXML() {
        return SnippetExtractorFactory.startAnnotationXML;
    }

    /**
     * @param startAnnotationXML the startAnnotationXML to set
     */
    public void setStartAnnotationXML(final String startAnnotationXML) {
        SnippetExtractorFactory.startAnnotationXML = startAnnotationXML;
    }

    /**
     * @return the endAnnotationXML
     */
    public String getEndAnnotationXML() {
        return SnippetExtractorFactory.endAnnotationXML;
    }

    /**
     * @param endAnnotationXML the endAnnotationXML to set
     */
    public void setEndAnnotationXML(final String endAnnotationXML) {
        SnippetExtractorFactory.endAnnotationXML = endAnnotationXML;
    }

    /**
     * @return the startAnnotationFractal
     */
    public static String getStartAnnotationFractal() {
        return SnippetExtractorFactory.startAnnotationFractal;
    }

    /**
     * @param startAnnotationFractal the startAnnotationFractal to set
     */
    public static void setStartAnnotationFractal(final String startAnnotationFractal) {
        SnippetExtractorFactory.startAnnotationFractal = startAnnotationFractal;
    }

	/**
	 * @return the endAnnotationFractal
	 */
	public static String getEndAnnotationFractal() {
		return endAnnotationFractal;
	}

	/**
	 * @param endAnnotationFractal the endAnnotationFractal to set
	 */
	public static void setEndAnnotationFractal(String endAnnotationFractal) {
		SnippetExtractorFactory.endAnnotationFractal = endAnnotationFractal;
	}

}
