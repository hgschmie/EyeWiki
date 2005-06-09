package de.softwareforge.eyewiki.filters;

import org.apache.commons.configuration.Configuration;

import de.softwareforge.eyewiki.filters.BasicPageFilter;
import de.softwareforge.eyewiki.filters.PageFilter;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TestFilter
        extends BasicPageFilter
{
    /** DOCUMENT ME! */
    private final Configuration conf;

    /**
     * DOCUMENT ME!
     *
     * @param props DOCUMENT ME!
     */
    public TestFilter(final Configuration conf)
    {
        super(conf);
        this.conf = conf;
    }
    
    public Configuration getConfiguration()
    {
        return conf;
    }
    
    public boolean isVisible()
    {
        return true;
    }
    
    public int getPriority()
    {
        return PageFilter.NORMAL_PRIORITY;
    }

}
