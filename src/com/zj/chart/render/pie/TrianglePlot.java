package com.zj.chart.render.pie;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlotState;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.UnitType;

/**
 * A plot that displays data in the form of a pie chart, using data from any
 * class that implements the {@link PieDataset} interface. The example shown
 * here is generated by the <code>PieChartDemo2.java</code> program included in
 * the JFreeChart Demo Collection: <br>
 * <br>
 * <img src="../../../../images/PiePlotSample.png" alt="PiePlotSample.png" />
 * <P>
 * Special notes:
 * <ol>
 * <li>the default starting point is 12 o'clock and the pie sections proceed in
 * a clockwise direction, but these settings can be changed;</li>
 * <li>negative values in the dataset are ignored;</li>
 * <li>there are utility methods for creating a {@link PieDataset} from a
 * {@link org.jfree.data.category.CategoryDataset};</li>
 * </ol>
 * 
 * @see Plot
 * @see PieDataset
 */
@SuppressWarnings("serial")
public class TrianglePlot extends TPiePlot implements Cloneable, Serializable {
    
    private double triAngle;
    private double solidAngle;
    private boolean isThreeD;
    private double itemSpace;
    
    private Point2D.Double origenPoint;

    /**
     * Creates a new plot. The dataset is initially set to <code>null</code>.
     */
    public TrianglePlot() {
	this(null);
    }

    /**
     * Creates a plot that will draw a pie chart for the specified dataset.
     * 
     * @param dataset
     *            the dataset (<code>null</code> permitted).
     */
    public TrianglePlot(PieDataset dataset) {
	super();
	this.dataset = dataset;
	if (dataset != null) {
	    dataset.addChangeListener(this);
	}

	this.triAngle = 30.0;
	this.solidAngle = 15.0;
	this.itemSpace = 20.0;

	this.isThreeD = true;
	this.origenPoint = null;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     * 
     * @param g2
     *            the graphics device.
     * @param area
     *            the area within which the plot should be drawn.
     * @param anchor
     *            the anchor point (<code>null</code> permitted).
     * @param parentState
     *            the state from the parent plot, if there is one.
     * @param info
     *            collects info about the drawing (<code>null</code> permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {

	// adjust for insets...
	RectangleInsets insets = getInsets();
	insets.trim(area);

	if (info != null) {
	    info.setPlotArea(area);
	    info.setDataArea(area);
	}

	drawBackground(g2, area);
	drawOutline(g2, area);

	Shape savedClip = g2.getClip();
	g2.clip(area);

	Composite originalComposite = g2.getComposite();
	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));

	if (!DatasetUtilities.isEmptyOrNull(this.dataset)) {
	    drawPie(g2, area, info);
	} else {
	    drawNoDataMessage(g2, area);
	}

	g2.setClip(savedClip);
	g2.setComposite(originalComposite);

	drawOutline(g2, area);

    }

