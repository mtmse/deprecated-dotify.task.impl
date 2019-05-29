package org.daisy.dotify.tasks.impl.identity;

import java.io.IOException;
import java.io.InputStream;

import org.daisy.dotify.common.xml.XMLInfo;
import org.daisy.dotify.common.xml.XMLTools;
import org.daisy.dotify.common.xml.XMLToolsException;
import org.daisy.streamline.api.identity.IdentificationFailedException;
import org.daisy.streamline.api.identity.Identifier;
import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.media.AnnotatedInputStream;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;
import org.daisy.streamline.api.media.DefaultAnnotatedInputStream;
import org.daisy.streamline.api.media.DefaultFileDetails;
import org.daisy.streamline.api.media.InputStreamSupplier;
import org.xml.sax.InputSource;

/**
 * Provides an identifier for xml files. This identifier will attach some additional
 * information about the root element of the file. These can be accessed using 
 * {@link AnnotatedFile#getProperties()}. The keys are included below.
 * 
 * In addition, the meta data regarding some xml formats that are relevant to this
 * bundle (namely dtbook, html, obfl and pef) are specified to a greater detail
 * with respect to format name and media type.
 * 
 * @author Joel Håkansson
 */
public class XmlIdentifier implements Identifier {
	/**
	 * Defines the property key for the root element namespace.
	 */
	public static final String XMLNS_KEY = "xmlns";
	/**
	 * Defines the property key for the root element local name.
	 */
	public static final String LOCAL_NAME_KEY = "local-name";
	/**
	 * Defines the property key for the root element attributes.
	 */
	public static final String ATTRIBUTES_KEY = "attributes";
	
	/**
	 * Defines the property key for the public identifier.
	 */
	public static final String PUBLIC_ID_KEY = "publicId";

	/**
	 * Defines the property key for the system identifier.
	 */
	public static final String SYSTEM_ID_KEY = "systemId";

	/**
	 * Creates a new xml identifier instance.
	 */
	public XmlIdentifier() {
		super();
	}

	@Override
	public AnnotatedFile identify(AnnotatedFile f) throws IdentificationFailedException {
		XMLInfo info = null;
		try {
			info = XMLTools.parseXML(f.getPath().toFile(), true);
		} catch (XMLToolsException e) {
			throw new IdentificationFailedException(e);
		}
		if (info==null) {
			throw new IdentificationFailedException("Not well-formed XML: " + f.getPath());
		} else {
			DefaultAnnotatedFile.Builder ret = new DefaultAnnotatedFile.Builder(f.getPath());
			boolean xmlProps = true;
			if ("http://www.daisy.org/z3986/2005/dtbook/".equals(info.getUri())) {
				ret.formatName("dtbook").extension("xml").mediaType("application/x-dtbook+xml");
			} else if ("http://www.w3.org/1999/xhtml".equals(info.getUri())) {
				ret.formatName("xhtml").extension("xhtml").mediaType("application/xhtml+xml");
			} else if ("http://www.daisy.org/ns/2011/obfl".equals(info.getUri())) {
				ret.formatName("obfl").extension("obfl").mediaType("application/x-obfl+xml");
			} else if ("http://www.daisy.org/ns/2008/pef".equals(info.getUri())) {
				ret.formatName("pef").extension("pef").mediaType("application/x-pef+xml");
			} else if ("html".equals(info.getLocalName())) {
				ret.formatName("html").extension("html").mediaType("text/html");
				xmlProps = false;
			} else {
				ret.formatName("xml").extension("xml").mediaType("application/xml");
			}
			if (xmlProps) {
				ret.property(XMLNS_KEY, info.getUri())
					.property(LOCAL_NAME_KEY, info.getLocalName())
					.property(ATTRIBUTES_KEY, info.getAttributes());
				if (info.getPublicId()!=null) {
					ret.property(PUBLIC_ID_KEY, info.getPublicId());
				}
				if (info.getSystemId()!=null) {
					ret.property(SYSTEM_ID_KEY, info.getSystemId());
				}
			}
			return ret.build();
		}
	}

	@Override
	public AnnotatedInputStream identify(InputStreamSupplier stream) throws IdentificationFailedException {
		XMLInfo info = null;
		try (InputStream is = stream.newInputStream()) {
			InputSource source = new InputSource(is);
			source.setSystemId(stream.getSystemId());
			info = XMLTools.parseXML(source, true);
		} catch (XMLToolsException e) {
			throw new IdentificationFailedException(e);
		} catch (IOException e) {
			// thrown by InputStream.close()
		}
		if (info==null) {
			throw new IdentificationFailedException("Not well-formed XML: " + stream.getSystemId());
		} else {
			DefaultFileDetails.Builder details = new DefaultFileDetails.Builder();
			boolean xmlProps = true;
			if ("http://www.daisy.org/z3986/2005/dtbook/".equals(info.getUri())) {
				details.formatName("dtbook").extension("xml").mediaType("application/x-dtbook+xml");
			} else if ("http://www.w3.org/1999/xhtml".equals(info.getUri())) {
				details.formatName("xhtml").extension("xhtml").mediaType("application/xhtml+xml");
			} else if ("http://www.daisy.org/ns/2011/obfl".equals(info.getUri())) {
				details.formatName("obfl").extension("obfl").mediaType("application/x-obfl+xml");
			} else if ("http://www.daisy.org/ns/2008/pef".equals(info.getUri())) {
				details.formatName("pef").extension("pef").mediaType("application/x-pef+xml");
			} else if ("html".equals(info.getLocalName())) {
				details.formatName("html").extension("html").mediaType("text/html");
				xmlProps = false;
			} else {
				details.formatName("xml").extension("xml").mediaType("application/xml");
			}
			if (xmlProps) {
				details.property(XMLNS_KEY, info.getUri())
					.property(LOCAL_NAME_KEY, info.getLocalName())
					.property(ATTRIBUTES_KEY, info.getAttributes());
				if (info.getPublicId()!=null) {
					details.property(PUBLIC_ID_KEY, info.getPublicId());
				}
				if (info.getSystemId()!=null) {
					details.property(SYSTEM_ID_KEY, info.getSystemId());
				}
			}
			return new DefaultAnnotatedInputStream.Builder(stream).details(details.build()).build();
		}
	}

}
