package org.daisy.dotify.tasks.impl.input.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.common.xml.XMLTools;
import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;
import org.daisy.streamline.api.option.UserOption;
import org.daisy.streamline.api.option.UserOptionValue;
import org.daisy.streamline.api.tasks.InternalTaskException;
import org.daisy.streamline.api.tasks.ReadWriteTask;

class Text2ObflTask extends ReadWriteTask {
	private static final Logger LOGGER = Logger.getLogger(Text2ObflTask.class.getCanonicalName());
	private static final String SOURCE_ENCODING = "source-encoding";
	private static final String SOURCE_LANGUAGE = "source-language";
	private static final String PAGE_WIDTH = "page-width";
	private static final String PAGE_HEIGHT = "page-height";
	private static final String DUPLEX = "duplex";
	private static final String INNER_MARGIN = "inner-margin";
	private static final String OUTER_MARGIN = "outer-margin";
	private static final String ROW_SPACING = "row-spacing";
	private static final String SHOW_BRAILLE_PAGE_NUMBERS = "show-braille-page-numbers";
	
	private static final String DEFAULT_ENCODING = "utf-8";
	private final String encoding;
	private final String rootLang;
	private final Map<String, Object> params;
	private static List<UserOption> options = null;
	
	Text2ObflTask(String name, String rootLang, Map<String, Object> params) {
		this(name, rootLang, getEncoding(params), params);
	}

	Text2ObflTask(String name, String rootLang, String encoding, Map<String, Object> params) {
		super(name);
		this.rootLang = rootLang;
		this.encoding = encoding;
		this.params = params;
	}
	
	private static String getEncoding(Map<String, Object> params) {
		Object param = params.get(SOURCE_ENCODING);
		return (param!=null)?""+param:null;
	}
	
	private String getLanguage() {
		Object param = params.get(SOURCE_LANGUAGE);
		return (param!=null)?""+param:rootLang;
	}

	@Override
	public AnnotatedFile execute(AnnotatedFile input, File output) throws InternalTaskException {
		try {
			Text2ObflWriter fw = new Text2ObflWriter(input.getPath().toFile(), output, encoding!=null?encoding:
				XMLTools.detectBomEncoding(Files.readAllBytes(input.getPath()))
					.map(v->v.name())
					.orElse(DEFAULT_ENCODING));
			fw.setRootLang(getLanguage());
		
			asIntParam(PAGE_WIDTH).ifPresent(v->fw.setWidth(v));
			asIntParam(PAGE_HEIGHT).ifPresent(v->fw.setHeight(v));
			asIntParam(INNER_MARGIN).ifPresent(v->fw.setInnerMargin(v));
			asIntParam(OUTER_MARGIN).ifPresent(v->fw.setOuterMargin(v));
			asDoubleParam(ROW_SPACING).ifPresent(v->fw.setRowSpacing(v));
			asBooleanParam(DUPLEX).ifPresent(v->fw.setDuplex(v));
			asBooleanParam(SHOW_BRAILLE_PAGE_NUMBERS).ifPresent(v->fw.setShowBraillePageNumbers(v));
			fw.parse();

		} catch (FileNotFoundException e) {
			throw new InternalTaskException("FileNotFoundException", e);
		} catch (IOException e) {
			throw new InternalTaskException("IOException", e);
		}
		return new DefaultAnnotatedFile.Builder(output.toPath()).extension("obfl").mediaType("application/x-obfl+xml").build();
	}
	
	private Optional<Integer> asIntParam(String key) {
		return Optional.ofNullable(params.get(key)).map(v->{
			try {
				return Integer.parseInt(v.toString());
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, String.format("Failed to set %s.", key), e);
				return null;
			}
		});
	}
	
	private Optional<Double> asDoubleParam(String key) {
		return Optional.ofNullable(params.get(key)).map(v->{
			try {
				return Double.parseDouble(v.toString());
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, String.format("Failed to set %s.", key), e);
				return null;
			}
		});
	}
	
	private Optional<Boolean> asBooleanParam(String key) {
		return Optional.ofNullable(params.get(key)).map(v->Boolean.valueOf(v.toString()));
	}

	@Override
	@Deprecated
	public void execute(File input, File output) throws InternalTaskException {
		execute(new DefaultAnnotatedFile.Builder(input).build(), output);
	}

	@Override
	public List<UserOption> getOptions() {
		if (options==null) {
			options = new ArrayList<>();
			options.add(new UserOption.Builder(PAGE_WIDTH)
					.description("The width of the page (a positive integer)")
					.defaultValue(String.valueOf(Text2ObflWriter.DEFAULT_WIDTH))
					.build());
			options.add(new UserOption.Builder(PAGE_HEIGHT)
					.description("The height of the page (a positive integer)")
					.defaultValue(String.valueOf(Text2ObflWriter.DEFAULT_HEIGHT))
					.build());
			options.add(new UserOption.Builder(INNER_MARGIN)
					.description("The inner margin (a non-negative integer)")
					.defaultValue(String.valueOf(Text2ObflWriter.DEFAULT_INNER_MARGIN))
					.build());
			options.add(new UserOption.Builder(OUTER_MARGIN)
					.description("The outer margin (a non-negative integer)")
					.defaultValue(String.valueOf(Text2ObflWriter.DEFAULT_OUTER_MARGIN))
					.build());
			options.add(new UserOption.Builder(ROW_SPACING)
					.description("The row spacing (a number >= 1)")
					.defaultValue(String.valueOf(Text2ObflWriter.DEFAULT_ROW_SPACING))
					.build());
			options.add(new UserOption.Builder(DUPLEX)
					.description("Layout on both sides of the sheet")
					.addValue(new UserOptionValue.Builder("true").build())
					.addValue(new UserOptionValue.Builder("false").build())
					.defaultValue(String.valueOf(Text2ObflWriter.DEFAULT_DUPLEX)).build());
			options.add(new UserOption.Builder(SHOW_BRAILLE_PAGE_NUMBERS)
					.description("Show braille page numbers in the header")
					.addValue(new UserOptionValue.Builder("true").build())
					.addValue(new UserOptionValue.Builder("false").build())
					.defaultValue(String.valueOf(Text2ObflWriter.DEFAULT_SHOW_BRAILLE_PAGE_NUMBERS))
					.build());
			options.add(new UserOption.Builder(SOURCE_ENCODING)
					.description("The encoding of the input file")
					.defaultValue("[detect]")
					.build());
			options.add(new UserOption.Builder(SOURCE_LANGUAGE)
					.description("The language of the input file")
					.defaultValue(rootLang)
					.build());
		}
		return options;
	}

}
