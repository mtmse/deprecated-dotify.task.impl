package org.daisy.dotify.tasks.impl.input.epub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.streamline.api.tasks.InternalTask;
import org.daisy.streamline.api.tasks.TaskGroup;
import org.daisy.streamline.api.tasks.TaskSystemException;

/**
 * Provides an epub 3 to html task group.
 * @author Joel Håkansson
 *
 */
public class Epub3InputManager implements TaskGroup {
	private final boolean strict;
	
	Epub3InputManager(boolean strict) {
		this.strict = strict;
	}

	@Override
	public List<InternalTask> compile(Map<String, Object> parameters) throws TaskSystemException {
		List<InternalTask> ret = new ArrayList<>();
		if (strict) {
			ret.add(new Epub3Task("Epub to XHTML converter", (String)parameters.get("opf-path"), strict));
		} else {
			ret.add(new Epub3Task("Epub to HTML converter", (String)parameters.get("opf-path"), strict));
		}
		return ret;
	}

}