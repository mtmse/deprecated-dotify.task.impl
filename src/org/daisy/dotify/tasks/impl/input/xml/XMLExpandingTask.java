package org.daisy.dotify.tasks.impl.input.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.common.io.ResourceLocator;
import org.daisy.dotify.common.io.ResourceLocatorException;
import org.daisy.dotify.tasks.impl.identity.XmlIdentifier;
import org.daisy.dotify.tasks.impl.input.ValidatorTask;
import org.daisy.dotify.tasks.tools.XsltTask;
import org.daisy.streamline.api.identity.IdentificationFailedException;
import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;
import org.daisy.streamline.api.tasks.ExpandingTask;
import org.daisy.streamline.api.tasks.InternalTask;
import org.daisy.streamline.api.tasks.InternalTaskException;

class XMLExpandingTask extends ExpandingTask {
	private static final Logger logger = Logger.getLogger(XMLExpandingTask.class.getCanonicalName());
	private static final String TEMPLATES_PATH = "templates/";
	private final ResourceLocator localLocator;
	private final ResourceLocator commonLocator;
	private final String template;
	private final Map<String, Object> xsltParams;

	XMLExpandingTask(String template, Map<String, Object> xsltParams, ResourceLocator localLocator, ResourceLocator commonLocator) {
		super("XML Tasks Bundle");
		this.localLocator = localLocator;
		this.commonLocator = commonLocator;
		this.template = template;
		this.xsltParams = xsltParams;
	}

	@Override
	public List<InternalTask> resolve(AnnotatedFile input) throws InternalTaskException {
		try {
			if (!input.getProperties().containsKey(XmlIdentifier.LOCAL_NAME_KEY) || !input.getProperties().containsKey(XmlIdentifier.XMLNS_KEY)) {
				 input = new XmlIdentifier().identify(input);
			}
			String rootNS = String.valueOf(input.getProperties().get(XmlIdentifier.XMLNS_KEY));
			String rootElement = String.valueOf(input.getProperties().get(XmlIdentifier.LOCAL_NAME_KEY));
			
			return getConfiguration(rootElement, rootNS, template, xsltParams, localLocator, commonLocator);
		} catch (IdentificationFailedException e) {
			throw new InternalTaskException("Failed to read input as xml", e);
		}
	}
	
	private static List<InternalTask> getConfiguration(String rootElement, String rootNS, String template, Map<String, Object> xsltParams, ResourceLocator localLocator, ResourceLocator commonLocator) throws InternalTaskException {
		String inputformat = DefaultInputUrlResourceLocator.getInstance().getConfigFileName(rootElement, rootNS);
		if (inputformat !=null && "".equals(inputformat)) {
			return new ArrayList<>();
		}
		String xmlformat = "xml.properties";
		String basePath = TEMPLATES_PATH + template + "/";
		if (inputformat!=null) {
			try {
				return readConfiguration(rootElement, localLocator, basePath + inputformat, xsltParams);
			} catch (ResourceLocatorException e) {
				logger.fine("Cannot find localized URL " + basePath + inputformat);
			}
		}
		try {
			return readConfiguration(rootElement, localLocator, basePath + xmlformat, xsltParams);
		} catch (ResourceLocatorException e) {
			logger.fine("Cannot find localized URL " + basePath + xmlformat);
		}
		if (inputformat!=null) {
			try {
				return readConfiguration(rootElement, commonLocator, basePath + inputformat, xsltParams);
			} catch (ResourceLocatorException e) {
				logger.fine("Cannot find common URL " + basePath + inputformat);
			}
		}
		try {
			return readConfiguration(rootElement, commonLocator, basePath + xmlformat, xsltParams);
		} catch (ResourceLocatorException e) {
			logger.fine("Cannot find common URL " + basePath + xmlformat);
		}
		throw new InternalTaskException("Unable to open a configuration stream for the format.");
	}
	
	private static List<InternalTask> readConfiguration(String type, ResourceLocator locator, String path, Map<String, Object> xsltParams) throws InternalTaskException, ResourceLocatorException {
		URL t = locator.getResource(path);
		List<InternalTask> setup = new ArrayList<>();				
		try {
			Properties pa = new Properties();
			try {
				logger.fine("Opening stream: " + t.getFile());					
				pa.loadFromXML(t.openStream());
			} catch (IOException e) {
				logger.log(Level.FINE, "Cannot open stream: " + t.getFile(), e);
				throw new ResourceLocatorException("Cannot open stream");
			}
			addValidationTask(type, removeSchemas(pa, "validation"), setup, locator);
			addXsltTask(type, removeSchemas(pa, "transformation"), setup, locator, xsltParams); 
			for (Object key : pa.keySet()) {
				logger.info("Unrecognized key: " + key);							
			}
		} catch (IOException e) {
			throw new InternalTaskException("Unable to open settings file.", e);
		}
		
		return setup;
	}

	@Override
	@Deprecated
	public List<InternalTask> resolve(File input) throws InternalTaskException {
		return resolve(new DefaultAnnotatedFile.Builder(input).build());
	}
	
	private static void addValidationTask(String type, String[] schemas, List<InternalTask> setup, ResourceLocator locator) throws ResourceLocatorException {
		if (schemas!=null) {
			for (String s : schemas) {
				if (s!=null && !s.equals("")) {
					setup.add(new ValidatorTask(type + " conformance checker: " + s, locator.getResource(s)));
				}
			}
		} 
	}
	
	private static void addXsltTask(String type, String[] schemas, List<InternalTask> setup, ResourceLocator locator, Map<String, Object> xsltParams) throws ResourceLocatorException {
		if (schemas!=null) {
			for (String s : schemas) {
				if (s!=null && s!="") {
					setup.add(new XsltTask(type + " to OBFL converter", locator.getResource(s), xsltParams));
				}
			}
		}
	}
	private static String[] removeSchemas(Properties p, String key) {
		Object o = p.remove(key);
		String value = (o instanceof String) ? (String)o : null;
		if (value==null) {
			return null;
		} else {
			return value.split("\\s*,\\s*");
		}
	}
}