
package com.ecyrd.jspwiki;
import java.util.Properties;
import java.io.*;

import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.providers.*;

/**
 *  Simple test engine that always assumes pages are found.
 */
public class TestEngine extends WikiEngine
{
    static Logger log = Logger.getLogger( TestEngine.class );

    public TestEngine( Properties props )
        throws WikiException
    {
        super( props );
    }

    public static final InputStream findTestProperties()
    {
        return findTestProperties( "/jspwiki.properties" );
    }

    public static final InputStream findTestProperties( String properties )
    {
        return TestEngine.class.getResourceAsStream( properties );
    }

    /**
     *  Deletes all files under this directory, and does them recursively.
     */
    public static void deleteAll( File file )
    {
        if( file.isDirectory() )
        {
            File[] files = file.listFiles();

            for( int i = 0; i < files.length; i++ )
            {
                if( files[i].isDirectory() )
                {
                    deleteAll(files[i]);
                }

                files[i].delete();
            }
        }

        file.delete();
    }

    /**
     *  Copied from FileSystemProvider
     */
    protected String mangleName( String pagename )
    {
        // FIXME: Horrible kludge, very slow, etc.
        if( "UTF-8".equals( getContentEncoding() ) )
            return TextUtil.urlEncodeUTF8( pagename );

        return java.net.URLEncoder.encode( pagename );
    }

    /**
     *  Removes a page, but not any auxiliary information.  Works only
     *  with FileSystemProvider.
     */
    public void deletePage( String name )
    {
        String files = getWikiProperties().getProperty( FileSystemProvider.PROP_PAGEDIR );

        try
        {
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
            String files = getWikiProperties().getProperty( BasicAttachmentProvider.PROP_STORAGEDIR );

            File f = new File( files, page+BasicAttachmentProvider.DIR_EXTENSION );

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
        
        FileUtil.copyContents( new StringReader( "asdfa���dfzbvasdjkfbwfkUg783gqdwog" ), out );

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