    /**
     * Draws the pie.
     * 
     * @param g2
     *            the graphics device.
     * @param plotArea
     *            the plot area.
     * @param info
     *            chart rendering info.
     */
    @SuppressWarnings("unchecked")
	protected void drawPie(Graphics2D g2, Rectangle2D plotArea, PlotRenderingInfo info) {

	PiePlotState state = initialise(g2, plotArea, this, null, info);

	// adjust the plot area for interior spacing and labels...
	double labelReserve = 0.0;
	if (this.getLabelGenerator() != null && !this.simpleLabels) {
	    labelReserve = this.getLabelGap() + this.getMaximumLabelWidth();
	}
	double gapHorizontal = plotArea.getWidth() * (this.getInteriorGap() + labelReserve) * 2.0;
	double gapVertical = plotArea.getHeight() * this.getInteriorGap() * 2.0;

	if (DEBUG_DRAW_INTERIOR) {
	    double hGap = plotArea.getWidth() * this.getInteriorGap();
	    double vGap = plotArea.getHeight() * this.getInteriorGap();

	    double igx1 = plotArea.getX() + hGap;
	    double igx2 = plotArea.getMaxX() - hGap;
	    double igy1 = plotArea.getY() + vGap;
	    double igy2 = plotArea.getMaxY() - vGap;
	    g2.setPaint(Color.gray);
	    g2.draw(new Rectangle2D.Double(igx1, igy1, igx2 - igx1, igy2 - igy1));
	}

	double linkX = plotArea.getX() + gapHorizontal / 2;
	double linkY = plotArea.getY() + gapVertical / 2;
	double linkW = plotArea.getWidth() - gapHorizontal;
	double linkH = plotArea.getHeight() - gapVertical;

	// make the link area a square if the pie chart is to be circular...
	if (this.isCircular()) {
	    double min = Math.min(linkW, linkH) / 2;
	    linkX = (linkX + linkX + linkW) / 2 - min;
	    linkY = (linkY + linkY + linkH) / 2 - min;
	    linkW = 2 * min;
	    linkH = 2 * min;
	}

	// the link area defines the dog leg points for the linking lines to
	// the labels
	Rectangle2D linkArea = new Rectangle2D.Double(linkX, linkY, linkW, linkH);
	state.setLinkArea(linkArea);

	if (DEBUG_DRAW_LINK_AREA) {
	    g2.setPaint(Color.blue);
	    g2.draw(linkArea);
	    g2.setPaint(Color.yellow);
	    g2.draw(new Ellipse2D.Double(linkArea.getX(), linkArea.getY(), linkArea.getWidth(), linkArea.getHeight()));
	}

	// the explode area defines the max circle/ellipse for the exploded
	// pie sections. it is defined by shrinking the linkArea by the
	// linkMargin factor.
	double lm = 0.0;
	if (!this.simpleLabels) {
	    lm = this.labelLinkMargin;
	}
	double hh = linkArea.getWidth() * lm * 2.0;
	double vv = linkArea.getHeight() * lm * 2.0;
	Rectangle2D explodeArea = new Rectangle2D.Double(linkX + hh / 2.0, linkY + vv / 2.0, linkW - hh, linkH - vv);

	state.setExplodedPieArea(explodeArea);

	// the pie area defines the circle/ellipse for regular pie sections.
	// it is defined by shrinking the explodeArea by the explodeMargin
	// factor.
	double maximumExplodePercent = getMaximumExplodePercent();
	double percent = maximumExplodePercent / (1.0 + maximumExplodePercent);

	double h1 = explodeArea.getWidth() * percent;
	double v1 = explodeArea.getHeight() * percent;
	Rectangle2D pieArea = new Rectangle2D.Double(explodeArea.getX() + h1 / 2.0, explodeArea.getY() + v1 / 2.0,
		explodeArea.getWidth() - h1, explodeArea.getHeight() - v1);

	if (DEBUG_DRAW_PIE_AREA) {
	    g2.setPaint(Color.green);
	    g2.draw(pieArea);
	}
	state.setPieArea(pieArea);
	state.setPieCenterX(pieArea.getCenterX());
	state.setPieCenterY(pieArea.getCenterY());
	state.setPieWRadius(pieArea.getWidth() / 2.0);
	state.setPieHRadius(pieArea.getHeight() / 2.0);

	this.itemSpace = pieArea.getWidth() / 20.0;
	this.origenPoint = new Point2D.Double(pieArea.getCenterX(), pieArea.getCenterY() + 0.4 * pieArea.getHeight());
	
	// plot the data (unless the dataset is null)...
	if ((this.dataset != null) && (this.dataset.getKeys().size() > 0)) {

	    List keys = this.dataset.getKeys();
	    double totalValue = DatasetUtilities.calculatePieDatasetTotal(this.dataset);

	    Rectangle2D[] labelArea = new Rectangle2D[keys.size()];
	    double maxValue = maxValue(this.dataset);
	    int passesRequired = state.getPassesRequired();
	    for (int pass = 0; pass < passesRequired; pass++) {
		for (int section = 0; section < keys.size(); section++) {
		    double value = 0.0;
		    Number n = this.dataset.getValue(section);
		    if (n.doubleValue() > 0.0) {
			value = n.doubleValue() * pieArea.getHeight() / maxValue;
			if ((section % 2) == 0) {
			    labelArea[section] = new Rectangle2D.Double(state.getPieCenterX() - value / 2, state
				    .getPieCenterY()
				    + pieArea.getHeight() / 2 - value, value / 2, value / 2);
			} else {
			    labelArea[section] = new Rectangle2D.Double(state.getPieCenterX(), state.getPieCenterY()
				    + pieArea.getHeight() / 2 - value, value, value);
			}
			
			if (!this.isThreeD) {
			    drawItem(g2, section, value, state, pass);
			} else {
			    double angle = this.solidAngle;
			    drawItem(g2, section, value, state, pass, angle);
			}
		    }
		}
	    }

	    if (this.simpleLabels) {
		drawSimpleLabels(g2, keys, totalValue, plotArea, labelArea, state);
	    } else {
		drawLabels(g2, keys, totalValue, plotArea, linkArea, state);
	    }

	} else {
	    drawNoDataMessage(g2, plotArea);
	}
    }

