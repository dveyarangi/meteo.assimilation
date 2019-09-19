package meteo.assimilation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
import meteo.util.Env;

/**
 * Monitors a folder for arriving files and feeds fully arrived files to {@link #assimilator} 
 * for further processing.
 */
@Slf4j
public class FolderMonitor
{
	private MonitorCfg monitorCfg;
	
	/**
	 * File consumer
	 */
	private final FileAssimilator assimilator;
	
	private final AsListener listener;

	/**
	 * List of files in the input folder and their statuses
	 */
	private final FileQueue fileQueue;

	private File monitoredFolder;

	private File tempFolder;

	private File doneFolder;

	private File errorFolder;
	
	/**
	 * 
	 * @param monitorCfg 
	 * 
	 * @param assimilator - files processing module
	 * @param 
	 */
	public FolderMonitor(
			MonitorCfg monitorCfg, 
			FileAssimilator assimilator,
			AsListener listener)
	{
		this.monitorCfg = monitorCfg;
		this.assimilator = assimilator;
		
		this.listener = listener;
		
		this.fileQueue = new FileQueue();
		
		this.monitoredFolder = monitorCfg.getInputDir();
		
		this.tempFolder = monitorCfg.getTempDir() == null ? 
				new File(Env.cachePath(monitoredFolder.getName())) : 
				monitorCfg.getTempDir();
				
		this.doneFolder = monitorCfg.getDoneDir();
		this.errorFolder = monitorCfg.getErrorDir();
		
	}

	/**
	 * Processing files in temp folder 
	 * Files could remain there if the program was terminated during processing
	 */
	public void init()
	{
		assimilator.init(monitorCfg.getConfig());
		
		validateAssimilator(assimilator);
		
		log.trace("Started listing files in {}...", tempFolder.getAbsolutePath());
		List <File> tempFiles = Arrays.asList( tempFolder.listFiles() );
		// process the files:
		assimilateFiles( tempFiles );
		log.trace("Finished processing {}", tempFolder.getAbsolutePath() );
	}

	private void validateAssimilator( FileAssimilator assimilator2 )
	{
		log.info(String.format("Monitoring %10s - %s", 
				assimilator.getType().toUpperCase(), monitorCfg.getInputDir().getAbsolutePath() ) );
		if( monitorCfg.getDoneDir() == null )
			log.debug("No done folder specified for {} files, assimilated files will be deleted", assimilator.getType().toUpperCase() );
		
		if( monitorCfg.getErrorDir() == null )
			log.debug("No error folder specified for {} files, unhandleable files will be deleted", assimilator.getType().toUpperCase() );
	}

	/**
	 * Lists the monitored folder, reads files and submits read data to the store
	 */
	public void update()
	{
		log.trace("Started listing files in {}...", monitoredFolder.getAbsolutePath());
		// catching the moment:
		long now = System.currentTimeMillis();

		validateFolders();
		
		/////////////////////////////////////////////////////////////
		// listing the files in folder:
		File [] files = monitoredFolder.listFiles();

		
		/////////////////////////////////////////////////////////////
		List <File> readyFiles = new LinkedList <File> ();

		List <File> errorFiles = new LinkedList <File> ();

		// extracting list of files ready to be processed:
		for(File file : files)
		{
			// test whether the file has fully arrived to input folder
			if( !fileQueue.checkIfReady( file ) )
				continue;
			
			// test whether the file can be assimilated:
			if( !canAssimilate( file ) )
				errorFiles.add( file );
			else
				readyFiles.add( file );
		}

		/////////////////////////////////////////////////////////////
		// process the files:
		assimilateFiles( readyFiles );

		/////////////////////////////////////////////////////////////
		// handle unreadibles:
		handleErrors( errorFiles );
		/////////////////////////////////////////////////////////////
		// some auditing
		long duration = System.currentTimeMillis() - now;
		log.trace("Finished processing {} in {}ms", monitoredFolder.getAbsolutePath(), duration );
	}

	
	private void validateFolders() 
	{
		if(!monitoredFolder.exists()) // revalidate folder...
			throw new RuntimeException("Missing folder " + monitoredFolder );	
		if(!tempFolder.exists())
			tempFolder.mkdirs();
	}

