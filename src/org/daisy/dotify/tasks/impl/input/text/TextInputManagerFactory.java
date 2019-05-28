package org.daisy.dotify.tasks.impl.input.text;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.tasks.impl.input.text.TextInputManager.Type;
import org.daisy.streamline.api.tasks.TaskGroup;
import org.daisy.streamline.api.tasks.TaskGroupFactory;
import org.daisy.streamline.api.tasks.TaskGroupInformation;
import org.daisy.streamline.api.tasks.TaskGroupSpecification;
import org.osgi.service.component.annotations.Component;

/**
 * Provides a task group factory for text input.
 * @author Joel Håkansson
 */
@Component
public class TextInputManagerFactory implements TaskGroupFactory {
	private static final Logger LOGGER = Logger.getLogger(TextInputManagerFactory.class.getCanonicalName());
	private final Set<TaskGroupInformation> information;
	private static final String HTML = "html";
	private static final String XHTML = "xhtml";

	/**
	 * Creates a new text input manager factory.
	 */
	public TextInputManagerFactory() {
		String text = "text";
		// TODO: remove txt
		String txt = "txt"; 
		String obfl = "obfl";
		
		Set<TaskGroupInformation> tmp = new HashSet<>();
		tmp.add(TaskGroupInformation.newConvertBuilder(text, obfl).build());
		tmp.add(TaskGroupInformation.newConvertBuilder(txt, obfl).build());
		tmp.add(TaskGroupInformation.newConvertBuilder(text, HTML).build());
		tmp.add(TaskGroupInformation.newConvertBuilder(txt, HTML).build());
		tmp.add(TaskGroupInformation.newConvertBuilder(text, XHTML).build());
		tmp.add(TaskGroupInformation.newConvertBuilder(txt, XHTML).build());
		information = Collections.unmodifiableSet(tmp);
	}
	
	@Override
	public boolean supportsSpecification(TaskGroupInformation spec) {
		return listAll().contains(spec);
	}

	@Override
	public TaskGroup newTaskGroup(TaskGroupSpecification spec) {
		if ("txt".equalsIgnoreCase(spec.getInputType().getIdentifier()) && LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.log(Level.WARNING, "Format identifier \"txt\" is deprecated, use \"text\" instead.");
		}
		if (HTML.equalsIgnoreCase(spec.getOutputType().getIdentifier())) {
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("HTML output will be XML compliant.");
			}
			return new TextInputManager(spec.getLocale(), Type.HTML);
		} else if (XHTML.equalsIgnoreCase(spec.getOutputType().getIdentifier())) {
			return new TextInputManager(spec.getLocale(), Type.XHTML);
		} else {
			return new TextInputManager(spec.getLocale(), Type.OBFL);
		}
	}

	@Override
	public Set<TaskGroupInformation> listAll() {
		return information;
	}

}