    /**
     * Draws a single data item.
     * 
     * @param g2
     *            the graphics device (<code>null</code> not permitted).
     * @param section
     *            the section index.
     * @param dataArea
     *            the data plot area.
     * @param state
     *            state information for one chart.
     * @param currentPass
     *            the current pass index.
     */
    @SuppressWarnings("unchecked")
	protected void drawItem(Graphics2D g2, int section, double value, PiePlotState state, int currentPass) {
	Comparable key = getSectionKey(section);

	double maxValue = state.getPieArea().getHeight();
	double angle = this.triAngle * Math.PI / 180.0;
	
	double shortGap = value * (Math.sin(angle)) / (Math.cos(angle));
	
	double flag = 0.0;

	if (section % 2 == 0) {
	    flag = 1.0;
	}else {
	    flag = -1.0;
	}
	
	GeneralPath path = new GeneralPath();
	path.moveTo(this.origenPoint.getX() - this.itemSpace * flag, this.origenPoint.getY());
	path.lineTo(this.origenPoint.getX() - this.itemSpace * flag, this.origenPoint.getY() - value);
	path.lineTo(this.origenPoint.getX() - this.itemSpace * flag - shortGap * flag, this.origenPoint.getY() - value);
	path.closePath();

	Rectangle2D rect = new Rectangle2D.Double(state.getPieCenterX() - 0.15 * maxValue, state.getPieCenterY() + 0.45
		* maxValue, 0.3 * maxValue, 0.02 * maxValue);

	if (currentPass == 0) {
	    Paint shadowPaint = getShadowPaint();
	    double shadowXOffset = getShadowXOffset();
	    double shadowYOffset = getShadowYOffset();
	    if (shadowPaint != null) {
		Shape shadowArc = ShapeUtilities.createTranslatedShape(path, (float) shadowXOffset,
			(float) shadowYOffset);
		g2.setPaint(shadowPaint);
		g2.fill(shadowArc);
		g2.fill(rect);
	    }
	} else if (currentPass == 1) {
	    Paint paint = lookupSectionPaint(key);
	    g2.setPaint(paint);
	    g2.fill(path);

	    g2.setPaint(new Color(50, 30, 40));
	    g2.fill(rect);
	    Paint outlinePaint = lookupSectionOutlinePaint(key);
	    Stroke outlineStroke = lookupSectionOutlineStroke(key);
	    if (outlinePaint != null && outlineStroke != null) {
		g2.setPaint(outlinePaint);
		g2.setStroke(outlineStroke);
		g2.draw(path);
	    }
	}
    }

