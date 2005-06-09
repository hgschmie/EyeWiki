package de.softwareforge.eyewiki.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.jrcs.diff.AddDelta;
import org.apache.commons.jrcs.diff.ChangeDelta;
import org.apache.commons.jrcs.diff.Chunk;
import org.apache.commons.jrcs.diff.DeleteDelta;
import org.apache.commons.jrcs.diff.Delta;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.DifferentiationFailedException;
import org.apache.commons.jrcs.diff.Revision;
import org.apache.commons.jrcs.diff.RevisionVisitor;
import org.apache.commons.jrcs.diff.myers.MyersDiff;
import org.apache.log4j.Logger;


import de.softwareforge.eyewiki.WikiConstants;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.util.TextUtil;


/**
 * A seriously better diff provider, which highlights changes word-by-word using CSS. Suggested by
 * John Volkar.
 *
 * @author John Volkar
 * @author Janne Jalkanen
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 */
public class ContextualDiffProvider
        implements DiffProvider
{
    /** DOCUMENT ME! */
    private static final Logger log = Logger.getLogger(ContextualDiffProvider.class);

    //TODO all of these publics can become eyewiki.properties entries...
    //TODO span title= can be used to get hover info...

    /** DOCUMENT ME! */
    protected boolean m_emitChangeNextPreviousHyperlinks = true;

    //Don't use spans here the deletion and insertions are nested in this...

    /** DOCUMENT ME! */
    protected String m_changeStartHtml = ""; //This could be a image '>' for a start marker

    /** DOCUMENT ME! */
    protected String m_changeEndHtml = ""; //and an image for an end '<' marker

    /** DOCUMENT ME! */
    protected String m_diffStart = "<div class=\""+ WikiConstants.CSS_DIFF_BLOCK + "\">";

    /** DOCUMENT ME! */
    protected String m_diffEnd = "</div>";

    /** DOCUMENT ME! */
    protected String m_insertionStartHtml = "<span class=\"" + WikiConstants.CSS_DIFF_ADD + "\">";

    /** DOCUMENT ME! */
    protected String m_insertionEndHtml = "</span>";

    /** DOCUMENT ME! */
    protected String m_deletionStartHtml = "<span class=\"" + WikiConstants.CSS_DIFF_REM + "\">";

    /** DOCUMENT ME! */
    protected String m_deletionEndHtml = "</span>";

    /** DOCUMENT ME! */
    protected String m_anchorPreIndex = "<a class=\"" + WikiConstants.CSS_DIFF_ANCHOR + "\" name=\"change-";

    /** DOCUMENT ME! */
    protected String m_anchorPostIndex = "\" />";

    /** DOCUMENT ME! */
    protected String m_backPreIndex = "<a class=\"" + WikiConstants.CSS_LINK_DIFF + "\" href=\"#change-";

    /** DOCUMENT ME! */
    protected String m_backPostIndex = "\">&lt;&lt;</a>";

    /** DOCUMENT ME! */
    protected String m_forwardPreIndex = "<a class=\"" + WikiConstants.CSS_LINK_DIFF + "\" href=\"#change-";

    /** DOCUMENT ME! */
    protected String m_forwardPostIndex = "\">&gt;&gt;</a>";

    /**
     * Creates a new ContextualDiffProvider object.
     */
    public ContextualDiffProvider(WikiEngine engine, Configuration conf)
    {
    }

    /**
     * @see de.softwareforge.eyewiki.WikiProvider#getProviderInfo()
     */
    public String getProviderInfo()
    {
        return this.getClass().getName();
    }

    /**
     * Do a colored diff of the two regions. This. is. serious. fun. ;-)
     *
     * @see de.softwareforge.eyewiki.diff.DiffProvider#makeDiff(java.lang.String, java.lang.String)
     */
    public synchronized String makeDiff(String wikiOld, String wikiNew)
    {
        //Sequencing handles lineterminator to <br /> and every-other consequtive space to a &nbsp;
        String [] alpha = sequence(TextUtil.replaceEntities(wikiOld));
        String [] beta = sequence(TextUtil.replaceEntities(wikiNew));

        Revision rev = null;

        try
        {
            rev = Diff.diff(alpha, beta, new MyersDiff());
        }
        catch (DifferentiationFailedException dfe)
        {
            log.error("Diff generation failed", dfe);

            return "Error while creating version diff.";
        }

        int revSize = rev.size();

        StringBuffer sb = new StringBuffer(revSize * 20); // Guessing how big it will become...

        sb.append(m_diffStart);

        // The MyersDiff is a bit dumb by converting a single line multi-word diff into a series
        // of Changes. The ChangeMerger pulls them together again...
        ChangeMerger cm = new ChangeMerger(sb, alpha, revSize);

        rev.accept(cm);

        cm.shutdown();

        sb.append(m_diffEnd);

        return sb.toString();
    }

    /**
     * Take the string and create an array from it, split it first on newlines, making sure to
     * preserve the newlines in the elements, split each resulting element on spaces, preserving
     * the spaces. All this preseving of newlines and spaces is so the wikitext when diffed will
     * have fidelity to it's original form.  As a side affect we see edits of purely whilespace.
     *
     * @param wikiText DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String [] sequence(String wikiText)
    {
        String [] linesArray = Diff.stringToArray(wikiText);

        List list = new ArrayList();

        for (int i = 0; i < linesArray.length; i++)
        {
            String line = linesArray[i];

            // StringTokenizer might be discouraged but it still is perfect here...
            for (StringTokenizer st = new StringTokenizer(line); st.hasMoreTokens();)
            {
                list.add(st.nextToken());

                if (st.hasMoreTokens())
                {
                    list.add(" ");
                }
            }

            list.add("<br />"); // Line Break
        }

        return (String []) list.toArray(new String[0]);
    }

    /**
     * This helper class does the housekeeping for merging our various changes down and also makes
     * sure that the whole change process is threadsafe by encapsulating all necessary variables.
     */
    private final class ChangeMerger
            implements RevisionVisitor
    {
        /** DOCUMENT ME! */
        private StringBuffer sb = null;

        /** Keeping score of the original lines to process */
        private int max = -1;

        /** DOCUMENT ME! */
        private int index = 0;

        /** Index of the next line to be copied into the output. */
        private int firstLine = 0;

        /** Link Anchor counter */
        private int count = 1;

        /** State Machine Mode */
        private int mode = -1; /* -1: Unset, 0: Add, 1: Del, 2: Change mode */

        /** Buffer to coalesce the changes together */
        private StringBuffer origBuf = null;

        /** DOCUMENT ME! */
        private StringBuffer newBuf = null;

        /** Reference to the source string array */
        private String [] origStrings = null;

        /**
         * Creates a new ChangeMerger object.
         *
         * @param sb DOCUMENT ME!
         * @param origStrings DOCUMENT ME!
         * @param max DOCUMENT ME!
         */
        private ChangeMerger(final StringBuffer sb, final String [] origStrings, final int max)
        {
            this.sb = sb;
            this.origStrings = origStrings;
            this.max = max;

            this.origBuf = new StringBuffer();
            this.newBuf = new StringBuffer();
        }

        private void updateState(Delta delta)
        {
            index++;

            Chunk orig = delta.getOriginal();

            if (orig.first() > firstLine)
            {
                // We "skip" some lines in the output.
                // So flush out the last Change, if one exists.
                flush();

                for (int j = firstLine; j < orig.first(); j++)
                {
                    sb.append(origStrings[j]);
                }
            }

            firstLine = orig.last() + 1;
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
            updateState(delta);

            // We have run Deletes up to now. Flush them out.
            if (mode == 1)
            {
                flush();
                mode = -1;
            }

            // We are in "neutral mode". Start a new Change
            if (mode == -1)
            {
                mode = 0;
            }

            // We are in "add mode".
            if ((mode == 0) || (mode == 2))
            {
                addNew(delta.getRevised());
                mode = 1;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param delta DOCUMENT ME!
         */
        public void visit(ChangeDelta delta)
        {
            updateState(delta);

            // We are in "neutral mode". A Change might be merged with an add or delete.
            if (mode == -1)
            {
                mode = 2;
            }

            // Add the Changes to the buffers.
            addOrig(delta.getOriginal());
            addNew(delta.getRevised());
        }

        /**
         * DOCUMENT ME!
         *
         * @param delta DOCUMENT ME!
         */
        public void visit(DeleteDelta delta)
        {
            updateState(delta);

            // We have run Adds up to now. Flush them out.
            if (mode == 0)
            {
                flush();
                mode = -1;
            }

            // We are in "neutral mode". Start a new Change
            if (mode == -1)
            {
                mode = 1;
            }

            // We are in "delete mode".
            if ((mode == 1) || (mode == 2))
            {
                addOrig(delta.getOriginal());
                mode = 1;
            }
        }

        /**
         * DOCUMENT ME!
         */
        public void shutdown()
        {
            index = max + 1; // Make sure that no hyperlink gets created
            flush();

            if (firstLine < origStrings.length)
            {
                for (int j = firstLine; j < origStrings.length; j++)
                {
                    sb.append(origStrings[j]);
                }
            }
        }

        private void addOrig(Chunk chunk)
        {
            if (chunk != null)
            {
                chunk.toString(origBuf);
            }
        }

        private void addNew(Chunk chunk)
        {
            if (chunk != null)
            {
                chunk.toString(newBuf);
            }
        }

        private void flush()
        {
            if ((newBuf.length() + origBuf.length()) > 0)
            {
                // This is the span element which encapsulates anchor and the change itself
                sb.append(m_changeStartHtml);

                // Do we want to have a "back link"?
                if (m_emitChangeNextPreviousHyperlinks && (count > 1))
                {
                    sb.append(m_backPreIndex);
                    sb.append(count - 1);
                    sb.append(m_backPostIndex);
                }

                // An anchor for the change.
                sb.append(m_anchorPreIndex);
                sb.append(count++);
                sb.append(m_anchorPostIndex);

                // ... has been added
                if (newBuf.length() > 0)
                {
                    sb.append(m_insertionStartHtml);
                    sb.append(newBuf);
                    sb.append(m_insertionEndHtml);
                }

                sb.append(" ");

                // .. has been removed
                if (origBuf.length() > 0)
                {
                    sb.append(m_deletionStartHtml);
                    sb.append(origBuf);
                    sb.append(m_deletionEndHtml);
                }

                // Do we want a "forward" link?
                if (m_emitChangeNextPreviousHyperlinks && (index <= max))
                {
                    sb.append(m_forwardPreIndex);
                    sb.append(count); // Has already been incremented.
                    sb.append(m_forwardPostIndex);
                }

                sb.append(m_changeEndHtml);
                sb.append("\n");

                // Nuke the buffers.
                origBuf = new StringBuffer();
                newBuf = new StringBuffer();
            }

            // After a flush, everything is reset.
            mode = -1;
        }
    }
}
