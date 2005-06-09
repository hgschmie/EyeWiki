/*
    JSPWiki - a JSP-based WikiWiki clone.

    Copyright (C) 2001 Janne Jalkanen (Janne.Jalkanen@iki.fi)

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

package de.softwareforge.eyewiki.variable;

import org.picocontainer.Startable;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.manager.VariableManager;

public class EngineVariables
        implements Startable
{
    private final WikiEngine engine;
    private final VariableManager variableManager;

    public EngineVariables(final VariableManager variableManager, final WikiEngine engine)
    {
        this.engine = engine;
        this.variableManager = variableManager;
    }

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

    public synchronized void stop()
    {
        // GNDN
    }

    private class ApplicationName
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String variableName)
        {
            return engine.getApplicationName();
        }
    }

    private class ContentEncoding
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String variableName)
        {
            return engine.getContentEncoding();
        }
    }

    private class TotalPages
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String variableName)
        {
            return Integer.toString(engine.getPageCount());
        }
    }

    private class PageProvider
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String variableName)
        {
            return engine.getCurrentProvider();
        }
    }

    private class PageProviderDescription
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String variableName)
        {
            return engine.getCurrentProviderInfo();
        }
    }

    private class BaseURL
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String variableName)
        {
            return engine.getBaseURL();
        }
    }

}

