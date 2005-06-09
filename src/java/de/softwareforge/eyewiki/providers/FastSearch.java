package de.softwareforge.eyewiki.providers;

/**
 * If a provider implements this interface, then CachingProvider will never attempt to search on
 * its own; it will always pass any searches through to the actual provider.
 *
 * @author Janne Jalkanen
 *
 * @since 2.1.57
 */
public interface FastSearch
{
}
