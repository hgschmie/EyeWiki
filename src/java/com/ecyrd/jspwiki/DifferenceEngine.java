/* 
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001-2002 Janne Jalkanen (Janne.Jalkanen@iki.fi)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2.1 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.ecyrd.jspwiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.jrcs.diff.AddDelta;
import org.apache.commons.jrcs.diff.ChangeDelta;
import org.apache.commons.jrcs.diff.Chunk;
import org.apache.commons.jrcs.diff.DeleteDelta;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.DifferentiationFailedException;
import org.apache.commons.jrcs.diff.Revision;
import org.apache.commons.jrcs.diff.RevisionVisitor;
import org.apache.commons.jrcs.diff.myers.MyersDiff;
import org.apache.log4j.Category;

// import org.suigeneris.diff.*;

/**
 *  Provides access to making a 'diff' between two Strings.
 *  Can be commanded to use a diff program or to use an internal diff.
 *
 *  @author Janne Jalkanen
 *  @author Erik Bunn
 *  @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 */
public class DifferenceEngine
{
    private static final Category   log = Category.getInstance(DifferenceEngine.class);

    private static final char   DIFF_ADDED_SYMBOL      = '+';
    private static final char   DIFF_REMOVED_SYMBOL    = '-';
    private static final String CSS_DIFF_ADDED       = "<tr><td bgcolor=\"#99FF99\" class=\"diffadd\">";
    private static final String CSS_DIFF_REMOVED     = "<tr><td bgcolor=\"#FF9933\" class=\"diffrem\">";
    private static final String CSS_DIFF_UNCHANGED   = "<tr><td class=\"diff\">";
    private static final String CSS_DIFF_CLOSE       = "</td></tr>";

    /** Default diff command */
    private String         m_diffCommand     = null; 

    private String         m_encoding;

    private boolean        m_useInternalDiff = true;

    /**
     *  Creates a new DifferenceEngine.
     *
     *  @param props The contents of jspwiki.properties
     *  @param encoding The character encoding used for making the diff.
     */
    public DifferenceEngine( Properties props, String encoding )
    {
        m_diffCommand = props.getProperty(
                WikiProperties.PROP_DIFFCOMMAND,
                WikiProperties.PROP_DIFFCOMMAND_DEFAULT);
        
        m_useInternalDiff = (m_diffCommand == null);

        m_encoding    = encoding;
    }

    private String getContentEncoding()
    {
        return m_encoding;
    }

    /**
     *  Returns a raw, text format diff of its arguments.  This diff can then
     *  be fed to the <TT>colorizeDiff()</TT>, below.
     *
     *  @see #colorizeDiff
     */
    public String makeDiff( String p1, String p2 )
    {
        if( m_useInternalDiff )
        {
            return makeDiffWithJRCS( p1, p2 );
        }
        else
        {
            return makeDiffWithProgram( p1, p2 );
        }
    }

    /**
     *  Makes a diff using the Apache JRCS diff
     *
     *  We use our own diff printer, which makes things
     *  easier.
     */
    private String makeDiffWithJRCS( String p1, String p2 )        
    {
        try
        {
            String[] first  = Diff.stringToArray(p1);
            String[] second = Diff.stringToArray(p2);
            
            Revision rev = Diff.diff(first, second, new MyersDiff());

            if( rev == null || rev.size() == 0)
            {
                // No differences.
                return "";
            }
            
            StringBuffer ret = new StringBuffer();
            rev.accept(new RevisionPrint(ret));
            return ret.toString();

        }
        catch (DifferentiationFailedException de)
        {
            log.error("Diff failed", de);
        }

        return null;
    }

    private class RevisionPrint
            implements RevisionVisitor
    {

        private StringBuffer sb = null;

        private RevisionPrint(StringBuffer sb)
        {
            this.sb = sb;
        }

        public void visit(Revision rev)
        {
            // GNDN
        }

        public void visit(AddDelta delta)
        {
            Chunk changed = delta.getRevised();
            print(changed, " added ");
            changed.toString(sb, "+ ", Diff.NL);
        }

        public void visit(ChangeDelta delta)
        {
            Chunk changed = delta.getOriginal();
            print(changed, " changed ");
            changed.toString(sb, "- ", Diff.NL);

            delta.getRevised().toString(sb, "+ ", Diff.NL);
        }

        public void visit(DeleteDelta delta)
        {
            Chunk changed = delta.getOriginal();
            print(changed, " removed ");
            changed.toString(sb, "- ", Diff.NL);
        }

        private void print(Chunk changed, String type)
        {
            sb.append("\nAt line ");
            sb.append(changed.first() + 1);
            sb.append(type);
            sb.append(changed.size());
            sb.append(" line");
            sb.append((changed.size() == 1) ? "." : "s.");
            sb.append("\n");
        }

    }

    /**
     *  Makes the diff by calling "diff" program.
     */
    private String makeDiffWithProgram( String p1, String p2 )
    {
        File f1 = null, f2 = null;
        String diff = null;

        try
        {
            f1 = FileUtil.newTmpFile( p1, getContentEncoding() );
            f2 = FileUtil.newTmpFile( p2, getContentEncoding() );

            String cmd = TextUtil.replaceString( m_diffCommand,
                                                 "%s1",
                                                 f1.getPath() );
            cmd = TextUtil.replaceString( cmd,
                                          "%s2",
                                          f2.getPath() );

            String output = FileUtil.runSimpleCommand( cmd, f1.getParent() );

            // FIXME: Should this rely on the system default encoding?
            diff = new String(output.getBytes("ISO-8859-1"),
                              getContentEncoding() );
        }
        catch( IOException e )
        {
            log.error("Failed to do file diff",e);
        }
        catch( InterruptedException e )
        {
            log.error("Interrupted",e);
        }
        finally
        {
            if( f1 != null ) f1.delete();
            if( f2 != null ) f2.delete();
        }

        return diff;
    }

    /**
     * Goes through output provided by a diff command and inserts
     * HTML tags to make the result more legible.
     * Currently colors lines starting with a + green,
     * those starting with - reddish (hm, got to think of
     * color blindness here...).
     */
    public String colorizeDiff( String diffText )
        throws IOException
    {
        String line = null;
        String start = null;
        String stop = null;

        if( diffText == null )
        {
            return "Invalid diff - probably something wrong with server setup.";
        }

        BufferedReader in = new BufferedReader( new StringReader( diffText ) );
        StringBuffer out = new StringBuffer();

        out.append("<table class=\"diff\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
        while( ( line = in.readLine() ) != null )
        {
            stop  = CSS_DIFF_CLOSE;

            if( line.length() > 0 )
            {
                switch( line.charAt( 0 ) )
                {
                  case DIFF_ADDED_SYMBOL:
                    start = CSS_DIFF_ADDED;
                    break;
                  case DIFF_REMOVED_SYMBOL:
                    start = CSS_DIFF_REMOVED;
                    break;
                  default:
                    start = CSS_DIFF_UNCHANGED;
                }
            }
            else
            {
                start = CSS_DIFF_UNCHANGED;
            }
            
            out.append( start );
            out.append( line.trim() );
            out.append( stop + "\n" );

        }
        out.append("</table>\n");
        return( out.toString() );
    }

}
