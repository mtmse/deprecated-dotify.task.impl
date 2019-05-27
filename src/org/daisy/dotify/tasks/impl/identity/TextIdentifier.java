package org.daisy.dotify.tasks.impl.identity;

import org.daisy.streamline.api.identity.IdentificationFailedException;
import org.daisy.streamline.api.identity.Identifier;
import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.media.AnnotatedInputStream;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;
import org.daisy.streamline.api.media.DefaultAnnotatedInputStream;
import org.daisy.streamline.api.media.DefaultFileDetails;
import org.daisy.streamline.api.media.InputStreamSupplier;

/**
 * Provides an identifier for text files.
 * @author Joel Håkansson
 */
public class TextIdentifier implements Identifier {
	private final DefaultFileDetails textDetails;
	
	TextIdentifier() {
		this.textDetails = new DefaultFileDetails.Builder()
					.formatName("text")
					.extension("txt")
					.mediaType("text/plain")
					.build();
	}

	@Override
	public AnnotatedFile identify(AnnotatedFile f) throws IdentificationFailedException {
		if (f.getPath().toString().endsWith(".txt")) {
			return new DefaultAnnotatedFile.Builder(f.getPath())
					.formatName(textDetails.getFormatName())
					.extension(textDetails.getExtension())
					.mediaType(textDetails.getMediaType())
					.build();
		} else {
			throw new IdentificationFailedException();
		}
	}

	@Override
	public AnnotatedInputStream identify(InputStreamSupplier source) throws IdentificationFailedException {
		return new DefaultAnnotatedInputStream.Builder(source).details(textDetails).build();
	}

}
