package de.softwareforge.eyewiki.tags;

import java.io.IOException;

import de.softwareforge.eyewiki.providers.ProviderException;


/**
 * Includes the body in case the set page does exist.
 *
 * @author Janne Jalkanen
 *
 * @since 2.0
 */

// FIXME: Logically, this should probably be the master one, then
//        NoSuchPageTag should be the one that derives from this.
public class PageExistsTag
        extends NoSuchPageTag
{
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ProviderException DOCUMENT ME!
     */
    public final int doWikiStartTag()
            throws IOException, ProviderException
    {
        return (super.doWikiStartTag() == SKIP_BODY)
        ? EVAL_BODY_INCLUDE
        : SKIP_BODY;
    }
}
