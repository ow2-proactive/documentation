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
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Entry point class for extracting snippets from code files.
 * Snippets will be identified by the id next to the tag check
 * SnippetExtractorFactory.java for tags) and placed in the
 * ouput directory. To use in the docbook XML just use the
 * &lt;programlisting&gt;tag and include the needed snippet file.
 *
 * @author The ProActive Team
 *
 */
public class Snippetizer {

    private static Logger logger = Logger.getLogger(Snippetizer.class.getName());

    /**
     * Files that are excluded from snippet checks. This is necessary as some files might contain
     * the tag strings for other purposes than extracting code.
     */
    private final List<String> EXCLUDED_FILES = Arrays.asList(new String[] { "SnippetExtractorFactory.java",
            "UpdateCopyrightAndVersion.java", "main.xml" , "profiled.xml" });
    private final File targetDir;
    private String[] fileTypes = { ".java", ".xml", ".fractal", ".c" };

    /**
     * Constructor
     *
     * @param targetDir the directory where to put the extracted snippets
     */
    public Snippetizer(final File targetDir) {
        this.targetDir = targetDir;
    }

    /**
     * Extracts code snippets from all the java files
     * located into the specified directory or into its sub-directories
     *
     * ** recursive method **
     *
     * @param dir  the directory to start from - all the
     * sub-directories will be checked
     */
    public void startExtraction(final File dir) {

        // List the source directory.
	// If the current file is a directory,
	// then applies this method to this directory.
        // If the file is a java, xml or fractal file,
	// then checks for annotations.
        // otherwise ignore
        final File[] elements = dir.listFiles();
        for (File file : elements) {
            if (file.isDirectory()) {
                this.startExtraction(file);
            } else {
                for (String extension : this.fileTypes) {
                    if (file.toString().endsWith(extension) && !EXCLUDED_FILES.contains(file.getName())) {
                        //get the correct extractor and start it
                        try {
                            SnippetExtractorFactory.getExtractor(file, this.targetDir).run();
                        } catch (IOException e) {
                            logger.info("Only Java, XML, Fractal and C can be parsed. " + "Trying to parse [" +
                                file.getAbsolutePath() + "]   ", e);
                        }
                    }

                }
            }
        }
    }

    /**
     * main method
     *
     * @param args
     */
    public static void main(String[] args) {
        // TODO configure externally
        // Snippetizer.logger.setLevel(Level.ALL);
    	// PropertyConfigurator.configure("log4j.properties");
        if (args.length >= 2) {
			String sourceArgs = args[0];
			String[] sourcePaths = sourceArgs.split(":");
            final File targetDir = new File(args[1]);
            for ( String sourcePath : sourcePaths) {
	            final File sourceDir = new File(sourcePath);
	            if (sourceDir.isDirectory() && targetDir.isDirectory()) {
	                Snippetizer.logger.info("Processing starting from: " + sourceDir + ", outputting to: " +
	                    targetDir);
	                final Snippetizer parser = new Snippetizer(targetDir);
	                parser.startExtraction(sourceDir);
	            }
            }
            Snippetizer.logger.info("Snippet parsing completed.");
            return;
        }
        Snippetizer.logger.error("The snippet parser takes "
            + "two parameters. The first one is the source directory and the second "
            + "is the target directory. The source directory will be traversed recursively "
            + "and every file with the specified "
            + "extensions (check SnippetExtractorFactory.java"
            + " will be parsed for snippet parts");
        Snippetizer.logger
				.error("Not enough arguments or arguments are not directories.");
	}

}