    /**
     * Draws a single data item.
     * 
     * @param g2
     *            the graphics device (<code>null</code> not permitted).
     * @param section
     *            the section index.
     * @param dataArea
     *            the data plot area.
     * @param state
     *            state information for one chart.
     * @param currentPass
     *            the current pass index.
     */
    @SuppressWarnings("unchecked")
	protected void drawItem(Graphics2D g2, int section, double value, PiePlotState state, int currentPass, double angle) {
	Comparable key = getSectionKey(section);

	double angle1 = this.triAngle * Math.PI / 180.0;
	double angle2 = angle * Math.PI / 180.0;
	
	double shortGap = value * (Math.sin(angle1)) / (Math.cos(angle1));
	double shortGapX = shortGap * Math.cos(angle2);
	double shortGapY = shortGap * Math.sin(angle2);

	GeneralPath path = new GeneralPath();
	if ((section % 2) == 0) {
	    path.moveTo(this.origenPoint.getX(), this.origenPoint.getY());
	    path.lineTo(this.origenPoint.getX(), this.origenPoint.getY() - value);
	    path.lineTo(this.origenPoint.getX() - shortGapX, this.origenPoint.getY() + shortGapY);
	} else {
	    path.moveTo(this.origenPoint.getX(), this.origenPoint.getY());
	    path.lineTo(this.origenPoint.getX(), this.origenPoint.getY() - value);
	    path.lineTo(this.origenPoint.getX() + shortGapX, this.origenPoint.getY() + shortGapY);
	}
	
	path.closePath();

	if (currentPass == 0) {
	    Paint shadowPaint = getShadowPaint();
	    double shadowXOffset = getShadowXOffset();
	    double shadowYOffset = getShadowYOffset();
	    if (shadowPaint != null) {
		Shape shadowArc = ShapeUtilities.createTranslatedShape(path, (float) shadowXOffset,
			(float) shadowYOffset);
		g2.setPaint(shadowPaint);
		g2.fill(shadowArc);
	    }
	} else if (currentPass == 1) {
	    Paint paint = lookupSectionPaint(key);
	    g2.setPaint(paint);
	    g2.fill(path);

	    Paint outlinePaint = lookupSectionOutlinePaint(key);
	    Stroke outlineStroke = lookupSectionOutlineStroke(key);
	    if (outlinePaint != null && outlineStroke != null) {
		g2.setPaint(outlinePaint);
		g2.setStroke(outlineStroke);
		g2.draw(path);		
	    }
	}
    }

