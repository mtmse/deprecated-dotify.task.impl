package org.daisy.dotify.tasks.impl.input.xml;

import java.util.Map;

import org.daisy.dotify.common.xml.TransformerEnvironment;
import org.daisy.dotify.common.xml.XMLTools;
import org.daisy.streamline.api.tasks.library.XsltApplierException;

class XmlToolsAdapter {

	static void transform(Object source, Object result, Object xslt, Map<String, Object> params) throws XsltApplierException {
		XMLTools.transform(source, result, xslt, 
			TransformerEnvironment.builder()
				.parameters(params)
				.transformerFactory(new net.sf.saxon.TransformerFactoryImpl())
				.build(XsltApplierException::new)
		);
	}

}
