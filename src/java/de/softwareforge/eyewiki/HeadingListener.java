package de.softwareforge.eyewiki;

/**
 * Provides a listener interface for headings
 */
public interface HeadingListener
{
    /**
     * Is called whenever a heading is encountered in the stream.
     *
     * @param context DOCUMENT ME!
     * @param hd DOCUMENT ME!
     */
    void headingAdded(WikiContext context, TranslatorReader.Heading hd);
}
