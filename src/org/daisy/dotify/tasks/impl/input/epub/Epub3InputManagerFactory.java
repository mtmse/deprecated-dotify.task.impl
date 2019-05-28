package org.daisy.dotify.tasks.impl.input.epub;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.streamline.api.tasks.TaskGroup;
import org.daisy.streamline.api.tasks.TaskGroupFactory;
import org.daisy.streamline.api.tasks.TaskGroupInformation;
import org.daisy.streamline.api.tasks.TaskGroupSpecification;
import org.osgi.service.component.annotations.Component;

/**
 * Provides an factory for epub 3 to html conversion.
 * @author Joel Håkansson
 *
 */
@Component
public class Epub3InputManagerFactory implements TaskGroupFactory {
	private static final Logger LOGGER = Logger.getLogger(Epub3InputManagerFactory.class.getCanonicalName());
	private final Set<TaskGroupInformation> information;

	/**
	 * Creates a new epub 3 input manager factory.
	 */
	public Epub3InputManagerFactory() {
		Set<TaskGroupInformation> tmp = new HashSet<>();
		tmp.add(TaskGroupInformation.newConvertBuilder("epub", "html").build());
		tmp.add(TaskGroupInformation.newConvertBuilder("epub", "xhtml").build());
		information = Collections.unmodifiableSet(tmp);
	}
	
	@Override
	public boolean supportsSpecification(TaskGroupInformation spec) {
		return listAll().contains(spec);
	}

	@Override
	public TaskGroup newTaskGroup(TaskGroupSpecification spec) {
		boolean strict = "xhtml".equalsIgnoreCase(spec.getOutputType().getIdentifier());
		if (!strict && LOGGER.isLoggable(Level.INFO)) {
			LOGGER.info("HTML output will be XML compliant.");
		}
		return new Epub3InputManager(strict);
	}
	@Override
	public Set<TaskGroupInformation> listAll() {
		return information;
	}

}