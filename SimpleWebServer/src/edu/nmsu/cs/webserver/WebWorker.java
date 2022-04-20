package edu.nmsu.cs.webserver;

/**
 * Web worker: an object of this class executes in its own new thread to receive and respond to a
 * single HTTP request. After the constructor the object executes on its "run" method, and leaves
 * when it is done.
 *
 * One WebWorker object is only responsible for one client connection. This code uses Java threads
 * to parallelize the handling of clients: each WebWorker runs in its own thread. This means that
 * you can essentially just think about what is happening on one client at a time, ignoring the fact
 * that the entirety of the webserver execution might be handling other clients, too.
 *
 * This WebWorker class (i.e., an object of this class) is where all the client interaction is done.
 * The "run()" method is the beginning -- think of it as the "main()" for a client interaction. It
 * does three things in a row, invoking three methods in this class: it reads the incoming HTTP
 * request; it writes out an HTTP header to begin its response, and then it writes out some HTML
 * content for the response content. HTTP requests and responses are just lines of text (in a very
 * particular format).
 * 
 * @author Jon Cook, Ph.D.
 * 
 * edited by Remington Crichton - 2/13/2022
 * 
 **/

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WebWorker implements Runnable
{
	private Socket socket;
	public boolean valid = false; //Public variable representing whether requested file was found. 
	public boolean fileAccess = false; //Public variable representing whether user requested file. 
	public boolean logoAccess = false; 
	public boolean fileImage = false; 
	public String secure = ""; //Public string containing the file path requested. 
	public String contentType = ""; 

	/**
	 * Constructor: must have a valid open socket
	 **/
	public WebWorker( Socket s )
	{
		socket = s;
	}

	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and then returns, which
	 * destroys the thread. This method assumes that whoever created the worker created it with a
	 * valid open socket object.
	 **/

	//Calls methods to properly handle Server request: 
	public void run()
	{
		System.err.println( "Handling connection..." );
		try
		{
			InputStream is = socket.getInputStream(); //Pulling in stuff from web browser
			OutputStream os = socket.getOutputStream(); //Sending back to the browser

			readHTTPRequest( is ); 
			writeHTTPHeader( os, contentType );
			writeContent( os );

			os.flush();
			socket.close();
		}
		catch ( Exception e )
		{
			System.err.println( "Output error: " + e );
		}
		System.err.println( "Done handling connection." );
		return;
	}

	/**
	 * Read the HTTP request header.
	 **/

	//Seperates file Path from GET request and checks whether file exists: 
	private void readHTTPRequest( InputStream is )
	{
		String line;
		String fileRequest = ""; 
		String splitRequest[] = {}; 
		
		BufferedReader r = new BufferedReader( new InputStreamReader(is) );

		while ( true )
		{ 
			try 
			{
				while ( !r.ready() )
					Thread.sleep( 1 );

				line = r.readLine();
				System.err.println( "Request line: (" + line + ")" );
				
				//Check to see if server request contains file request: 
				if( line.contains( "GET" ) &&  line.contains( ".html" ) ){ //User requested file
					fileAccess = true; 
					fileImage = false; 
					contentType = "text/html"; 

					fileRequest = line; 
					splitRequest = fileRequest.split( "\\s+" ); //Seperate GET request at spaces
					secure = splitRequest[1]; //index 1 includes file path (index 0 = GET, index 2 = HTTP/1.1)

					secure = secure.substring( 1 ); //Remove initial forward slash and store address in public variable

					File file = new File( secure ); 
						valid = file.exists(); //Make sure that file can be opened
				} //end if
				else if( line.contains( "GET" ) && ( line.contains( ".gif" ) || line.contains( ".jpeg" ) || line.contains( ".png" ) ) ){ //User requested image
					fileAccess = true; 
					fileImage = true; 

					if( line.contains( ".gif") ){
						contentType = "image/gif";
					}  
					else if( line.contains( ".jpeg" ) ){ 
						contentType = "image/jpeg"; 
					} 
					else if( line.contains( ".png" ) ){ 
						contentType = "image/png";
					} 

					fileRequest = line; 
					splitRequest = fileRequest.split( "\\s+" ); //Seperate GET request at spaces
					secure = splitRequest[1]; //index 1 includes file path (index 0 = GET, index 2 = HTTP/1.1)

					secure = secure.substring( 1 ); //Remove initial forward slash and store address in public variable

					File file = new File( secure ); 
						valid = file.exists(); //Make sure that file can be opened
				} //end else if
				else if( line.contains( "GET" ) && line.contains( ".ico" ) ){ //Handle logo request
					contentType = "image/png"; 
					valid = true; 
					logoAccess = true; 
				} //end else if
				else if( line.contains( "GET" ) ){ //User requested webpage but no file. 
					fileAccess = false; 
					valid = true; 
				} //end else if

				if (line.length() == 0)
					break;
			} //end try
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			} //end catch
		} //end while
	} //End Read Request

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/

	//Sends correct error code based on whether file was found: 
	private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));

		//Send correct error code: 
		if( valid == true && fileAccess == true ){ //User requested valid file. 
			os.write("HTTP/1.1 200 OK\n".getBytes()); //File Exists
		} //end if
		else if( valid == true && fileAccess == false ){ //User requested webserver but no file. 
			os.write("HTTP/1.1 200 OK\n".getBytes()); //No file requested. Default page will be written. 
		} //end if
		else{
			os.write( "HTTP/1.1 404 NOT FOUND\n".getBytes() ); //File requested does not Exist
		}
		os.write( "Date: ".getBytes() );
		os.write( (df.format(d)).getBytes() );
		os.write( "\n".getBytes() );
		os.write( "Server: Jon's very own server\n".getBytes() );
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
		os.write( "Connection: close\n".getBytes() );
		os.write( "Content-Type: ".getBytes() );

		if( ( contentType == "text/html" ) || ( valid == true && ( contentType == "image/jpeg" || 
			contentType == "image/png" || contentType == "image/gif" ) ) ){ 

			os.write( contentType.getBytes() ); //For program 2
		} //end if
		else{ //Image was not found and we need to print html - 404 not found
			contentType = "text/html"; 
			os.write( contentType.getBytes() ); //For program 2
		} //end else

		os.write( "\n\n".getBytes() ); // HTTP header ends with 2 newlines
		return;
	} //End Write Header

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/

	//This method sends the html file as well as identify html tags and change them: 
	private void writeContent(OutputStream os) throws Exception
	{ 
		if( valid == true && fileAccess == true && fileImage == false ){ //If text file was found 
			String serverName = "The wonderfull... The Great... The FANTASTIC!... Remington's Serverrrr!!"; 
			String image = "<img src='ghost.png'>"; 

			Date today = new Date();

			BufferedReader fileRead = new BufferedReader( new FileReader( secure ) ); 
			String fileHold = ""; 

			while( ( fileHold = fileRead.readLine() ) != null ){ 
				if( fileHold.contains( "<cs371server>" ) )  
					fileHold = fileHold.replace( "<cs371server>", serverName ); 
				else if( fileHold.contains( "<cs371date>" ) ) 
					fileHold = fileHold.replace( "<cs371date>", today.toString() );
				else if(fileHold.contains( "<image>" ) ) 
					fileHold = fileHold.replace( "<image>", image); 

				os.write("<html><head></head><body>\n".getBytes());
				os.write(("<h3>"+ fileHold +"\n</h3>\n").getBytes()); //Print HTML on page. 
				os.write("</body></html>\n".getBytes());
			}//end while
		}//end if
		else if( valid == true && fileAccess == true && fileImage == true ){ //If image file was found  
			if( logoAccess == true ){ 
				secure = "favicon.png"; //write logo image
			} //end if
			
			File picture = new File( secure ); 

			try{ 
				os.write( Files.readAllBytes( picture.toPath() ) ); 
			}catch( IOException e ){ 
				e.printStackTrace();
			} //end catch
		}//end else if
		else if( valid == true && fileAccess == false && fileImage == false ) //No file requested. Write default page. 
		{ 
			os.write("This server works! But you did not request me to read a file :(".getBytes());
		} //end else if
		else{ //File was not found - Print 404 on page rather than HTML file
			os.write("<html><head></head><body>\n".getBytes());
			os.write(("<h3> 404 Not Found \n</h3>\n").getBytes()); 
			os.write("</body></html>\n".getBytes());
		} //end else
	} //end Write Content 
} // end class