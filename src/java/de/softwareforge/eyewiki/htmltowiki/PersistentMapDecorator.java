package de.softwareforge.eyewiki.htmltowiki;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * Adds the load / save - functionality known from the Properties - class to any Map
 * implementation.
 *
 * @author <a href="mailto:sbaltes@gmx.com">Sebastian Baltes</a>
 */
public class PersistentMapDecorator
        extends Properties
{
    /**
     * DOCUMENT ME!
     */
    private Map delegate;

    /**
     * Creates a new PersistentMapDecorator object.
     *
     * @param delegate DOCUMENT ME!
     */
    public PersistentMapDecorator(Map delegate)
    {
        this.delegate = delegate;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear()
    {
        delegate.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean containsKey(Object key)
    {
        return delegate.containsKey(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean containsValue(Object value)
    {
        return delegate.containsValue(value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Set entrySet()
    {
        return delegate.entrySet();
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(Object obj)
    {
        return delegate.equals(obj);
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object get(Object key)
    {
        return delegate.get(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int hashCode()
    {
        return delegate.hashCode();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Set keySet()
    {
        return delegate.keySet();
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg0 DOCUMENT ME!
     * @param arg1 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object put(Object arg0, Object arg1)
    {
        return delegate.put(arg0, arg1);
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg0 DOCUMENT ME!
     */
    public void putAll(Map arg0)
    {
        delegate.putAll(arg0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object remove(Object key)
    {
        return delegate.remove(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int size()
    {
        return delegate.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return delegate.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Collection values()
    {
        return delegate.values();
    }
}
