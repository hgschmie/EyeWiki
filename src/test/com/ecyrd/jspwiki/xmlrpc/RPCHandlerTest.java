
package com.ecyrd.jspwiki.xmlrpc;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.xmlrpc.XmlRpcException;

import com.ecyrd.jspwiki.TestEngine;
import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.attachment.Attachment;

public class RPCHandlerTest extends TestCase
{
    TestEngine m_engine;
    RPCHandler m_handler;

    static final String NAME1 = "Test";

    public RPCHandlerTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        PropertiesConfiguration conf = new PropertiesConfiguration();
        conf.load( TestEngine.findTestProperties() );

        m_engine = new TestEngine( conf );

        m_handler = new RPCHandler();
        m_handler.initialize( m_engine );
    }

    public void tearDown()
    	throws Exception
    {
        TestEngine.deleteTestPage( NAME1 );
        m_engine.deleteAttachments( NAME1 );
        TestEngine.emptyWorkDir();
    }

    public void testNonexistantPage()
    {
        try
        {
            byte[] res = m_handler.getPage( "NoSuchPage" );
            fail("No exception for missing page.");
        }
        catch( XmlRpcException e ) 
        {
            assertEquals( "Wrong error code.", RPCHandler.ERR_NOPAGE, e.code );
        }
    }

    public void testRecentChanges()
        throws Exception
    {
        String text = "Foo";
        String pageName = NAME1;

        m_engine.saveText( pageName, text );

        WikiPage directInfo = m_engine.getPage( NAME1 );

        Date modDate = directInfo.getLastModified();

        Calendar cal = Calendar.getInstance();
        cal.setTime( modDate );
        cal.add( Calendar.HOUR, -1 );

        // Go to UTC
        cal.add( Calendar.MILLISECOND, 
                 -(cal.get( Calendar.ZONE_OFFSET )+
                  (cal.getTimeZone().inDaylightTime( modDate ) ? cal.get( Calendar.DST_OFFSET ) : 0 ) ) );
        

        Vector v = m_handler.getRecentChanges( cal.getTime() );

        assertEquals( "wrong number of changes", 1, v.size() );
    }

    public void testRecentChangesWithAttachments()
        throws Exception
    {
        String text = "Foo";
        String pageName = NAME1;

        m_engine.saveText( pageName, text );

        Attachment att = new Attachment( NAME1, "TestAtt.txt" );
        att.setAuthor( "FirstPost" );
        m_engine.getAttachmentManager().storeAttachment( att, m_engine.makeAttachmentFile() );

        WikiPage directInfo = m_engine.getPage( NAME1 );

        Date modDate = directInfo.getLastModified();

        Calendar cal = Calendar.getInstance();
        cal.setTime( modDate );
        cal.add( Calendar.HOUR, -1 );

        // Go to UTC
        cal.add( Calendar.MILLISECOND, 
                 -(cal.get( Calendar.ZONE_OFFSET )+
                  (cal.getTimeZone().inDaylightTime( modDate ) ? cal.get( Calendar.DST_OFFSET ) : 0 ) ) );
        

        Vector v = m_handler.getRecentChanges( cal.getTime() );

        assertEquals( "wrong number of changes", 1, v.size() );
    }

    public void testPageInfo()
        throws Exception
    {
        String text = "Foobar.";
        String pageName = NAME1;

        m_engine.saveText( pageName, text );

        WikiPage directInfo = m_engine.getPage( NAME1 );

        Hashtable ht = m_handler.getPageInfo( NAME1 );

        assertEquals( "name", (String)ht.get( "name" ), NAME1 );
        
        Date d = (Date) ht.get( "lastModified" );

        Calendar cal = Calendar.getInstance();
        cal.setTime( d );

        System.out.println("Real: "+directInfo.getLastModified() );
        System.out.println("RPC:  "+d );

        // Offset the ZONE offset and DST offset away.  DST only
        // if we're actually in DST.
        cal.add( Calendar.MILLISECOND, 
                 (cal.get( Calendar.ZONE_OFFSET )+
                  (cal.getTimeZone().inDaylightTime( d ) ? cal.get( Calendar.DST_OFFSET ) : 0 ) ) );
        System.out.println("RPC2: "+cal.getTime() );

        assertEquals( "date", cal.getTime().getTime(), 
                      directInfo.getLastModified().getTime() );
    }

    /**
     *  Tests if listLinks() works with a single, non-existant local page.
     */
    public void testListLinks()
        throws Exception
    {
        String text = "[Foobar]";
        String pageName = NAME1;

        m_engine.saveText( pageName, text );

        Vector links = m_handler.listLinks( pageName );

        assertEquals( "link count", 1, links.size() );

        Hashtable linkinfo = (Hashtable) links.elementAt(0);

        assertEquals( "name", "Foobar", linkinfo.get("page") );
        assertEquals( "type", "local",  linkinfo.get("type") );
        assertEquals( "href", "Edit.jsp?page=Foobar", linkinfo.get("href") );
    }


    public void testListLinksWithAttachments()
        throws Exception
    {
        String text = "[Foobar] [Test/TestAtt.txt]";
        String pageName = NAME1;

        m_engine.saveText( pageName, text );

        Attachment att = new Attachment( NAME1, "TestAtt.txt" );
        att.setAuthor( "FirstPost" );
        m_engine.getAttachmentManager().storeAttachment( att, m_engine.makeAttachmentFile() );

        // Test.

        Vector links = m_handler.listLinks( pageName );

        assertEquals( "link count", 2, links.size() );

        Hashtable linkinfo = (Hashtable) links.elementAt(0);

        assertEquals( "edit name", "Foobar", linkinfo.get("page") );
        assertEquals( "edit type", "local",  linkinfo.get("type") );
        assertEquals( "edit href", "Edit.jsp?page=Foobar", linkinfo.get("href") );

        linkinfo = (Hashtable) links.elementAt(1);

        assertEquals( "att name", NAME1+"/TestAtt.txt", linkinfo.get("page") );
        assertEquals( "att type", "local", linkinfo.get("type") );
        assertEquals( "att href", "attach/"+NAME1+"/TestAtt.txt", linkinfo.get("href") );
    }

    /*
     * TODO: ENABLE
    public void testPermissions()
        throws Exception
    {
        String text ="Blaa. [{DENY view Guest}] [{ALLOW view NamedGuest}]";

        m_engine.saveText( NAME1, text );

        try
        {
            Vector links = m_handler.listLinks( NAME1 );
            fail("Didn't get an exception in listLinks()");
        }
        catch( XmlRpcException e ) {}

        try
        {
            Hashtable ht = m_handler.getPageInfo( NAME1 );
            fail("Didn't get an exception in getPageInfo()");
        }
        catch( XmlRpcException e ) {}
    }
*/
    
    public static Test suite()
    {
        return new TestSuite( RPCHandlerTest.class );
    }
}
