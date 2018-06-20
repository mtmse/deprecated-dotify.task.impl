package org.daisy.dotify.tasks.impl.input.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

enum DefaultInputUrlResourceLocator {
	INSTANCE;
	private Map<String, String> props;

	private DefaultInputUrlResourceLocator() {
		props = new HashMap<>();
		props.put("dtbook@http://www.daisy.org/z3986/2005/dtbook/", "dtbook.properties");
		props.put("html@http://www.w3.org/1999/xhtml", "html.properties");
	}

	static DefaultInputUrlResourceLocator getInstance() {
		return INSTANCE;
	}
	
	String getConfigFileName(String rootElement, String rootNS) {
		if (rootNS!=null) {
			return props.get(rootElement+"@"+rootNS);
		} else {
			return props.get(rootElement);
		}
	}

	Set<String> listFileFormats() {
		return props.keySet().stream().map(s->{
			int inx;
			if ((inx = s.indexOf('@')) > -1) {
				return s.substring(0, inx);
			} else {
				return s;
			}
		}).collect(Collectors.toSet());
	}

}
