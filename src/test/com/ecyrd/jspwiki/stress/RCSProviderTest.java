package com.ecyrd.jspwiki.stress;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.providers.FileSystemProvider;

/**
 *  Does com.ecyrd.jspwiki.stress testing on the RCSProvider.
 */
public class RCSProviderTest extends TestCase
{
    public static final String NAME1 = "Test1";

    PropertiesConfiguration conf = new PropertiesConfiguration();

    TestEngine engine;

    public RCSProviderTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
       	conf.load( TestEngine.findTestProperties("/jspwiki_rcs.properties") );

        engine = new TestEngine(conf);
    }

    public void tearDown()
    	throws Exception
    {
        String files = conf.getString(WikiProperties.PROP_PAGEDIR );

        File f = new File( files, NAME1+FileSystemProvider.FILE_EXT );

        f.delete();

        f = new File( files+File.separator+"RCS", NAME1+FileSystemProvider.FILE_EXT+",v" );

        f.delete();

        f = new File( files, "RCS" );

        f.delete();
    }

    /**
     *  Bug report by Anon: RCS under Windows 2k:
     * <PRE>
     * In getPageInfo of RCSFileProvider:
     *
     * Problem:
     *
     * With a longer rlog result, the break clause in the last "else if" 
     * breaks out of the reading loop before all the lines in the full 
     * rlog have been read in. This causes the process.wait() to hang.
     *
     * Suggested quick fix:
     *
     * Always read all the contents of the rlog, even if it is slower.
     * </PRE>
     *
     */

    public void testMillionChanges()
        throws Exception
    {
        String text = "";
        String name = NAME1;
        int    maxver = 200; // Save 200 versions.
        Benchmark mark = new Benchmark();

        mark.start();
        for( int i = 0; i < maxver; i++ )
        {
            text = text + ".";
            engine.saveText( name, text );
        }
        mark.stop();

        System.out.println("Benchmark: "+mark.toString(200)+" pages/second");
        WikiPage pageinfo = engine.getPage( NAME1 );

        assertEquals( "wrong version", maxver, pageinfo.getVersion() );
        // +2 comes from \r\n at the end of each file.
        assertEquals( "wrong text", maxver+2, engine.getText(NAME1).length() );
    }

    public static Test suite()
    {
        return new TestSuite( RCSProviderTest.class );
    }
}
