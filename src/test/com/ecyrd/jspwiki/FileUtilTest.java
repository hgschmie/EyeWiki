
package com.ecyrd.jspwiki;

import junit.framework.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;

public class FileUtilTest extends TestCase
{
    public FileUtilTest( String s )
    {
        super( s );
        Properties props = new Properties();
        try
        {
            props.load( TestEngine.findTestProperties() );
            PropertyConfigurator.configure(props);
        }
        catch( IOException e ) {}
    }

    public void setUp()
        throws Exception
    {
    }

    public void tearDown()
    {
    }

    /**
     *  This test actually checks if your JDK is misbehaving.  On my own Debian
     *  machine, changing the system to use UTF-8 suddenly broke Java, and I put
     *  in this test to check for its brokenness.  If your tests suddenly stop
     *  running, check if this one is failing too.  If it is, your platform is
     *  broken.  If it's not, seek for the bug in your code.
     */
    public void testJDKString()
        throws Exception
    {
        String src = "abc\u00e4\u00e5\u00a6";

        String res = new String( src.getBytes("ISO-8859-1"), "ISO-8859-1" );

        assertEquals( src, res );
    }

    public void testReadContentsLatin1()
        throws Exception
    {
        String src = "abc\u00e4\u00e5\u00a6";

        String res = FileUtil.readContents( new ByteArrayInputStream( src.getBytes("ISO-8859-1") ),
                                            "ISO-8859-1" );

        assertEquals( src, res );
    }

    /**
     *  Check that fallbacks to ISO-Latin1 still work.
     */
    public void testReadContentsLatin1_2()
        throws Exception
    {
        String src = "abc\u00e4\u00e5\u00a6def";

        String res = FileUtil.readContents( new ByteArrayInputStream( src.getBytes("ISO-8859-1") ),
                                            "UTF-8" );

        assertEquals( src, res );
    }

    /**
       ISO Latin 1 from a pipe.

       FIXME: Works only on UNIX systems now.
    */
    public void testReadContentsFromPipe()
        throws Exception
    {
        String src = "abc\n123456\n\nfoobar.\n";

        // Make a very long string.

        for( int i = 0; i < 10; i++ )
        {
            src += src;
        }

        src += "\u00e4\u00e5\u00a6";

        File f = FileUtil.newTmpFile( src, "ISO-8859-1" );

        String[] envp = {};

        try
        {
            Process process = Runtime.getRuntime().exec( "cat "+f.getAbsolutePath(), 
                                                         envp, 
                                                         f.getParentFile() );

            String result = FileUtil.readContents( process.getInputStream(), "UTF-8" );

            f.delete();

            assertEquals( src,
                          result );
        }
        catch( IOException e ) {}
    }

    public void testReadContentsReader()
        throws IOException
    {
        String data = "ABCDEF";

        String result = FileUtil.readContents( new StringReader( data ) );

        assertEquals( data,
                      result );
    }

    public static Test suite()
    {
        return new TestSuite( FileUtilTest.class );
    }
}
