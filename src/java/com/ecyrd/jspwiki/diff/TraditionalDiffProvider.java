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
package com.ecyrd.jspwiki.diff;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.jrcs.diff.AddDelta;
import org.apache.commons.jrcs.diff.ChangeDelta;
import org.apache.commons.jrcs.diff.Chunk;
import org.apache.commons.jrcs.diff.DeleteDelta;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.DifferentiationFailedException;
import org.apache.commons.jrcs.diff.Revision;
import org.apache.commons.jrcs.diff.RevisionVisitor;
import org.apache.commons.jrcs.diff.myers.MyersDiff;
import org.apache.log4j.Logger;

import com.ecyrd.jspwiki.WikiConstants;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.util.TextUtil;


/**
 * This is the JSPWiki 'traditional' diff.
 *
 * @author Janne Jalkanen
 * @author Erik Bunn
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 */
public class TraditionalDiffProvider
        implements DiffProvider
{
    /** DOCUMENT ME! */
    protected final Logger log = Logger.getLogger(this.getClass());

    /** DOCUMENT ME! */
    protected String diffAdd = "<tr><td class=\"" + WikiConstants.CSS_DIFF_ADD + "\">";

    /** DOCUMENT ME! */
    protected String diffRem = "<tr><td class=\"" + WikiConstants.CSS_DIFF_REM + "\">";

    /** DOCUMENT ME! */
    protected String diffComment = "<tr><td class=\"" + WikiConstants.CSS_DIFF + "\">";

    /** DOCUMENT ME! */
    protected String diffClose = "</td></tr>\n";

    /** DOCUMENT ME! */
    protected String diffPrefix =
        "<table class=\""+ WikiConstants.CSS_DIFF_BLOCK + "\">\n";

    /** DOCUMENT ME! */
    protected String diffPostfix = "</table>\n";

    /**
     * @see com.ecyrd.jspwiki.WikiProvider#getProviderInfo()
     */
    public String getProviderInfo()
    {
        return this.getClass().getName();
    }

    /**
     * @see com.ecyrd.jspwiki.WikiProvider#initialize(com.ecyrd.jspwiki.WikiEngine,
     *      java.util.Properties)
     */
    public TraditionalDiffProvider(WikiEngine engine, Configuration conf)
    {
    }

    /**
     * Makes a diff using the BMSI utility package. We use our own diff printer, which makes things
     * easier.
     *
     * @param p1 DOCUMENT ME!
     * @param p2 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String makeDiff(String p1, String p2)
    {
        String diffResult = "";

        try
        {
            String [] first = Diff.stringToArray(TextUtil.replaceEntities(p1));
            String [] second = Diff.stringToArray(TextUtil.replaceEntities(p2));
            Revision rev = Diff.diff(first, second, new MyersDiff());

            if ((rev == null) || (rev.size() == 0))
            {
                // No difference
                return "";
            }

            StringBuffer ret = new StringBuffer(rev.size() * 20); // Guessing how big it will become...

            ret.append(diffPrefix);
            rev.accept(new RevisionPrint(ret));
            ret.append(diffPostfix);

            return ret.toString();
        }
        catch (DifferentiationFailedException e)
        {
            diffResult = "makeDiff failed with DifferentiationFailedException";
            log.error(diffResult, e);
        }

        return diffResult;
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    private final class RevisionPrint
            implements RevisionVisitor
    {
        /** DOCUMENT ME! */
        private StringBuffer m_result = null;

        /**
         * Creates a new RevisionPrint object.
         *
         * @param sb DOCUMENT ME!
         */
        private RevisionPrint(StringBuffer sb)
        {
            m_result = sb;
        }

        /**
         * DOCUMENT ME!
         *
         * @param rev DOCUMENT ME!
         */
        public void visit(Revision rev)
        {
            // GNDN (Goes nowhere, does nothing)
        }

        /**
         * DOCUMENT ME!
         *
         * @param delta DOCUMENT ME!
         */
        public void visit(AddDelta delta)
        {
            Chunk changed = delta.getRevised();
            print(changed, " added ");
            changed.toString(m_result, diffAdd, diffClose);
        }

        /**
         * DOCUMENT ME!
         *
         * @param delta DOCUMENT ME!
         */
        public void visit(ChangeDelta delta)
        {
            Chunk changed = delta.getOriginal();
            print(changed, " changed ");
            changed.toString(m_result, diffRem, diffClose);
            delta.getRevised().toString(m_result, diffAdd, diffClose);
        }

        /**
         * DOCUMENT ME!
         *
         * @param delta DOCUMENT ME!
         */
        public void visit(DeleteDelta delta)
        {
            Chunk changed = delta.getOriginal();
            print(changed, " removed ");
            changed.toString(m_result, diffRem, diffClose);
        }

        private void print(Chunk changed, String type)
        {
            m_result.append(diffComment);
            m_result.append("At line ");
            m_result.append(changed.first() + 1);
            m_result.append(type);
            m_result.append(changed.size());
            m_result.append(" line");
            m_result.append((changed.size() == 1)
                ? "."
                : "s.");
            m_result.append(diffClose);
        }
    }
}
