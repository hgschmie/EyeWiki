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

