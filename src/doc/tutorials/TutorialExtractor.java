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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * This class is responsible with the actual parsing of the files.
 *
 * @author The ProActive Team
 *
 */
public class TutorialExtractor {
	
	protected static Logger logger = Logger.getLogger(TutorialExtractor.class.getName());

    private final String startAnnotation = "@tutorial-start";
    private final String endAnnotation = "@tutorial-end";

    private final String breakAnnotation = "@tutorial-break";
    private final String resumeAnnotation = "@tutorial-resume";

    protected final File target;
    protected final File targetDirectory;
    
    private BufferedReader reader;

    /**
     * Creates a tutorial extractor with the given source and target
     *
     * @param file
     *            file to be parsed
     * @param targetDir
     *            directory to save to
     */
    public TutorialExtractor(final File file, final File targetDir) {
        TutorialExtractor.logger.setLevel(Level.INFO);
        this.target = file;
        this.targetDirectory = targetDir;
    }

    /**
     * @see java.lang.Runnable#run()
     *
     * Checks if the supplied file is valid and contains tags.
     * If so, tries to extract the tutorial.
     */
    public void run() {

        try {
            this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.target)));
            try {
	        // check if the file is valid and then parse
				if (this.fileIsValid()) {
					TutorialExtractor.logger.debug("File is valid, trying to extract: " + this.target);
					this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.target)));
					try {
					   this.extractTutorial();
					} finally {
					   this.reader.close();
					}
				}
            } finally {
            	this.reader.close();
            }

        } catch (final IOException ioExcep) {
            ioExcep.printStackTrace();
            TutorialExtractor.logger.error("Extraction error for file: " + this.target + " " +
                ioExcep.getMessage());
        }
    }

    /**
     * Checks the file validity.
     *
     * Validity conditions for start and end tags:
     * 1. if a start tag exists, then a end one also exists and is after.
     * 3. no duplicate start or end tags
     *
     * Validity conditions for break and resume tags:
     * 1. Before each break/resume tag, there is a corresponding start tag
     * 2. After each break/resume tag, there is no corresponding end tag
     * => 1 & 2: break/resume tags are between a start and an end tags
     * 3. Each break tag si followed by a resume tag before the end tag
     * 4. Before each break tag, there is no such a break tag already read without
     * its corresponding resume tag
     * => 4: break blocks cannot be imbricated.
     * 5. Before each resume tag, there is a corresponding break tag
     *
     * @return a boolean value saying if the file is valid or not
     * 		   return true only if some tags are present and valid
     * 		   If no tag is present, then return false since the file has not
     * 		   to be parsed.
     * @throws IOException file error
     */
    private boolean fileIsValid() throws IOException {
        
    	String line;
        
    	// Line markers for testing and logging
        Integer startTagLine = 0;
        Integer endTagLine = 0;
        Integer breakTagLine = 0;
        
        boolean broken = false;
        boolean isTagged = false;
        boolean fileValid = true;
        
        // The current line number
        int lineCounter = 1;
        
        // The read line
        line = this.reader.readLine();
        
        // While there are still lines
        while ((line != null) && fileValid) {
        	
        	// Read this.endAnnotation
            if (line.contains(this.endAnnotation)) {
                
                // check that the tag is unique
                if (endTagLine != 0) {
                    TutorialExtractor.logger.error("[" + lineCounter + "]  Duplicate stop tags" +
                        " at line " + "[" + lineCounter + "] and [" + endTagLine + "] " + ". File [" +
                        this.target + "] will not be parsed and some code parts may" +
                        " not appear in the final document");
                    fileValid = false;
                }
                
                // check that we have already read a start annotation
                if (startTagLine == 0) {
                    TutorialExtractor.logger.error("[" + lineCounter + "]  Present stop tag" +
                        " at line " + "[" + lineCounter + "] whereas there is no start tag. File [" +
                        this.target + "] will not be parsed and some code parts may" +
                        " not appear in the final document");
                    fileValid = false;
                }
                endTagLine = lineCounter;
                isTagged = true;
            }
            
            // Read this.startAnnotation
            if (line.contains(this.startAnnotation)) {
                
            	// Check that the tag is unique
                if (startTagLine != 0) {
                    TutorialExtractor.logger.error("[" + lineCounter + "]  Duplicate start tags" +
                        " at line " + "[" + lineCounter + "] and [" + startTagLine + "] " + ". File [" +
                        this.target + "] will not be parsed and some code parts may" +
                        " not appear in the final document");
                    fileValid = false;
                }
                
                startTagLine = lineCounter;
                isTagged = true;
            }
            
            //Read this.breakAnnotation
            if (line.contains(this.breakAnnotation)) {
            	
            	// Check that a break tag has not been read before (without being closed)
                if (broken) {
                    TutorialExtractor.logger.error("[" + lineCounter + "]  Imbricated break tags" +
                        "] at " + "[" + lineCounter + "] and [" + breakTagLine + "] " + ". File [" +
                        this.target + "] will not be parsed and some code parts may" +
                        " not appear in the final document");
                    fileValid = false;
                }
                
                // Check that a start line has already been read
                if (startTagLine == 0) {
                    TutorialExtractor.logger.error("[" + lineCounter + "]  Present break tag " +
                        "] at line " + "[" + lineCounter + "] whereas there is no corresponding start tag before" + ". File [" +
                        this.target + "] will not be parsed and some code parts may" +
                        " not appear in the final document");
                    fileValid = false;
                }
                
                // Check that no end tag has already been read
                if (endTagLine != 0) {
                    TutorialExtractor.logger.error("[" + lineCounter + "]  Present break tag" +
                        "] at line " + "[" + lineCounter + "] whereas there is a corresponding end tag before" + ". File [" +
                        this.target + "] will not be parsed and some code parts may" +
                        " not appear in the final document");
                    fileValid = false;
                }
                
                breakTagLine = lineCounter;
                broken = true;
                isTagged = true;
            }
            
            if (line.contains(this.resumeAnnotation)) {
                
            	// Check that a break tag has already been read
                if (!broken) {
                    TutorialExtractor.logger.error("[" + lineCounter + "]  Present resume tags" +
                        "] at line " + "[" + lineCounter + "] whereas there is no corresponding break tag before" + ". File [" +
                        this.target + "] will not be parsed and some code parts may" +
                        " not appear in the final document");
                    fileValid = false;
                }
                
             	// Check that no end tag has already been read
                if (endTagLine != 0) {
                    TutorialExtractor.logger.error("[" + lineCounter + "]  Present resume tag" +
                        "] at line " + "[" + lineCounter + "] whereas there is a corresponding end tag before" + ". File [" +
                        this.target + "] will not be parsed and some code parts may" +
                        " not appear in the final document");
                    fileValid = false;
                }
                
                broken = false;
                isTagged = true;
            }
            lineCounter++;
            line = this.reader.readLine();
        }
        
        // Check that the last break section has been closed by a resume tag
        if (broken) {
        	// Missing resume tags
        	TutorialExtractor.logger.error("Missings resume tag");
			TutorialExtractor.logger.error("Break tag present at line [" + breakTagLine + "]");
			fileValid = false;
        }

        // Check that the start section has been closed by an end tag
    	if ((endTagLine == 0) && (startTagLine != 0)) {
        	TutorialExtractor.logger.error("Missings end tag");
			TutorialExtractor.logger.error("Start tag present at line [" + startTagLine + "]");
			fileValid = false;
    	}
        
        return (isTagged) ? fileValid : false;
    }

    private boolean isAnnotatedLine(String line) {
    	return (line.contains(this.endAnnotation)) ||
    		   (line.contains(this.startAnnotation)) || 
    		   (line.contains(this.breakAnnotation)) ||
    		   (line.contains(this.resumeAnnotation)) ||
    		   (line.contains("@snippet-start")) ||
    		   (line.contains("@snippet-end")) ||
    		   (line.contains("@snippet-break")) ||
    		   (line.contains("@snippet-resume"));
    }
    
    private boolean isXmlFormat() {
		return target.getName().toLowerCase().endsWith(".xml") ||
			   target.getName().toLowerCase().endsWith(".fractal");
    }
    
    private boolean isJavaFormat() {
		return target.getName().toLowerCase().endsWith(".java") ||
			   target.getName().toLowerCase().endsWith(".c");
    }

    /**
     * Extracts tutorial from the file this.target
     * This file is considered to be valid as this method will
     * be called if and only if the fileIsValid method has previously
     * returned true.
     *
     * @throws IOException
     */
    private void extractTutorial() throws IOException {
        String line;
        BufferedWriter writer = null;
        boolean broken = false;
        boolean started = false;
        boolean ended = false;
        boolean hasReadPackage = false;
        String xmlRoot = "";
        String copyright = "";
        line = this.reader.readLine();
        while (line != null) {
        	
            // For .xml and .fractal files, we cannot write the start annotation before
            // the <?xml> tag. Thus, if we are extracted a tutorial from such a file,
            // we have to add the first line describing the xml version as well as the
            // encoding attribute.
            // For this, we have to retrieve the <?xml> line which should be read before
            // the tutorial starts.
			if (!started && this.isXmlFormat() && line.contains("<?xml")) {
				xmlRoot = line;
			}

			Pattern pattern = Pattern.compile("^\\s*package");
			Matcher matcher = pattern.matcher(line);
			if (this.isJavaFormat() && matcher.find()) {
				hasReadPackage = true;
			}
			
			if (this.isJavaFormat() && !hasReadPackage && !isAnnotatedLine(line)) {
				copyright += line + "\n";
			}
			
            // if we found an end annotation close the writer
            if (line.contains(this.endAnnotation)) {
            	assert started;
            	assert !ended;
            	assert writer != null;
                writer.flush();
                writer.close();
                ended = true;
            }

            // if writer still exist, e.g. the end annotation
            // hasn't been reached add to the tutorial file
            if (!this.isAnnotatedLine(line)) {
            		// if a break block has been openned for this key, we do not treat this line
                    if (started && !ended && !broken) {
                    	assert writer != null;
                    	writer.append(line);
                    	writer.newLine();
                    }
            }

            // if a start tag encountered, create the file and the writer
            if (line.contains(this.startAnnotation)) {
            	
            	// Create the directory
                if (!this.targetDirectory.exists()) {
                	this.targetDirectory.mkdirs();
                }
                final File targetFile = new File(this.targetDirectory, this.target.getName());
                
                //if file exist log a warning, otherwise proceed as normal
                if (targetFile.exists()) {
                    TutorialExtractor.logger.warn(" File " + targetFile +
                        " already exists and it will NOT be overwritten. " +
                        " Either the directory has not been emptied" +
                        " or there are global duplicate tags. The file name is" + ":" + targetFile.getName() +
                        ". The tag has be read from file " + this.target);
                } else {
                    writer = this.createFile(targetFile);
                    started = true;

                    if (isXmlFormat() && (xmlRoot.length() > 0)) {
	                    writer.append(xmlRoot);
	                    writer.newLine();
                    }
                    if (isJavaFormat() && (copyright.length() > 0)) {
	                    writer.append(copyright);
                    }
                    
                    TutorialExtractor.logger.info("File [" + this.target.getName() + "] created.");
                }
            }

            //If break annotation appears, then broken is updated
            if (line.contains(this.breakAnnotation)) {
            	assert !broken;
            	assert started;
            	broken = true;
            }

            //If resume annotation appears, then broken is updated
            if (line.contains(this.resumeAnnotation)) {
				assert broken;
				assert started;
				broken = false;
            }
            line = this.reader.readLine();
        }
    }

    /**
     * Creates a new file with the specified name
     *
     * @param file the file name to be created
     * @return a BufferedWriter representing the created file
     */
    private BufferedWriter createFile(final File targetFile) {

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(targetFile));
            TutorialExtractor.logger.debug("Creating: " + targetFile);
            return writer;
        } catch (final IOException e) {
            e.printStackTrace();
            TutorialExtractor.logger.error("File " + targetFile + " could not be created.");
        }
        return null;
    }
}