    /**
     * Draws the pie section labels in the simple form.
     * 
     * @param g2
     *            the graphics device.
     * @param keys
     *            the section keys.
     * @param totalValue
     *            the total value for all sections in the pie.
     * @param plotArea
     *            the plot area.
     * @param pieArea
     *            the area containing the pie.
     * @param state
     *            the plot state.
     * @since 1.0.7
     */
    @SuppressWarnings("unchecked")
	protected void drawSimpleLabels(Graphics2D g2, List keys, double totalValue, Rectangle2D plotArea,
	    Rectangle2D[] pieArea, PiePlotState state) {

	Composite originalComposite = g2.getComposite();
	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

	RectangleInsets labelInsets = new RectangleInsets(UnitType.RELATIVE, 0.18, 0.18, 0.18, 0.18);

	int section = 0;
	double runningTotal = 0.0;
	Iterator iterator = keys.iterator();
	while (iterator.hasNext()) {
	    Comparable key = (Comparable) iterator.next();
	    if(null == pieArea[section])
	    {
	    	continue;
	    }
	    Rectangle2D labelsArea = labelInsets.createInsetRectangle(pieArea[section]);

	    boolean include = true;
	    double v = 0.0;
	    Number n = getDataset().getValue(key);
	    if (n == null) {
		include = !getIgnoreNullValues();
	    } else {
		v = n.doubleValue();
		include = getIgnoreZeroValues() ? v > 0.0 : v >= 0.0;
	    }

	    if (include) {
		runningTotal = runningTotal + v;
		// work out the mid angle (0 - 90 and 270 - 360) = right,
		// otherwise left
		double mid = getStartAngle()
			+ (getDirection().getFactor() * ((runningTotal - v / 2.0) * 360) / totalValue);

		Arc2D arc = new Arc2D.Double(labelsArea, getStartAngle(), mid - getStartAngle(), Arc2D.OPEN);
		int x = (int) arc.getEndPoint().getX();
		int y = (int) arc.getEndPoint().getY();

		PieSectionLabelGenerator labelGenerator = getLabelGenerator();
		if (labelGenerator == null) {
		    continue;
		}
		String label = labelGenerator.generateSectionLabel(this.dataset, key);
		if (label == null) {
		    continue;
		}
		g2.setFont(this.getLabelFont());
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D bounds = TextUtilities.getTextBounds(label, g2, fm);
		Rectangle2D out = this.getLabelPadding().createOutsetRectangle(bounds);
		Shape bg = ShapeUtilities.createTranslatedShape(out, x - bounds.getCenterX(), y - bounds.getCenterY());
		if (this.getLabelShadowPaint() != null) {
		    Shape shadow = ShapeUtilities.createTranslatedShape(bg, this.getShadowXOffset(), this
			    .getShadowYOffset());
		    g2.setPaint(this.getLabelShadowPaint());
		    g2.fill(shadow);
		}
		if (this.getLabelBackgroundPaint() != null) {
		    g2.setPaint(this.getLabelBackgroundPaint());
		    g2.fill(bg);
		}
		if (this.getLabelOutlinePaint() != null && this.getLabelOutlineStroke() != null) {
		    g2.setPaint(this.getLabelOutlinePaint());
		    g2.setStroke(this.getLabelOutlineStroke());
		    g2.draw(bg);
		}

		g2.setPaint(this.getLabelPaint());
		g2.setFont(this.getLabelFont());
		TextUtilities.drawAlignedString(getLabelGenerator().generateSectionLabel(getDataset(), key), g2, x, y,
			TextAnchor.CENTER);

	    }
	    section++;
	}

	g2.setComposite(originalComposite);

    }

    public double maxValue(PieDataset data) {
	int sizeCount = dataset.getKeys().size();
	double maxValue = dataset.getValue(0).doubleValue();

	for (int section = 0; section < sizeCount; section++) {
	    Number n = this.dataset.getValue(section);
	    if (n != null && (n.doubleValue() > maxValue)) {
		maxValue = n.doubleValue();
	    }
	}
	return maxValue;
    }

