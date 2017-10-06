package com.zj.chart.render.pie;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PiePlotState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;

@SuppressWarnings("serial")
public class TRingPlot3D extends TRingPlot
{
	    /** The factor of the depth of the pie from the plot height */
	    private double depthFactor = 0.12;

	    /**
	     * This debug flag controls whether or not an outline is drawn showing the
	     * interior of the plot region.  This is drawn as a lightGray rectangle
	     * showing the padding provided by the 'interiorGap' setting.
	     */
	    static final boolean DEBUG_DRAW_INTERIOR = false;

	    /**
	     * This debug flag controls whether or not an outline is drawn showing the
	     * link area (in blue) and link ellipse (in yellow).  This controls where
	     * the label links have 'elbow' points.
	     */
	    static final boolean DEBUG_DRAW_LINK_AREA = false;

	    /**
	     * This debug flag controls whether or not an outline is drawn showing
	     * the pie area (in green).
	     */
	    static final boolean DEBUG_DRAW_PIE_AREA = false;
	    
	    /**
	     * A flag that controls whether or not the sides of the pie chart
	     * are rendered using a darker colour.
	     *
	     *  @since 1.0.7.
	     */
	    private boolean darkerSides = true;  // default preserves previous
	                                          // behaviour
	    
	    /**
	     * Creates a new instance with no dataset.
	     */
	    public TRingPlot3D() {
	        this(null);
	    }

	    /**
	     * Creates a pie chart with a three dimensional effect using the specified
	     * dataset.
	     *
	     * @param dataset  the dataset (<code>null</code> permitted).
	     */
	    public TRingPlot3D(PieDataset dataset) {
	        super(dataset);
	        setCircular(false, false);
	    }

	    /**
	     * Returns the depth factor for the chart.
	     *
	     * @return The depth factor.
	     *
	     * @see #setDepthFactor(double)
	     */
	    public double getDepthFactor() {
	        return this.depthFactor;
	    }

	    /**
	     * Sets the pie depth as a percentage of the height of the plot area, and
	     * sends a {@link PlotChangeEvent} to all registered listeners.
	     *
	     * @param factor  the depth factor (for example, 0.20 is twenty percent).
	     *
	     * @see #getDepthFactor()
	     */
	    public void setDepthFactor(double factor) {
	        this.depthFactor = factor;
	        fireChangeEvent();
	    }

	    /**
	     * Returns a flag that controls whether or not the sides of the pie chart
	     * are rendered using a darker colour.  This is only applied if the
	     * section colour is an instance of {@link java.awt.Color}.
	     *
	     * @return A boolean.
	     *
	     * @see #setDarkerSides(boolean)
	     *
	     * @since 1.0.7
	     */
	    public boolean getDarkerSides() {
	        return this.darkerSides;
	    }

	    /**
	     * Sets a flag that controls whether or not the sides of the pie chart
	     * are rendered using a darker colour, and sends a {@link PlotChangeEvent}
	     * to all registered listeners.  This is only applied if the
	     * section colour is an instance of {@link java.awt.Color}.
	     *
	     * @param darker true to darken the sides, false to use the default
	     *         behaviour.
	     *
	     * @see #getDarkerSides()
	     *
	     * @since 1.0.7.
	     */
	    public void setDarkerSides(boolean darker) {
	        this.darkerSides = darker;
	        fireChangeEvent();
	    }

