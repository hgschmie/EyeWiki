package com.ecyrd.jspwiki.stress;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.ecyrd.jspwiki.FileUtil;
import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.WikiProperties;
import com.ecyrd.jspwiki.providers.FileSystemProvider;

public class SpeedTest extends TestCase
{
    private static int ITERATIONS = 1000;
    public static final String NAME1 = "Test1";

    PropertiesConfiguration conf = new PropertiesConfiguration();

    TestEngine engine;

    public SpeedTest( String s )
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

    public void testSpeed1()
        throws Exception
    {
        InputStream is = getClass().getResourceAsStream("/TextFormattingRules.txt");
        Reader      in = new InputStreamReader( is, "ISO-8859-1" );
        StringWriter out = new StringWriter();
        Benchmark mark = new Benchmark();

        FileUtil.copyContents( in, out );

        engine.saveText( NAME1, out.toString() );

        mark.start();

        for( int i = 0; i < ITERATIONS; i++ )
        {
            String txt = engine.getHTML( NAME1 );
            assertTrue( 0 != txt.length() );
        }

        mark.stop();

        System.out.println( ITERATIONS+" pages took "+mark.getDurationMs()+" ms (="+
                            mark.getDurationMs()/ITERATIONS+" ms/page)" );
    }

    public void testSpeed2()
        throws Exception
    {
        InputStream is = getClass().getResourceAsStream("/TestPlugins.txt");
        Reader      in = new InputStreamReader( is, "ISO-8859-1" );
        StringWriter out = new StringWriter();
        Benchmark mark = new Benchmark();

        FileUtil.copyContents( in, out );

        engine.saveText( NAME1, out.toString() );

        mark.start();

        for( int i = 0; i < ITERATIONS; i++ )
        {
            String txt = engine.getHTML( NAME1 );
            assertTrue( 0 != txt.length() );
        }

        mark.stop();

        System.out.println( ITERATIONS+" plugin pages took "+mark.getDurationMs()+" ms (="+
                            mark.getDurationMs()/ITERATIONS+" ms/page)" );
    }

    public static Test suite()
    {
        return new TestSuite( SpeedTest.class );
    }
}