    /**
     * Draws the pie section labels in the simple form.
     * 
     * @param g2
     *            the graphics device.
     * @param keys
     *            the section keys.
     * @param totalValue
     *            the total value for all sections in the pie.
     * @param plotArea
     *            the plot area.
     * @param pieArea
     *            the area containing the pie.
     * @param state
     *            the plot state.
     * @since 1.0.7
     */
    @SuppressWarnings("unchecked")
	protected void drawSimpleLabels(Graphics2D g2, List keys, double totalValue, Rectangle2D plotArea,
	    Rectangle2D pieArea, PiePlotState state) {

	Composite originalComposite = g2.getComposite();
	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

	RectangleInsets labelInsets = new RectangleInsets(UnitType.RELATIVE, 0.18, 0.18, 0.18, 0.18);
	Rectangle2D labelsArea = labelInsets.createInsetRectangle(pieArea);
	double runningTotal = 0.0;
	Iterator iterator = keys.iterator();
	while (iterator.hasNext()) {
	    Comparable key = (Comparable) iterator.next();
	    boolean include = true;
	    double v = 0.0;
	    Number n = getDataset().getValue(key);
	    if (n == null) {
		include = !getIgnoreNullValues();
	    } else {
		v = n.doubleValue();
		include = getIgnoreZeroValues() ? v > 0.0 : v >= 0.0;
	    }

	    if (include) {
		runningTotal = runningTotal + v;
		// work out the mid angle (0 - 90 and 270 - 360) = right,
		// otherwise left
		double mid = getStartAngle()
			+ (getDirection().getFactor() * ((runningTotal - v / 2.0) * 360) / totalValue);

		Arc2D arc = new Arc2D.Double(labelsArea, getStartAngle(), mid - getStartAngle(), Arc2D.OPEN);
		int x = (int) arc.getEndPoint().getX();
		int y = (int) arc.getEndPoint().getY();

		PieSectionLabelGenerator labelGenerator = getLabelGenerator();
		if (labelGenerator == null) {
		    continue;
		}
		String label = labelGenerator.generateSectionLabel(this.dataset, key);
		if (label == null) {
		    continue;
		}
		g2.setFont(this.getLabelFont());
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D bounds = TextUtilities.getTextBounds(label, g2, fm);
		Rectangle2D out = this.getLabelPadding().createOutsetRectangle(bounds);
		Shape bg = ShapeUtilities.createTranslatedShape(out, x - bounds.getCenterX(), y - bounds.getCenterY());
		if (this.getLabelShadowPaint() != null) {
		    Shape shadow = ShapeUtilities.createTranslatedShape(bg, this.getShadowXOffset(), this
			    .getShadowYOffset());
		    g2.setPaint(this.getLabelShadowPaint());
		    g2.fill(shadow);
		}
		if (this.getLabelBackgroundPaint() != null) {
		    g2.setPaint(this.getLabelBackgroundPaint());
		    g2.fill(bg);
		}
		if (this.getLabelOutlinePaint() != null && this.getLabelOutlineStroke() != null) {
		    g2.setPaint(this.getLabelOutlinePaint());
		    g2.setStroke(this.getLabelOutlineStroke());
		    g2.draw(bg);
		}

		g2.setPaint(this.getLabelPaint());
		g2.setFont(this.getLabelFont());
		TextUtilities.drawAlignedString(getLabelGenerator().generateSectionLabel(getDataset(), key), g2, x, y,
			TextAnchor.CENTER);

	    }
	}

	g2.setComposite(originalComposite);

    }

    /**
     * Returns a clone of the plot.
     * 
     * @return A clone.
     * @throws CloneNotSupportedException
     *             if some component of the plot does not support cloning.
     */
    public Object clone() throws CloneNotSupportedException {
	TrianglePlot clone = (TrianglePlot) super.clone();
	if (clone.dataset != null) {
	    clone.dataset.addChangeListener(clone);
	}
	if (this.getUrlGenerator() instanceof PublicCloneable) {
	    clone.setUrlGenerator((PieURLGenerator) ObjectUtilities.clone(this.getUrlGenerator()));
	}
	clone.setLegendItemShape(ShapeUtilities.clone(this.getLegendItemShape()));
	if (this.getLegendLabelGenerator() != null) {
	    clone.setLegendLabelGenerator((PieSectionLabelGenerator) ObjectUtilities.clone(this
		    .getLegendLabelGenerator()));
	}
	if (this.getLegendLabelToolTipGenerator() != null) {
	    clone.setLegendLabelToolTipGenerator((PieSectionLabelGenerator) ObjectUtilities.clone(this
		    .getLegendLabelToolTipGenerator()));
	}
	if (this.getLegendLabelURLGenerator() instanceof PublicCloneable) {
	    clone
		    .setLegendLabelURLGenerator((PieURLGenerator) ObjectUtilities.clone(this
			    .getLegendLabelURLGenerator()));
	}
	return clone;
    }

    public void setTriAngle(double angle) {
	if ((angle % 90.0) < 60.0) {
	    this.triAngle = angle;
	}
    }

    public void setSolidAngle(double angle) {
	this.solidAngle = angle;
    }

    public void setIsThreeD(boolean bool) {
	this.isThreeD = bool;
    }
}