	    /**
	     * Draws the plot on a Java 2D graphics device (such as the screen or a
	     * printer).  This method is called by the
	     * {@link org.jfree.chart.JFreeChart} class, you don't normally need
	     * to call it yourself.
	     *
	     * @param g2  the graphics device.
	     * @param plotArea  the area within which the plot should be drawn.
	     * @param anchor  the anchor point.
	     * @param parentState  the state from the parent plot, if there is one.
	     * @param info  collects info about the drawing
	     *              (<code>null</code> permitted).
	     */
	    @SuppressWarnings("unchecked")
		public void draw(Graphics2D g2, Rectangle2D plotArea, Point2D anchor,
	                     PlotState parentState,
	                     PlotRenderingInfo info) {

	        // adjust for insets...
	        RectangleInsets insets = getInsets();
	        insets.trim(plotArea);

	        Rectangle2D originalPlotArea = (Rectangle2D) plotArea.clone();
	        if (info != null) {
	            info.setPlotArea(plotArea);
	            info.setDataArea(plotArea);
	        }

	        drawBackground(g2, plotArea);

	        Shape savedClip = g2.getClip();
	        g2.clip(plotArea);

	        // adjust the plot area by the interior spacing value
	        double gapPercent = getInteriorGap();
	        double labelPercent = 0.0;
	        if (getLabelGenerator() != null&& !this.getSimpleLabels()) {
	            labelPercent = getLabelGap() + getMaximumLabelWidth();
	        }
	        double gapHorizontal = plotArea.getWidth() * (gapPercent
	                + labelPercent) * 2.0;
	        double gapVertical = plotArea.getHeight() * gapPercent * 2.0;

	        if (DEBUG_DRAW_INTERIOR) {
	            double hGap = plotArea.getWidth() * getInteriorGap();
	            double vGap = plotArea.getHeight() * getInteriorGap();
	            double igx1 = plotArea.getX() + hGap;
	            double igx2 = plotArea.getMaxX() - hGap;
	            double igy1 = plotArea.getY() + vGap;
	            double igy2 = plotArea.getMaxY() - vGap;
	            g2.setPaint(Color.lightGray);
	            g2.draw(new Rectangle2D.Double(igx1, igy1, igx2 - igx1,
	                    igy2 - igy1));
	        }

	        double linkX = plotArea.getX() + gapHorizontal / 2;
	        double linkY = plotArea.getY() + gapVertical / 2;
	        double linkW = plotArea.getWidth() - gapHorizontal;
	        double linkH = plotArea.getHeight() - gapVertical;

	        // make the link area a square if the pie chart is to be circular...
	        if (isCircular()) { // is circular?
	            double min = Math.min(linkW, linkH) / 2;
	            linkX = (linkX + linkX + linkW) / 2 - min;
	            linkY = (linkY + linkY + linkH) / 2 - min;
	            linkW = 2 * min;
	            linkH = 2 * min;
	        }

	        PiePlotState state = initialise(g2, plotArea, this, null, info);

	        // the link area defines the dog leg points for the linking lines to
	        // the labels
	        Rectangle2D linkAreaXX = new Rectangle2D.Double(linkX, linkY, linkW,
	                linkH * (1 - this.depthFactor));
	        state.setLinkArea(linkAreaXX);

	        if (DEBUG_DRAW_LINK_AREA) {
	            g2.setPaint(Color.blue);
	            g2.draw(linkAreaXX);
	            g2.setPaint(Color.yellow);
	            g2.draw(new Ellipse2D.Double(linkAreaXX.getX(), linkAreaXX.getY(),
	                    linkAreaXX.getWidth(), linkAreaXX.getHeight()));
	        }

	        // the explode area defines the max circle/ellipse for the exploded pie
	        // sections.
	        // it is defined by shrinking the linkArea by the linkMargin factor.
	        double hh = linkW * getLabelLinkMargin();
	        double vv = linkH * getLabelLinkMargin();
	        Rectangle2D explodeArea = new Rectangle2D.Double(linkX + hh / 2.0,
	                linkY + vv / 2.0, linkW - hh, linkH - vv);

	        state.setExplodedPieArea(explodeArea);

	        // the pie area defines the circle/ellipse for regular pie sections.
	        // it is defined by shrinking the explodeArea by the explodeMargin
	        // factor.
	        double maximumExplodePercent = getMaximumExplodePercent();
	        double percent = maximumExplodePercent / (1.0 + maximumExplodePercent);

	        double h1 = explodeArea.getWidth() * percent;
	        double v1 = explodeArea.getHeight() * percent;
	        Rectangle2D pieArea = new Rectangle2D.Double(explodeArea.getX()
	                + h1 / 2.0, explodeArea.getY() + v1 / 2.0,
	                explodeArea.getWidth() - h1, explodeArea.getHeight() - v1);

	        // the link area defines the dog-leg point for the linking lines to
	        // the labels
	        int depth = (int) (pieArea.getHeight() * this.depthFactor);
	        Rectangle2D linkArea = new Rectangle2D.Double(linkX, linkY, linkW,
	                linkH - depth);
	        state.setLinkArea(linkArea);

	        state.setPieArea(pieArea);
	        state.setPieCenterX(pieArea.getCenterX());
	        state.setPieCenterY(pieArea.getCenterY() - depth / 2.0);
	        state.setPieWRadius(pieArea.getWidth() / 2.0);
	        state.setPieHRadius((pieArea.getHeight() - depth) / 2.0);

	        // get the data source - return if null;
	        PieDataset dataset = getDataset();
	        if (DatasetUtilities.isEmptyOrNull(getDataset())) {
	            drawNoDataMessage(g2, plotArea);
	            g2.setClip(savedClip);
	            drawOutline(g2, plotArea);
	            return;
	        }

	        // if too any elements
	        if (dataset.getKeys().size() > plotArea.getWidth()) {
	            String text = "Too many elements";
	            Font sfont = new Font("dialog", Font.BOLD, 10);
	            g2.setFont(sfont);
	            FontMetrics fm = g2.getFontMetrics(sfont);
	            int stringWidth = fm.stringWidth(text);

	            g2.drawString(text, (int) (plotArea.getX() + (plotArea.getWidth()
	                    - stringWidth) / 2), (int) (plotArea.getY()
	                    + (plotArea.getHeight() / 2)));
	            return;
	        }
	        // if we are drawing a perfect circle, we need to readjust the top left
	        // coordinates of the drawing area for the arcs to arrive at this
	        // effect.
	        if (isCircular()) {
	            double min = Math.min(plotArea.getWidth(),
	                    plotArea.getHeight()) / 2;
	            plotArea = new Rectangle2D.Double(plotArea.getCenterX() - min,
	                    plotArea.getCenterY() - min, 2 * min, 2 * min);
	        }
	        // get a list of keys...
	        List sectionKeys = dataset.getKeys();

	        if (sectionKeys.size() == 0) {
	            return;
	        }

	        // establish the coordinates of the top left corner of the drawing area
	        double arcX = pieArea.getX();
	        double arcY = pieArea.getY();

	        //g2.clip(+);
	        Composite originalComposite = g2.getComposite();
	        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
	                getForegroundAlpha()));

	        double totalValue = DatasetUtilities.calculatePieDatasetTotal(dataset);
	        double runningTotal = 0;
	        if (depth < 0) {
	            return;  // if depth is negative don't draw anything
	        }

	        ArrayList arcList = new ArrayList();//这是用来画底部的圆
	        Arc2D.Double arc;
	        Paint paint;
	        Paint outlinePaint;
	        Stroke outlineStroke;

	        Iterator iterator = sectionKeys.iterator();
	        while (iterator.hasNext()) {

	            Comparable currentKey = (Comparable) iterator.next();
	            Number dataValue = dataset.getValue(currentKey);
	            if (dataValue == null) {
	                arcList.add(null);
	                continue;
	            }
	            double value = dataValue.doubleValue();
	            if (value <= 0) {
	                arcList.add(null);
	                continue;
	            }
	            double startAngle = getStartAngle();
	            double direction = getDirection().getFactor();
	            double angle1 = startAngle + (direction * (runningTotal * 360))
	                    / totalValue;
	            double angle2 = startAngle + (direction * (runningTotal + value)
	                    * 360) / totalValue;
	            if (Math.abs(angle2 - angle1) > getMinimumArcAngleToDraw()) {
	                arcList.add(new Arc2D.Double(arcX, arcY + depth,
	                        pieArea.getWidth(), pieArea.getHeight() - depth,
	                        angle1, angle2 - angle1, Arc2D.PIE));
	            }
	            else {
	                arcList.add(null);
	            }
	            runningTotal += value;
	        }

	        Shape oldClip = g2.getClip();

	        Ellipse2D top = new Ellipse2D.Double(pieArea.getX(), pieArea.getY(),
	                pieArea.getWidth(), pieArea.getHeight() - depth);

	        Ellipse2D bottom = new Ellipse2D.Double(pieArea.getX(), pieArea.getY()
	                + depth, pieArea.getWidth(), pieArea.getHeight() - depth);

	        Rectangle2D lower = new Rectangle2D.Double(top.getX(),
	                top.getCenterY(), pieArea.getWidth(), bottom.getMaxY()
	                - top.getCenterY());

	        Rectangle2D upper = new Rectangle2D.Double(pieArea.getX(), top.getY(),
	                pieArea.getWidth(), bottom.getCenterY() - top.getY());

	        Area a = new Area(top);
	        a.add(new Area(lower));
	        Area b = new Area(bottom);
	        b.add(new Area(upper));
	        Area pie = new Area(a);
	        pie.intersect(b);

	        Area front = new Area(pie);
	        front.subtract(new Area(top));

	        Area back = new Area(pie);
	        back.subtract(new Area(bottom));

	        //front back 是前后相交的部分
	        double sectionDepth = super.getSectionDepth()/2;
	        Ellipse2D top1 = new Ellipse2D.Double(
	        		top.getX()+sectionDepth*top.getWidth()/2,
	        		top.getY()+sectionDepth*top.getHeight()/2,
	                top.getWidth()-sectionDepth*top.getWidth(),
	                top.getHeight() - sectionDepth*top.getHeight());

	        Ellipse2D bottom1 = new Ellipse2D.Double(
	        		bottom.getX()+sectionDepth*bottom.getWidth()/2, 
	        		bottom.getY()+ sectionDepth*bottom.getHeight()/2,
	        		bottom.getWidth()-sectionDepth*bottom.getWidth(), 
	        		bottom.getHeight() -sectionDepth*bottom.getHeight());

	        Rectangle2D lower1 = new Rectangle2D.Double(
	        		top1.getX(),
	                top1.getCenterY(), 
	                top1.getWidth(),
	                bottom1.getMaxY()-  top1.getCenterY());

	        Rectangle2D upper1 = new Rectangle2D.Double(
	        		top1.getX(), 
	        		top1.getY(),
	        		top1.getWidth(),
	                bottom1.getCenterY()- top1.getY());

	        Area a1 = new Area(top1);
	        a1.add(new Area(lower1));
	        Area b1 = new Area(bottom1);
	        b1.add(new Area(upper1));
	        Area pie1 = new Area(a1);
	        pie1.intersect(b1);

	        Area front1 = new Area(pie1);
	        front1.subtract(new Area(top1));

	        Area back1 = new Area(pie1);
	        back1.subtract(new Area(bottom1));
	        
	        
	        // draw the bottom circle
	        int[] xs;
	        int[] ys;
	        arc = new Arc2D.Double(arcX, arcY + depth, pieArea.getWidth(),
	                pieArea.getHeight() - depth, 0, 360, Arc2D.PIE);

	        int categoryCount = arcList.size();
	        for (int categoryIndex = 0; categoryIndex < categoryCount;
	                 categoryIndex++) {
	            arc = (Arc2D.Double) arcList.get(categoryIndex);
	            if (arc == null) {
	                continue;
	            }
	            
	            //////
	            Area ringArc = new Area(arc);
	            // create the bounds for the inner arc
	            Rectangle2D innerArcBounds = new Rectangle2D.Double(
	            		arc.getMinX()+sectionDepth*arc.getWidth()/2,
	            		arc.getMinY()+sectionDepth*arc.getHeight()/2,
	            		arc.getWidth()-sectionDepth*arc.getWidth(),
	            		arc.getHeight()-sectionDepth*arc.getHeight());
	            Arc2D.Double inner = new Arc2D.Double(innerArcBounds,arc.getAngleStart(),arc.getAngleExtent(),Arc2D.PIE);
	            ringArc.subtract(new Area(inner));

	            //////
	            Comparable key = getSectionKey(categoryIndex);
	            paint = lookupSectionPaint(key);
	            outlinePaint = lookupSectionOutlinePaint(key);
	            outlineStroke = lookupSectionOutlineStroke(key);
	            g2.setPaint(paint);
	            g2.fill(ringArc);
	            g2.setPaint(outlinePaint);
	            g2.setStroke(outlineStroke);
	            //g2.draw(ringArc);
	            g2.setPaint(paint);

	            Point2D p1 = arc.getStartPoint();
	            Point2D p2 = inner.getStartPoint();
	            // draw the height
//	            xs = new int[] {(int) arc.getCenterX(), (int) arc.getCenterX(),
//	                    (int) p1.getX(), (int) p1.getX()};
//	            ys = new int[] {(int) arc.getCenterY(), (int) arc.getCenterY()
//	                    - depth, (int) p1.getY() - depth, (int) p1.getY()};
	            xs = new int[] {(int) p2.getX(), (int) p2.getX(),
	                    (int) p1.getX(), (int) p1.getX()};
	            ys = new int[] {(int) p2.getY(), (int) p2.getY()
	                    - depth, (int) p1.getY() - depth, (int) p1.getY()};
	            Polygon polygon = new Polygon(xs, ys, 4);
	            g2.setPaint(java.awt.Color.lightGray);
	            g2.fill(polygon);
	            g2.setPaint(outlinePaint);
	            g2.setStroke(outlineStroke);
	            //g2.draw(polygon);
	            g2.setPaint(paint);

	        }

	        g2.setPaint(Color.gray);
	        g2.fill(back1);
	        g2.fill(front);

	        // cycle through once drawing only the sides at the back...
	        int cat = 0;
	        iterator = arcList.iterator();
	        while (iterator.hasNext()) {
	            Arc2D segment = (Arc2D) iterator.next();
	            if (segment != null) {
	                Comparable key = getSectionKey(cat);
	                paint = lookupSectionPaint(key);
	                outlinePaint = lookupSectionOutlinePaint(key);
	                outlineStroke = lookupSectionOutlineStroke(key);
	                
		            Rectangle2D innerArcBounds = new Rectangle2D.Double(
		            		segment.getMinX()+sectionDepth*segment.getWidth()/2,
		            		segment.getMinY()+sectionDepth*segment.getHeight()/2,
		            		segment.getWidth()-sectionDepth*segment.getWidth(),
		            		segment.getHeight()-sectionDepth*segment.getHeight());
		            Arc2D.Double inner = new Arc2D.Double(innerArcBounds,segment.getAngleStart(),segment.getAngleExtent(),Arc2D.PIE);
	                
	                drawSide(g2, pieArea, inner, front, back1, paint,
	                        outlinePaint, outlineStroke, false, true);
	            }
	            cat++;
	        }

	        // cycle through again drawing only the sides at the front...
	        cat = 0;
	        iterator = arcList.iterator();
	        while (iterator.hasNext()) {
	            Arc2D segment = (Arc2D) iterator.next();
	            if (segment != null) {
	                Comparable key = getSectionKey(cat);
	                paint = lookupSectionPaint(key);
	                outlinePaint = lookupSectionOutlinePaint(key);
	                outlineStroke = lookupSectionOutlineStroke(key);
	                drawSide(g2, pieArea, segment, front, back, paint,
	                        outlinePaint, outlineStroke, true, false);
	            }
	            cat++;
	        }

	        g2.setClip(oldClip);

	        // draw the sections at the top of the pie (and set up tooltips)...
	        Arc2D upperArc;
	        for (int sectionIndex = 0; sectionIndex < categoryCount;
	                 sectionIndex++) {
	            arc = (Arc2D.Double) arcList.get(sectionIndex);
	            if (arc == null) {
	                continue;
	            }
	            upperArc = new Arc2D.Double(arcX, arcY, pieArea.getWidth(),
	                    pieArea.getHeight() - depth, arc.getAngleStart(),
	                    arc.getAngleExtent(), Arc2D.OPEN);

	            Arc2D upperArc1 = new Arc2D.Double(arcX, arcY, pieArea.getWidth(),
	                    pieArea.getHeight() - depth, arc.getAngleStart(),
	                    arc.getAngleExtent(), Arc2D.PIE);
	            
	            //////
	            Area upperRingArc = new Area(upperArc1);
	            // create the bounds for the inner arc
//	            RectangleInsets s = new RectangleInsets(UnitType.RELATIVE,sectionDepth, sectionDepth, sectionDepth, sectionDepth);
//	            Rectangle2D innerArcBounds = new Rectangle2D.Double();
//	            innerArcBounds.setRect(upperArc.getBounds2D());
//	            s.trim(innerArcBounds);
	                
	            Rectangle2D innerArcBounds = new Rectangle2D.Double(
	            		upperArc.getMinX()+sectionDepth*upperArc.getWidth()/2,
	            		upperArc.getMinY()+sectionDepth*upperArc.getHeight()/2,
	            		upperArc.getWidth()-sectionDepth*upperArc.getWidth(),
	            		upperArc.getHeight()-sectionDepth*upperArc.getHeight());
	            Arc2D.Double inner = new Arc2D.Double(innerArcBounds,upperArc.getAngleStart()+upperArc.getAngleExtent(),-upperArc.getAngleExtent(),Arc2D.OPEN);
	            Arc2D.Double inner1 = new Arc2D.Double(innerArcBounds,upperArc.getAngleStart(),upperArc.getAngleExtent(),Arc2D.PIE);
	            upperRingArc.subtract(new Area(inner1));
	            
	            Comparable currentKey = (Comparable) sectionKeys.get(sectionIndex);
	            paint = lookupSectionPaint(currentKey, true);
	            outlinePaint = lookupSectionOutlinePaint(currentKey);
	            outlineStroke = lookupSectionOutlineStroke(currentKey);
	            g2.setPaint(paint);
	            
	            GeneralPath path = new GeneralPath();
	            path.moveTo((float) upperArc.getStartPoint().getX(),
	                    (float) upperArc.getStartPoint().getY());
	            path.append(upperArc.getPathIterator(null), false);
	            path.append(inner.getPathIterator(null), true);
	            path.closePath();
	            
	            //GeneralPath path = new GeneralPath(upperRingArc);
	            g2.fill(path);
	            g2.setStroke(outlineStroke);
	            g2.setPaint(outlinePaint);
	            g2.draw(path);
	            //g2.draw(upperRingArc);

	           // add a tooltip for the section...
	            if (info != null) {
	                EntityCollection entities
	                        = info.getOwner().getEntityCollection();
	                if (entities != null) {
	                    String tip = null;
	                    PieToolTipGenerator tipster = getToolTipGenerator();
	                    if (tipster != null) {
	                        // @mgs: using the method's return value was missing
	                        tip = tipster.generateToolTip(dataset, currentKey);
	                    }
	                    String url = null;
	                    if (getURLGenerator() != null) {
	                        url = getURLGenerator().generateURL(dataset, currentKey,
	                                getPieIndex());
	                    }
	                    PieSectionEntity entity = new PieSectionEntity(
	                    		upperRingArc, dataset, getPieIndex(), sectionIndex,
	                            currentKey, tip, url);
	                    entities.add(entity);
	                }
	            }
	        }

	        List keys = dataset.getKeys();
	        Rectangle2D adjustedPlotArea = new Rectangle2D.Double(
	                originalPlotArea.getX(), originalPlotArea.getY(),
	                originalPlotArea.getWidth(), originalPlotArea.getHeight()
	                - depth);
	        if (getSimpleLabels()) {
	            drawSimpleLabels(g2, keys, totalValue, adjustedPlotArea,
	                    linkArea, state);
	        }
	        else {
	            drawLabels(g2, keys, totalValue, adjustedPlotArea, linkArea,
	                    state);
	        }

	        g2.setClip(savedClip);
	        g2.setComposite(originalComposite);
	        drawOutline(g2, originalPlotArea);

	    }

	    /**
	     * Draws the side of a pie section.
	     *
	     * @param g2  the graphics device.
	     * @param plotArea  the plot area.
	     * @param arc  the arc.
	     * @param front  the front of the pie.
	     * @param back  the back of the pie.
	     * @param paint  the color.
	     * @param outlinePaint  the outline paint.
	     * @param outlineStroke  the outline stroke.
	     * @param drawFront  draw the front?
	     * @param drawBack  draw the back?
	     */
	    protected void drawSide(Graphics2D g2,
	                            Rectangle2D plotArea,
	                            Arc2D arc,
	                            Area front,
	                            Area back,
	                            Paint paint,
	                            Paint outlinePaint,
	                            Stroke outlineStroke,
	                            boolean drawFront,
	                            boolean drawBack) {

	        if (getDarkerSides()) {
	            if (paint instanceof Color) {
	                Color c = (Color) paint;
	                c = c.darker();
	                paint = c;
	            }
	        }

	        double start = arc.getAngleStart();
	        double extent = arc.getAngleExtent();
	        double end = start + extent;

	        g2.setStroke(outlineStroke);

	        // for CLOCKWISE charts, the extent will be negative...
	        if (extent < 0.0) {

	            if (isAngleAtFront(start)) {  // start at front

	                if (!isAngleAtBack(end)) {

	                    if (extent > -180.0) {  // the segment is entirely at the
	                                            // front of the chart
	                        if (drawFront) {
	                            Area side = new Area(new Rectangle2D.Double(
	                                    arc.getEndPoint().getX(), plotArea.getY(),
	                                    arc.getStartPoint().getX()
	                                    - arc.getEndPoint().getX(),
	                                    plotArea.getHeight()));
	                            side.intersect(front);
	                            g2.setPaint(paint);
	                            g2.fill(side);
	                            g2.setPaint(outlinePaint);
	                            g2.draw(side);
	                        }
	                    }
	                    else {  // the segment starts at the front, and wraps all
	                            // the way around
	                            // the back and finishes at the front again
	                        Area side1 = new Area(new Rectangle2D.Double(
	                                plotArea.getX(), plotArea.getY(),
	                                arc.getStartPoint().getX() - plotArea.getX(),
	                                plotArea.getHeight()));
	                        side1.intersect(front);

	                        Area side2 = new Area(new Rectangle2D.Double(
	                                arc.getEndPoint().getX(), plotArea.getY(),
	                                plotArea.getMaxX() - arc.getEndPoint().getX(),
	                                plotArea.getHeight()));

	                        side2.intersect(front);
	                        g2.setPaint(paint);
	                        if (drawFront) {
	                            g2.fill(side1);
	                            g2.fill(side2);
	                        }

	                        if (drawBack) {
	                            g2.fill(back);
	                        }

	                        g2.setPaint(outlinePaint);
	                        if (drawFront) {
	                            g2.draw(side1);
	                            g2.draw(side2);
	                        }

	                        if (drawBack) {
	                            g2.draw(back);
	                        }

	                    }
	                }
	                else {  // starts at the front, finishes at the back (going
	                        // around the left side)

	                    if (drawBack) {
	                        Area side2 = new Area(new Rectangle2D.Double(
	                                plotArea.getX(), plotArea.getY(),
	                                arc.getEndPoint().getX() - plotArea.getX(),
	                                plotArea.getHeight()));
	                        side2.intersect(back);
	                        g2.setPaint(paint);
	                        g2.fill(side2);
	                        g2.setPaint(outlinePaint);
	                        g2.draw(side2);
	                    }

	                    if (drawFront) {
	                        Area side1 = new Area(new Rectangle2D.Double(
	                                plotArea.getX(), plotArea.getY(),
	                                arc.getStartPoint().getX() - plotArea.getX(),
	                                plotArea.getHeight()));
	                        side1.intersect(front);
	                        g2.setPaint(paint);
	                        g2.fill(side1);
	                        g2.setPaint(outlinePaint);
	                        g2.draw(side1);
	                    }
	                }
	            }
	            else {  // the segment starts at the back (still extending
	                    // CLOCKWISE)

	                if (!isAngleAtFront(end)) {
	                    if (extent > -180.0) {  // whole segment stays at the back
	                        if (drawBack) {
	                            Area side = new Area(new Rectangle2D.Double(
	                                    arc.getStartPoint().getX(), plotArea.getY(),
	                                    arc.getEndPoint().getX()
	                                    - arc.getStartPoint().getX(),
	                                    plotArea.getHeight()));
	                            side.intersect(back);
	                            g2.setPaint(paint);
	                            g2.fill(side);
	                            g2.setPaint(outlinePaint);
	                            g2.draw(side);
	                        }
	                    }
	                    else {  // starts at the back, wraps around front, and
	                            // finishes at back again
	                        Area side1 = new Area(new Rectangle2D.Double(
	                                arc.getStartPoint().getX(), plotArea.getY(),
	                                plotArea.getMaxX() - arc.getStartPoint().getX(),
	                                plotArea.getHeight()));
	                        side1.intersect(back);

	                        Area side2 = new Area(new Rectangle2D.Double(
	                                plotArea.getX(), plotArea.getY(),
	                                arc.getEndPoint().getX() - plotArea.getX(),
	                                plotArea.getHeight()));

	                        side2.intersect(back);

	                        g2.setPaint(paint);
	                        if (drawBack) {
	                            g2.fill(side1);
	                            g2.fill(side2);
	                        }

	                        if (drawFront) {
	                            g2.fill(front);
	                        }

	                        g2.setPaint(outlinePaint);
	                        if (drawBack) {
	                            g2.draw(side1);
	                            g2.draw(side2);
	                        }

	                        if (drawFront) {
	                            g2.draw(front);
	                        }

	                    }
	                }
	                else {  // starts at back, finishes at front (CLOCKWISE)

	                    if (drawBack) {
	                        Area side1 = new Area(new Rectangle2D.Double(
	                                arc.getStartPoint().getX(), plotArea.getY(),
	                                plotArea.getMaxX() - arc.getStartPoint().getX(),
	                                plotArea.getHeight()));
	                        side1.intersect(back);
	                        g2.setPaint(paint);
	                        g2.fill(side1);
	                        g2.setPaint(outlinePaint);
	                        g2.draw(side1);
	                    }

	                    if (drawFront) {
	                        Area side2 = new Area(new Rectangle2D.Double(
	                                arc.getEndPoint().getX(), plotArea.getY(),
	                                plotArea.getMaxX() - arc.getEndPoint().getX(),
	                                plotArea.getHeight()));
	                        side2.intersect(front);
	                        g2.setPaint(paint);
	                        g2.fill(side2);
	                        g2.setPaint(outlinePaint);
	                        g2.draw(side2);
	                    }

	                }
	            }
	        }
	        else if (extent > 0.0) {  // the pie sections are arranged ANTICLOCKWISE

	            if (isAngleAtFront(start)) {  // segment starts at the front

	                if (!isAngleAtBack(end)) {  // and finishes at the front

	                    if (extent < 180.0) {  // segment only occupies the front
	                        if (drawFront) {
	                            Area side = new Area(new Rectangle2D.Double(
	                                    arc.getStartPoint().getX(), plotArea.getY(),
	                                    arc.getEndPoint().getX()
	                                    - arc.getStartPoint().getX(),
	                                    plotArea.getHeight()));
	                            side.intersect(front);
	                            g2.setPaint(paint);
	                            g2.fill(side);
	                            g2.setPaint(outlinePaint);
	                            g2.draw(side);
	                        }
	                    }
	                    else {  // segments wraps right around the back...
	                        Area side1 = new Area(new Rectangle2D.Double(
	                                arc.getStartPoint().getX(), plotArea.getY(),
	                                plotArea.getMaxX() - arc.getStartPoint().getX(),
	                                plotArea.getHeight()));
	                        side1.intersect(front);

	                        Area side2 = new Area(new Rectangle2D.Double(
	                                plotArea.getX(), plotArea.getY(),
	                                arc.getEndPoint().getX() - plotArea.getX(),
	                                plotArea.getHeight()));
	                        side2.intersect(front);

	                        g2.setPaint(paint);
	                        if (drawFront) {
	                            g2.fill(side1);
	                            g2.fill(side2);
	                        }

	                        if (drawBack) {
	                            g2.fill(back);
	                        }

	                        g2.setPaint(outlinePaint);
	                        if (drawFront) {
	                            g2.draw(side1);
	                            g2.draw(side2);
	                        }

	                        if (drawBack) {
	                            g2.draw(back);
	                        }

	                    }
	                }
	                else {  // segments starts at front and finishes at back...
	                    if (drawBack) {
	                        Area side2 = new Area(new Rectangle2D.Double(
	                                arc.getEndPoint().getX(), plotArea.getY(),
	                                plotArea.getMaxX() - arc.getEndPoint().getX(),
	                                plotArea.getHeight()));
	                        side2.intersect(back);
	                        g2.setPaint(paint);
	                        g2.fill(side2);
	                        g2.setPaint(outlinePaint);
	                        g2.draw(side2);
	                    }

	                    if (drawFront) {
	                        Area side1 = new Area(new Rectangle2D.Double(
	                                arc.getStartPoint().getX(), plotArea.getY(),
	                                plotArea.getMaxX() - arc.getStartPoint().getX(),
	                                plotArea.getHeight()));
	                        side1.intersect(front);
	                        g2.setPaint(paint);
	                        g2.fill(side1);
	                        g2.setPaint(outlinePaint);
	                        g2.draw(side1);
	                    }
	                }
	            }
	            else {  // segment starts at back

	                if (!isAngleAtFront(end)) {
	                    if (extent < 180.0) {  // and finishes at back
	                        if (drawBack) {
	                            Area side = new Area(new Rectangle2D.Double(
	                                    arc.getEndPoint().getX(), plotArea.getY(),
	                                    arc.getStartPoint().getX()
	                                    - arc.getEndPoint().getX(),
	                                    plotArea.getHeight()));
	                            side.intersect(back);
	                            g2.setPaint(paint);
	                            g2.fill(side);
	                            g2.setPaint(outlinePaint);
	                            g2.draw(side);
	                        }
	                    }
	                    else {  // starts at back and wraps right around to the
	                            // back again
	                        Area side1 = new Area(new Rectangle2D.Double(
	                                arc.getStartPoint().getX(), plotArea.getY(),
	                                plotArea.getX() - arc.getStartPoint().getX(),
	                                plotArea.getHeight()));
	                        side1.intersect(back);

	                        Area side2 = new Area(new Rectangle2D.Double(
	                                arc.getEndPoint().getX(), plotArea.getY(),
	                                plotArea.getMaxX() - arc.getEndPoint().getX(),
	                                plotArea.getHeight()));
	                        side2.intersect(back);

	                        g2.setPaint(paint);
	                        if (drawBack) {
	                            g2.fill(side1);
	                            g2.fill(side2);
	                        }

	                        if (drawFront) {
	                            g2.fill(front);
	                        }

	                        g2.setPaint(outlinePaint);
	                        if (drawBack) {
	                            g2.draw(side1);
	                            g2.draw(side2);
	                        }

	                        if (drawFront) {
	                            g2.draw(front);
	                        }

	                    }
	                }
	                else {  // starts at the back and finishes at the front
	                        // (wrapping the left side)
	                    if (drawBack) {
	                        Area side1 = new Area(new Rectangle2D.Double(
	                                plotArea.getX(), plotArea.getY(),
	                                arc.getStartPoint().getX() - plotArea.getX(),
	                                plotArea.getHeight()));
	                        side1.intersect(back);
	                        g2.setPaint(paint);
	                        g2.fill(side1);
	                        g2.setPaint(outlinePaint);
	                        g2.draw(side1);
	                    }

	                    if (drawFront) {
	                        Area side2 = new Area(new Rectangle2D.Double(
	                                plotArea.getX(), plotArea.getY(),
	                                arc.getEndPoint().getX() - plotArea.getX(),
	                                plotArea.getHeight()));
	                        side2.intersect(front);
	                        g2.setPaint(paint);
	                        g2.fill(side2);
	                        g2.setPaint(outlinePaint);
	                        g2.draw(side2);
	                    }
	                }
	            }

	        }

	    }

	    /**
	     * Returns a short string describing the type of plot.
	     *
	     * @return <i>Pie 3D Plot</i>.
	     */
	    public String getPlotType() {
	        return localizationResources.getString("Pie_3D_Plot");
	    }

	    /**
	     * A utility method that returns true if the angle represents a point at
	     * the front of the 3D pie chart.  0 - 180 degrees is the back, 180 - 360
	     * is the front.
	     *
	     * @param angle  the angle.
	     *
	     * @return A boolean.
	     */
	    private boolean isAngleAtFront(double angle) {
	        return (Math.sin(Math.toRadians(angle)) < 0.0);
	    }

	    /**
	     * A utility method that returns true if the angle represents a point at
	     * the back of the 3D pie chart.  0 - 180 degrees is the back, 180 - 360
	     * is the front.
	     *
	     * @param angle  the angle.
	     *
	     * @return <code>true</code> if the angle is at the back of the pie.
	     */
	    private boolean isAngleAtBack(double angle) {
	        return (Math.sin(Math.toRadians(angle)) > 0.0);
	    }

	    /**
	     * Tests this plot for equality with an arbitrary object.
	     *
	     * @param obj  the object (<code>null</code> permitted).
	     *
	     * @return A boolean.
	     */
	    public boolean equals(Object obj) {
	        if (obj == this) {
	            return true;
	        }
	        if (!(obj instanceof PiePlot3D)) {
	            return false;
	        }
	        TRingPlot3D that = (TRingPlot3D) obj;
	        if (this.depthFactor != that.depthFactor) {
	            return false;
	        }
	        if (this.darkerSides != that.darkerSides) {
	            return false;
	        }
	        return super.equals(obj);
	    }


}
