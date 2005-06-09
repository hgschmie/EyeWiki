package de.softwareforge.eyewiki;

/**
 * Defines an interface for transforming strings within a Wiki context.
 *
 * @since 1.6.4
 */
public interface StringTransmutator
{
    /**
     * Returns a changed String, suitable for Wiki context.
     *
     * @param context DOCUMENT ME!
     * @param source DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    String mutate(WikiContext context, String source);
}
