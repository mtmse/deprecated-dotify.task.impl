package org.daisy.dotify.tasks.impl.input.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

class Text2ObflWriter extends Xml2ObflWriter {
	static final int DEFAULT_HEIGHT = 29;
	static final int DEFAULT_WIDTH = 32;
	static final int DEFAULT_INNER_MARGIN = 2;
	static final int DEFAULT_OUTER_MARGIN = 2;
	static final double DEFAULT_ROW_SPACING = 1.0;
	static final boolean DEFAULT_DUPLEX = true;
	static final boolean DEFAULT_SHOW_BRAILLE_PAGE_NUMBERS = false;
	private int height;
	private int width;
	private int innerMargin;
	private int outerMargin;
	private double rowSpacing;
	private boolean duplex;
	private boolean showBraillePageNumbers;

	Text2ObflWriter(InputStream is, OutputStream os, String encoding) {
		super(is, os, encoding);
		this.height = DEFAULT_HEIGHT;
		this.width = DEFAULT_WIDTH;
		this.innerMargin = DEFAULT_INNER_MARGIN;
		this.outerMargin = DEFAULT_OUTER_MARGIN;
		this.rowSpacing = DEFAULT_ROW_SPACING;
		this.duplex = DEFAULT_DUPLEX;
		this.showBraillePageNumbers = DEFAULT_SHOW_BRAILLE_PAGE_NUMBERS;
	}

	Text2ObflWriter(File input, File output, String encoding) throws FileNotFoundException {
		this(new FileInputStream(input), new FileOutputStream(output), encoding);
	}
	
	Text2ObflWriter(String input, String output, String encoding) throws FileNotFoundException {
		this(new File(input), new File(output), encoding);
	}
	
	int getHeight() {
		return height;
	}

	void setHeight(int value) {
		this.height = value;
	}

	int getWidth() {
		return width;
	}
	
	void setWidth(int value) {
		this.width = value;
	}
	
	int getInnerMargin() {
		return innerMargin;
	}

	void setInnerMargin(int innerMargin) {
		this.innerMargin = innerMargin;
	}

	int getOuterMargin() {
		return outerMargin;
	}

	void setOuterMargin(int outerMargin) {
		this.outerMargin = outerMargin;
	}

	double getRowSpacing() {
		return rowSpacing;
	}

	void setRowSpacing(double rowSpacing) {
		this.rowSpacing = rowSpacing;
	}

	boolean isDuplex() {
		return duplex;
	}

	void setDuplex(boolean duplex) {
		this.duplex = duplex;
	}
	
	boolean isShowBraillePageNumbers() {
		return showBraillePageNumbers;
	}

	void setShowBraillePageNumbers(boolean showBraillePageNumbers) {
		this.showBraillePageNumbers = showBraillePageNumbers;
	}

	@Override
	protected void writePrologue(PrintWriter pw) {
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.println("<obfl version=\"2011-1\" xml:lang=\"" + getRootLang() + "\" xmlns=\"http://www.daisy.org/ns/2011/obfl\">");
		pw.println(String.format(
				"<layout-master name=\"default\" page-height=\"%s\" page-width=\"%s\" inner-margin=\"%s\" outer-margin=\"%s\" row-spacing=\"%s\" duplex=\"%s\">", 
				height, width, innerMargin, outerMargin, rowSpacing, duplex));
		
		if (showBraillePageNumbers) {
			if (duplex) {
				pw.println("<template use-when=\"(= (% $page 2) 0)\">");
				pw.println("	<header>");
				pw.println("		<field>");
				pw.println("			<string value=\"  \"/>");
				pw.println("			<current-page number-format=\"default\"/>");
				pw.println("		</field>");
				pw.println("		<field/>");
				pw.println("	</header>");
				pw.println("	<footer/>");
				pw.println("</template>");
			}
			pw.println("<default-template>");
			pw.println("	<header>");
			pw.println("		<field/>");
			pw.println("		<field>");
			pw.println("			<current-page number-format=\"default\"/>");
			pw.println("		</field>");
			pw.println("	</header>");
			pw.println("	<footer/>");
			pw.println("</default-template>");
		} else {
			pw.println("<default-template>");
			pw.println("<header></header>");
			pw.println("<footer></footer>");
			pw.println("</default-template>");
		}
		pw.println("</layout-master>");
		pw.println("<sequence master=\"default\">");
	}

	@Override
	protected void startPara(PrintWriter pw) {
		pw.print("<block>");
	}

	@Override
	protected void endPara(PrintWriter pw) {
		pw.println("</block>");
	}

	@Override
	protected void writeEpilogue(PrintWriter pw) {
		pw.println("</sequence>");
		pw.println("</obfl>");
	}

}
