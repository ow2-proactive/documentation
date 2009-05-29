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
package doc.tutorials;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Entry point class for extracting tutorials from code files.
 *
 * @author The ProActive Team
 *
 */
public class Tutorializer {

    private static Logger logger = Logger.getLogger(Tutorializer.class.getName());

    /**
     * Files that are excluded from snippet checks. This is necessary as some files might contain
     * the tag strings for other purposes than extracting code.
     */
    private final List<String> EXCLUDED_FILES = Arrays.asList(new String[] { "SnippetExtractorFactory.java",
            "UpdateCopyrightAndVersion.java", "main.xml" , "profiled.xml" });
    private String[] fileTypes = { ".java", ".xml", ".fractal", ".c" };

    /**
     * Constructor
     */
    public Tutorializer() {
    }

    /**
     * Extracts code tutorials from all the java files
     * located into the specified directory or into its sub-directories
     *
     * ** recursive method **
     *
     * @param dir  the directory to start from - all the
     * sub-directories will be checked
     * @param target the directory where to put the tutorial file
     */
    public void startExtraction(final File dir, final File target) {

        // List the source directory.
    	// If the current file is a directory,
    	// then applies this method to this directory.
        // If the file is a java, xml or fractal file,
    	// then checks for annotations.
        // otherwise ignore
        final File[] elements = dir.listFiles();
        for (File file : elements) {
            if (file.isDirectory()) {
            	File targetTmp = new File(target + "/" + file.getName());
                this.startExtraction(file, targetTmp);
            } else {
                for (String extension : this.fileTypes) {
                    if (file.toString().endsWith(extension) && !EXCLUDED_FILES.contains(file.getName())) {
                        new TutorialExtractor(file, target).run();
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
        if (args.length >= 2) {
            final File sourceDir = new File(args[0]);
            final File targetDir = new File(args[1]);
            if (sourceDir.isDirectory() && targetDir.isDirectory()) {
                Tutorializer.logger.info("Processing starting from: " + sourceDir + ", outputting to: " +
                    targetDir);
                final Tutorializer parser = new Tutorializer();
                parser.startExtraction(sourceDir, targetDir);
                Tutorializer.logger.info("Tutorial parsing completed.");
                return;
            }
        }
        Tutorializer.logger.error("The tutorial parser takes "
            + "two parameters. The first one is the source directory and the second "
            + "is the target directory. The source directory will be traversed recursively "
            + "and every file with the specified extensions");
        Tutorializer.logger
				.error("Not enough arguments or arguments are not directories.");
	}

}
