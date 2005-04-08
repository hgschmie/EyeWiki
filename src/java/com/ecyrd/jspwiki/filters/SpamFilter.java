/* 
   JSPWiki - a JSP-based WikiWiki clone.

   Copyright (C) 2001-2004 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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
package com.ecyrd.jspwiki.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiPage;

/**
 *  A regular expression-based spamfilter.
 *
 *  @since 2.1.112
 *  @author Janne Jalkanen
 */
public class SpamFilter
        extends BasicPageFilter
{
    private String m_forbiddenWordsPage = "SpamFilterWordList";
    private String m_errorPage          = "RejectedMessage";

    private static final String LISTVAR = "spamwords";
    private PatternMatcher m_matcher = new Perl5Matcher();
    private PatternCompiler m_compiler = new Perl5Compiler();

    private Collection m_spamPatterns = null;

    private Date m_lastRebuild = new Date( 0L );

    static Logger log = Logger.getLogger( SpamFilter.class );

    public static final String PROP_WORDLIST  = "wordlist";
    public static final String PROP_ERRORPAGE = "errorpage";

    public void initialize( Properties properties )
    {
        m_forbiddenWordsPage = properties.getProperty( PROP_WORDLIST, 
                m_forbiddenWordsPage );
        m_errorPage = properties.getProperty( PROP_ERRORPAGE, 
                m_errorPage );
    }

    private Collection parseWordList( WikiPage source, String list )
    {
        ArrayList compiledpatterns = new ArrayList();

        if( list != null )
        {
            StringTokenizer tok = new StringTokenizer( list, " \t\n" );

            while( tok.hasMoreTokens() )
            {
                String pattern = tok.nextToken();

                try
                {
                    compiledpatterns.add( m_compiler.compile( pattern ) );
                }
                catch( MalformedPatternException e )
                {
                    if (log.isDebugEnabled()) {
                        log.debug( "Malformed spam filter pattern "+pattern );
                    }
                
                    source.setAttribute("error", "Malformed spam filter pattern "+pattern);
                }
            }
        }

        return compiledpatterns;
    }


    public String preSave( WikiContext context, String content )
            throws RedirectException
    {
        WikiPage source = context.getEngine().getPage( m_forbiddenWordsPage );

        if( source != null )
        {
            if( m_spamPatterns == null || m_spamPatterns.isEmpty() || source.getLastModified().after(m_lastRebuild) )
            {
                m_lastRebuild = source.getLastModified();

                m_spamPatterns = parseWordList( source, 
                        (String)source.getAttribute( LISTVAR ) );

                log.info("Spam filter reloaded - recognizing "+m_spamPatterns.size()+" patterns from page "+m_forbiddenWordsPage);
            }
        }

        //
        //  If we have no spam patterns defined, or we're trying to save
        //  the page containing the patterns, just return.
        //
        if( m_spamPatterns == null || context.getPage().getName().equals( m_forbiddenWordsPage ) )
        {
            return content;
        }

        for( Iterator i = m_spamPatterns.iterator(); i.hasNext(); )
        {
            Pattern p = (Pattern) i.next();

            if (log.isDebugEnabled()) {
                log.debug("Attempting to match page contents with "+p.getPattern());
            }

            if( m_matcher.contains( content, p ) )
            {
                //
                //  Spam filter has a match.
                //

                throw new RedirectException( "Content matches the spam filter '"+p.getPattern()+"'", 
                        context.getURL(WikiContext.VIEW,m_errorPage) );
            }
        }

        return content;
    }
}
