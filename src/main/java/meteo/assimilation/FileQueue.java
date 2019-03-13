package meteo.assimilation;

import java.io.File;
import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Holds file data between listing updates;
 * Used to determine whether a file is still arriving
 */
@Slf4j
public class FileQueue
{
	/**
	 * Time since the input file stopped changing till it is considered fully arrived and can be processed
	 * TODO: to config
	 */
	private static long FILE_STABLIZING_INTERVAL = 1000 * 5; // 5 sec

	/**
	 * Keeps file data for comparison
	 */
	private final LinkedHashMap <String, FileStamp> fileStamps = new LinkedHashMap <String, FileStamp> ();

	
	public class FileStamp
	{
		
		@Getter long time;
		@Getter long size;
	
		public FileStamp(File file )
		{
			update( file.length() );
		}
		
		public void update( long size)
		{
			this.size = size;
			this.time = System.currentTimeMillis();
		}
	
		public long length() { return size; }
	}


	public boolean checkIfReady(File file) 
	{
		long now = System.currentTimeMillis();
		
		String fileID = this.fileID( file );

		FileStamp fileData = fileStamps.get( fileID );
		
		/////////////////////////////////////////////////////////////
		// CHECK IF THE FILE IS READY FOR ASSIMILATION

		if(file.isDirectory())
			return false;  // someone put a directory inside input folder, ignore it

		if(fileData == null) // new file:
		{
			fileData = new FileStamp( file);
			fileStamps.put( fileID, fileData );
		}

		if( file.length() != fileData.length() )
		{
			fileData.update( file.length());
			log.trace("File {} is still growing, skipping for meanwhile.", file.getName());
			return false; // file is still not fully copied
		}
		
		if( now - fileData.getTime() < FILE_STABLIZING_INTERVAL )
		{
			log.trace("File " + file + " too young, skipping for meanwhile.");
			return false; // lets give it more time
		}
		return true;
	}
	

	private String fileID( File file )
	{
		return file.getAbsolutePath();
	}


	public void remove(File file) {
		fileStamps.remove( this.fileID( file ) );
	}

}