	/**
	 * test if the file is readible
	 * @param file
	 * @return
	 */
	public boolean canAssimilate( File file )
	{
		boolean canAssimilate;
		try {
			canAssimilate = assimilator.canAssimilate( file );
		} 
		catch (IOException x) {
			log.error("Failed to access file " + file, x);
			return false;
		}
		
		if( !canAssimilate  ) // file arrived to wrong folder or is corrupted
		{
			log.warn("Cannot recognize file " + file);
			return false;
		}		
		
		return true;
	}
	
	/**
	 * Handle files that are fully arrived and are ready to be assimilated
	 * @param inputFiles
	 */
	private void assimilateFiles(List<File> inputFiles) 
	{
		log.trace("Assimilating {} input files in {}...", inputFiles.size(), monitoredFolder.getName());
		
		for(File file : inputFiles)
		{
			/////////////////////////////////////////////////////////////
			// move to processing folder, to prevent rare case when 
			// something tries to change the file during the processing:
			File processedFile = new File(tempFolder, file.getName());
			if( ! file.equals(processedFile)) // file can be already in temp dir
				try {
					FileUtils.moveFile(file, processedFile);
				}
				catch( FileExistsException e ) { log.debug("File already exists at target folder. Failed to move file {} to folder {} ", file.getName(), processedFile.getAbsoluteFile());}
				catch( FileNotFoundException e ) { 
					continue; // TODO: file removed, no need to warn? 
				}
				catch( IOException e ) { 
					log.error("IO error reading " + file.getName(), e);
					continue;
				} 
			
			/////////////////////////////////////////////////////////////
			// assimilating the file:
			AsOutcome outcome = AsOutcome.DROP;
			try
			{
				outcome = assimilator.assimilate( processedFile );
			} 
			catch( IOException e ) { 
				log.error("IO error reading " + file.getName(), e);
				outcome = AsOutcome.ERROR;
			} 
			catch( Exception e ) {
				log.error("Unexected server error while parsing " + file.getName(), e);
				outcome = AsOutcome.ERROR;
				if( listener != null )
					try {
						listener.assimilationFailed(file, assimilator.getType(), e);
					} catch(Exception lisx) { log.error("Listener failure", lisx); } 
				// no hasErrors = true here, getting here means parser bug
			}
			
			if( listener != null )
				try {
					listener.fileAssimilated(file, assimilator.getType(), outcome);
				} catch(Exception lisx) { log.error("Listener failure", lisx); } 

			
			/////////////////////////////////////////////////////////////
			// archive the file
			File targetDir = null;
			switch(outcome)
			{
			case SUCCESS: targetDir = doneFolder; break;
			case ERROR: targetDir = errorFolder; break;
			case DROP: targetDir = null;
			default: 
			}
			
			moveOrDelete( processedFile, targetDir );
			
			// and forget about it:
			fileQueue.remove( file );
		}

		log.trace("Done processing {} input files in {}.", inputFiles.size(), doneFolder.getName());
	}

	/**
	 * Moves or deletes ( if no targetDir provided ) the specified sourceFile 
	 * @param sourceFile
	 * @param targetDir
	 */
	private void moveOrDelete(File sourceFile, File targetDir)
	{
		File targetFile = targetDir == null ? null : new File( targetDir, sourceFile.getName() );
		if( targetFile == null ) // delete
		{
			sourceFile.delete();
			log.trace( "File {} was deleted.", sourceFile.getName() ) ;
			return;
		}
		
		try
		{
			if(targetFile.exists())
				targetFile.delete();
			FileUtils.moveFile( sourceFile, targetFile );
			log.trace( "File {} moved to folder {}", targetFile.getName(), targetDir.getAbsoluteFile() ) ;
		}
		catch( FileExistsException e ) { log.debug("File already exists at target folder. Failed to move file {} to folder {} ", targetFile.getName(), targetDir.getAbsoluteFile());}
		catch( IOException e ) { 
			log.warn("Failed to move file {} to folder {} ", targetFile.getName(), targetDir.getAbsoluteFile(), e);}
		
	}

	@Override
	public String toString()
	{
		return "monitor of " + monitoredFolder.getName();
	}

	
	private void handleErrors(List<File> errorFiles)
	{
		for(File file : errorFiles)
		{
			moveOrDelete( file, errorFolder);
			fileQueue.remove( file );
			
			if(listener != null )
				listener.fileAssimilated(file, assimilator.getType(), AsOutcome.ERROR );
		}
	}

	public void close() 
	{
	}

}
