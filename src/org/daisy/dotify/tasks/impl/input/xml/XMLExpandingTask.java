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
			
			return DefaultInputUrlResourceLocator.getConfiguration(DefaultInputUrlResourceLocator.getInstance().getConfigFileName(rootElement, rootNS), rootElement, rootNS, template, xsltParams, localLocator, commonLocator);
		} catch (IdentificationFailedException e) {
			throw new InternalTaskException("Failed to read input as xml", e);
		}
	}
	
	@Override
	@Deprecated
	public List<InternalTask> resolve(File input) throws InternalTaskException {
		return resolve(new DefaultAnnotatedFile.Builder(input).build());
	}

}