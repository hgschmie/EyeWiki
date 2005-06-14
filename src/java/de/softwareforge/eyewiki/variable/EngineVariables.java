package de.softwareforge.eyewiki.variable;

import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.manager.VariableManager;

/*
 * ========================================================================
 *
 * eyeWiki - a WikiWiki clone written in Java
 *
 * ========================================================================
 *
 * Copyright (C) 2005 Henning Schmiedehausen <henning@software-forge.com>
 *
 * based on
 *
 * JSPWiki - a JSP-based WikiWiki clone.
 * Copyright (C) 2002-2005 Janne Jalkanen (Janne.Jalkanen@iki.fi)
 *
 * ========================================================================
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * ========================================================================
 */
import org.picocontainer.Startable;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Id$
 */
public class EngineVariables
        implements Startable
{
    /** DOCUMENT ME! */
    private final WikiEngine engine;

    /** DOCUMENT ME! */
    private final VariableManager variableManager;

    /**
     * Creates a new EngineVariables object.
     *
     * @param variableManager DOCUMENT ME!
     * @param engine DOCUMENT ME!
     */
    public EngineVariables(final VariableManager variableManager, final WikiEngine engine)
    {
        this.engine = engine;
        this.variableManager = variableManager;
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void start()
    {
        // Now is this cool or what?
        variableManager.registerVariable("applicationname", new ApplicationName());
        variableManager.registerVariable("encoding", new ContentEncoding());
        variableManager.registerVariable("totalpages", new TotalPages());
        variableManager.registerVariable("pageprovider", new PageProvider());
        variableManager.registerVariable("pageproviderdescription", new PageProviderDescription());
        variableManager.registerVariable("baseurl", new BaseURL());
    }

    /**
     * DOCUMENT ME!
     */
    public synchronized void stop()
    {
        // GNDN
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Id$
     */
    private class ApplicationName
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param variableName DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getValue(WikiContext context, String variableName)
        {
            return engine.getApplicationName();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Id$
     */
    private class ContentEncoding
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param variableName DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getValue(WikiContext context, String variableName)
        {
            return engine.getContentEncoding();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Id$
     */
    private class TotalPages
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param variableName DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getValue(WikiContext context, String variableName)
        {
            return Integer.toString(engine.getPageCount());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Id$
     */
    private class PageProvider
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param variableName DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getValue(WikiContext context, String variableName)
        {
            return engine.getCurrentProvider();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Id$
     */
    private class PageProviderDescription
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param variableName DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getValue(WikiContext context, String variableName)
        {
            return engine.getCurrentProviderInfo();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Id$
     */
    private class BaseURL
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        /**
         * DOCUMENT ME!
         *
         * @param context DOCUMENT ME!
         * @param variableName DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getValue(WikiContext context, String variableName)
        {
            return engine.getBaseURL();
        }
    }
}
