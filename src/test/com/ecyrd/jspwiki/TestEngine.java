package com.ecyrd.jspwiki;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.providers.BasicAttachmentProvider;
import com.ecyrd.jspwiki.providers.FileSystemProvider;

/**
 *  Simple test engine that always assumes pages are found.
 */
public class TestEngine extends WikiEngine
{
    static Logger log = Logger.getLogger( TestEngine.class );

    public TestEngine(Configuration conf)
        throws WikiException
    {
        super(conf);
    }

    public static void emptyWorkDir()
    	throws Exception
    {
        PropertiesConfiguration conf = new PropertiesConfiguration();
        
        try
        {
            conf.load( findTestProperties() );
        
            String workdir = conf.getString( WikiEngine.PROP_WORKDIR );
            if( workdir != null )
            {
                File f = new File( workdir );
                
                if( f.exists() && f.isDirectory() && new File( f, "refmgr.ser" ).exists() )
                {
                    deleteAll( f );
                }
            }
        }
        catch( IOException e ) {} // Fine   
    }
    
    public static final Reader findTestProperties()
	throws Exception
    {
        return findTestProperties( "/jspwiki.properties" );
    }

    public static final Reader findTestProperties( String properties )
    	throws Exception
    {
    	InputStream is = TestEngine.class.getResourceAsStream( properties );
    	return new InputStreamReader(is, "UTF-8");
    }

    /**
     *  Deletes all files under this directory, and does them recursively.
     */
    public static void deleteAll( File file )
    {
        if( file != null )
        {
            if( file.isDirectory() )
            {
                File[] files = file.listFiles();

                if( files != null ) 
                {
                    for( int i = 0; i < files.length; i++ )
                    {
                        if( files[i].isDirectory() )
                        {
                            deleteAll(files[i]);
                        }

                        files[i].delete();
                    }
                }
            }
            
            file.delete();
        }
    }

    /**
     *  Copied from FileSystemProvider
     */
    protected static String mangleName( String pagename )
        throws IOException
    {
        Properties properties = new Properties();
        String m_encoding = properties.getProperty( WikiEngine.PROP_ENCODING, 
                WikiEngine.PROP_ENCODING_DEFAULT );
        
        pagename = TextUtil.urlEncode( pagename, m_encoding );
        pagename = TextUtil.replaceString( pagename, "/", "%2F" );
        return pagename;
    }

    /**
     *  Removes a page, but not any auxiliary information.  Works only
     *  with FileSystemProvider.
     */
    public static void deleteTestPage( String name )
    {
        PropertiesConfiguration conf = new PropertiesConfiguration();
        
        try
        {
            conf.load( findTestProperties() );
            String files = conf.getString( WikiProperties.PROP_PAGEDIR );

            File f = new File( files, mangleName(name)+FileSystemProvider.FILE_EXT );

            f.delete();
        }
        catch( Exception e ) 
        {
            log.error("Couldn't delete "+name, e );
        }
    }

    /**
     *  Deletes all attachments related to the given page.
     */
    public void deleteAttachments( String page )
    {
        try
        {
            String files = getWikiConfiguration().getString( WikiProperties.PROP_STORAGEDIR );

            File f = new File( files, TextUtil.urlEncodeUTF8( page ) + BasicAttachmentProvider.DIR_EXTENSION );

            deleteAll( f );
        }
        catch( Exception e )
        {
            log.error("Could not remove attachments.",e);
        }
    }

    /**
     *  Makes a temporary file with some content, and returns a handle to it.
     */
    public File makeAttachmentFile()
        throws Exception
    {
        File tmpFile = File.createTempFile("test","txt");
        tmpFile.deleteOnExit();

        FileWriter out = new FileWriter( tmpFile );
        
        FileUtil.copyContents( new StringReader( "asdfaäöüdfzbvasdjkfbwfkUg783gqdwog" ), out );

        out.close();
        
        return tmpFile;
    }

    public void saveText( String pageName, String content )
        throws WikiException
    {
        WikiContext context = new WikiContext( this, new WikiPage(pageName) );

        saveText( context, content );
    }

    public static void trace()
    {
        try
        {
            throw new Exception("Foo");
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
