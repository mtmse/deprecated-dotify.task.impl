package org.daisy.dotify.tasks.impl.identity;

import org.daisy.streamline.api.identity.Identifier;
import org.daisy.streamline.api.identity.IdentifierFactory;
import org.daisy.streamline.api.media.FileDetails;
import org.osgi.service.component.annotations.Component;

/**
 * Provides a factory for identifying text files.
 * @author Joel Håkansson
 */
@Component
public class TextIdentifierFactory implements IdentifierFactory {

	/**
	 * Creates a new text identifier factory.
	 */
	public TextIdentifierFactory() {
		super();
	}

	@Override
	public Identifier newIdentifier() {
		return new TextIdentifier();
	}

	@Override
	public boolean accepts(FileDetails type) {
		return (type.getFormatName()==null);
	}

}
