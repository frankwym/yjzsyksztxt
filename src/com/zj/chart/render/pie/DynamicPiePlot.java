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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlotState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.SortOrder;
import org.jfree.util.UnitType;

@SuppressWarnings("serial")
public class DynamicPiePlot extends TPiePlot implements Cloneable, Serializable{
	

	/**
     * Creates a new plot.  The dataset is initially set to <code>null</code>.
     */
    public DynamicPiePlot() {
        this(null);
    }

    /**
     * Creates a plot that will draw a pie chart for the specified dataset.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public DynamicPiePlot(PieDataset dataset) {
    	super(dataset);
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     * @param anchor  the anchor point (<code>null</code> permitted).
     * @param parentState  the state from the parent plot, if there is one.
     * @param info  collects info about the drawing
     *              (<code>null</code> permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
                     PlotState parentState, PlotRenderingInfo info) {

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
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                getForegroundAlpha()));

        if (!DatasetUtilities.isEmptyOrNull(this.dataset)) {
            drawPie(g2, area, info);
        }
        else {
            drawNoDataMessage(g2, area);
        }

        g2.setClip(savedClip);
        g2.setComposite(originalComposite);

        drawOutline(g2, area);

    }
    
    /**
     * Draws the pie.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param info  chart rendering info.
     */
    @SuppressWarnings("unchecked")
	protected void drawPie(Graphics2D g2, Rectangle2D plotArea,
			PlotRenderingInfo info)
	{

		PiePlotState state = initialise(g2, plotArea, this, null, info);

		// adjust the plot area for interior spacing and labels...
		double labelReserve = 0.0;
		if(this.getLabelGenerator() != null && !this.simpleLabels)
		{
			labelReserve = this.getLabelGap() + this.getMaximumLabelWidth();
		}
		double gapHorizontal = plotArea.getWidth()
				* (this.getInteriorGap() + labelReserve) * 2.0;
		double gapVertical = plotArea.getHeight() * this.getInteriorGap() * 2.0;

		if(DEBUG_DRAW_INTERIOR)
		{
			double hGap = plotArea.getWidth() * this.getInteriorGap();
			double vGap = plotArea.getHeight() * this.getInteriorGap();

			double igx1 = plotArea.getX() + hGap;
			double igx2 = plotArea.getMaxX() - hGap;
			double igy1 = plotArea.getY() + vGap;
			double igy2 = plotArea.getMaxY() - vGap;
			g2.setPaint(Color.gray);
			g2
					.draw(new Rectangle2D.Double(igx1, igy1, igx2 - igx1, igy2
							- igy1));
		}

		double linkX = plotArea.getX() + gapHorizontal / 2;
		double linkY = plotArea.getY() + gapVertical / 2;
		double linkW = plotArea.getWidth() - gapHorizontal;
		double linkH = plotArea.getHeight() - gapVertical;

		// make the link area a square if the pie chart is to be circular...
		if(this.isCircular())
		{
			double min = Math.min(linkW, linkH) / 2;
			linkX = (linkX + linkX + linkW) / 2 - min;
			linkY = (linkY + linkY + linkH) / 2 - min;
			linkW = 2 * min;
			linkH = 2 * min;
		}

		// the link area defines the dog leg points for the linking lines to
		// the labels
		Rectangle2D linkArea = new Rectangle2D.Double(linkX, linkY, linkW,
				linkH);
		state.setLinkArea(linkArea);

		if(DEBUG_DRAW_LINK_AREA)
		{
			g2.setPaint(Color.blue);
			g2.draw(linkArea);
			g2.setPaint(Color.yellow);
			g2.draw(new Ellipse2D.Double(linkArea.getX(), linkArea.getY(),
					linkArea.getWidth(), linkArea.getHeight()));
		}

		// the explode area defines the max circle/ellipse for the exploded
		// pie sections.  it is defined by shrinking the linkArea by the
		// linkMargin factor.
		double lm = 0.0;
		if(!this.simpleLabels)
		{
			lm = this.getLabelLinkMargin();
		}
		double hh = linkArea.getWidth() * lm * 2.0;
		double vv = linkArea.getHeight() * lm * 2.0;
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
		Rectangle2D pieArea = new Rectangle2D.Double(explodeArea.getX() + h1
				/ 2.0, explodeArea.getY() + v1 / 2.0, explodeArea.getWidth()
				- h1, explodeArea.getHeight() - v1);

		if(DEBUG_DRAW_PIE_AREA)
		{
			g2.setPaint(Color.green);
			g2.draw(pieArea);
		}
		state.setPieArea(pieArea);
		state.setPieCenterX(pieArea.getCenterX());
		state.setPieCenterY(pieArea.getCenterY());
		state.setPieWRadius(pieArea.getWidth() / 2.0);
		state.setPieHRadius(pieArea.getHeight() / 2.0);

		// plot the data (unless the dataset is null)...
		if((this.dataset != null) && (this.dataset.getKeys().size() > 0))
		{

			List keys = this.dataset.getKeys();
			double totalValue = DatasetUtilities
					.calculatePieDatasetTotal(this.dataset);
			
			Rectangle2D[] labelArea = new Rectangle2D[keys.size()];
			DefaultPieDataset mDataset = (DefaultPieDataset)dataRange(this.dataset);
			double maxValue = pieArea.getHeight() / 2.0;
			
			int passesRequired = state.getPassesRequired();
			for (int pass = 0; pass < passesRequired; pass++)
			{
				for (int section = 0; section < keys.size(); section++)
				{
					Number n = mDataset.getValue(section);
					if(n != null)
					{
						double value = n.doubleValue();
						if(value > 0.0)
						{
							Rectangle2D areaRectangle2D;
							value = n.doubleValue()*pieArea.getHeight()/(mDataset.getValue(0).doubleValue());
							if (value == maxValue * 2.0)
							{
								areaRectangle2D = new Rectangle2D.Double(
										state.getPieCenterX() - value / 2.0, 
										state.getPieCenterY() - value / 2.0, value, value);
							}
							else 
							{
								areaRectangle2D = new Rectangle2D.Double(
										state.getPieCenterX() - value / 2.0, 
										state.getPieCenterY() - value / 2.0 + (maxValue - value/2.0), value, value);
							}
							state.setPieArea(areaRectangle2D);
							labelArea[section] = areaRectangle2D;
							
							drawItem(g2, section, explodeArea, state, pass);
						}
					}
				}
			}
			if(this.simpleLabels)
			{
				drawSimpleLabels(g2, keys, totalValue, plotArea, labelArea, state);
			}
			else
			{
				drawLabels(g2, keys, totalValue, plotArea, linkArea, state);
			}

		}
		else
		{
			drawNoDataMessage(g2, plotArea);
		}
	}
    
    /**
     * Draws a single data item.
     *
     * @param g2  the graphics device (<code>null</code> not permitted).
     * @param section  the section index.
     * @param dataArea  the data plot area.
     * @param state  state information for one chart.
     * @param currentPass  the current pass index.
     */
	@SuppressWarnings({ "deprecation", "unchecked" })
	protected void drawItem(Graphics2D g2, int section, Rectangle2D dataArea,
			PiePlotState state, int currentPass)
	{
		double angle = this.getStartAngle();
		double ep = 0.0;
		double mep = getMaximumExplodePercent();
		if(mep > 0.0)
		{
			ep = getExplodePercent(section) / mep;
		}
		Rectangle2D arcBounds = getArcBounds(state.getPieArea(), state
				.getExplodedPieArea(), angle, 360.0, ep);
		Arc2D.Double arc = new Arc2D.Double(arcBounds, angle, 360.0, Arc2D.OPEN);

		if(currentPass == 0)
		{
			if(this.getShadowPaint() != null)
			{
				Shape shadowArc = ShapeUtilities.createTranslatedShape(arc,
						(float)this.getShadowXOffset(), (float)this.getShadowYOffset());
				g2.setPaint(this.getShadowPaint());
				g2.fill(shadowArc);
			}
		}
		else
			if(currentPass == 1)
			{
				Comparable key = getSectionKey(section);
				Paint paint = lookupSectionPaint(key);
				g2.setPaint(paint);
				g2.fill(arc);

				Paint outlinePaint = lookupSectionOutlinePaint(key);
				Stroke outlineStroke = lookupSectionOutlineStroke(key);
				if(this.isSectionOutlinesVisible())
				{
					g2.setPaint(outlinePaint);
					g2.setStroke(outlineStroke);
					g2.draw(arc);
				}

				// update the linking line target for later
				// add an entity for the pie section
				if(state.getInfo() != null)
				{
					EntityCollection entities = state.getEntityCollection();
					if(entities != null)
					{
						String tip = null;
						if(this.getToolTipGenerator() != null)
						{
							tip = this.getToolTipGenerator().generateToolTip(
									this.dataset, key);
						}
						String url = null;
						if(this.getUrlGenerator() != null)
						{
							url = this.getUrlGenerator().generateURL(this.dataset,
									key, this.getPieIndex());
						}
						PieSectionEntity entity = new PieSectionEntity(arc,
								this.dataset, this.getPieIndex(), section, key, tip,
								url);
						entities.add(entity);
					}
				}
			}
		state.setLatestAngle(angle);
	}

	private PieDataset dataRange(PieDataset dataset)
	{
		DefaultPieDataset mDataset = (DefaultPieDataset)dataset;
		mDataset.sortByValues(SortOrder.DESCENDING);
		return mDataset;
	}
    /**
     * Draws the pie section labels in the simple form.
     *
     * @param g2  the graphics device.
     * @param keys  the section keys.
     * @param totalValue  the total value for all sections in the pie.
     * @param plotArea  the plot area.
     * @param pieArea  the area containing the pie.
     * @param state  the plot state.
     *
     * @since 1.0.7
     */
    @SuppressWarnings("unchecked")
	protected void drawSimpleLabels(Graphics2D g2, List keys,
            double totalValue, Rectangle2D plotArea, Rectangle2D[] pieArea,
            PiePlotState state) {

        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                1.0f));

        RectangleInsets labelInsets = new RectangleInsets(UnitType.RELATIVE,
                0.10, 0.10, 0.10, 0.10);
        
        Iterator iterator = keys.iterator();
        for (int i = 0; i < keys.size(); i++) {
            Comparable key = (Comparable) iterator.next();
            
            Rectangle2D labelsArea = labelInsets.createInsetRectangle(pieArea[i]);
            
            boolean include = true;
            double v = 0.0;
            Number n = getDataset().getValue(key);
            if (n == null) {
                include = !getIgnoreNullValues();
            }
            else {
                v = n.doubleValue();
                include = getIgnoreZeroValues() ? v > 0.0 : v >= 0.0;
            }

            if (include) {      
                int x = (int)labelsArea.getCenterX();
                int y = (int)labelsArea.getMinY();

                PieSectionLabelGenerator labelGenerator = getLabelGenerator();
                if (labelGenerator == null) {
                    continue;
                }
                String label = labelGenerator.generateSectionLabel(
                        this.dataset, key);
                if (label == null) {
                    continue;
                }
                g2.setFont(this.getLabelFont());
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D bounds = TextUtilities.getTextBounds(label, g2, fm);
                Rectangle2D out = this.getLabelPadding().createOutsetRectangle(
                        bounds);
                Shape bg = ShapeUtilities.createTranslatedShape(out,
                        x - bounds.getCenterX(), y - bounds.getCenterY());
                if (this.getLabelShadowPaint() != null) {
                    Shape shadow = ShapeUtilities.createTranslatedShape(bg,
                            this.getShadowXOffset(), this.getShadowYOffset());
                    g2.setPaint(this.getLabelShadowPaint());
                    g2.fill(shadow);
                }
                if (this.getLabelBackgroundPaint() != null) {
                    g2.setPaint(this.getLabelBackgroundPaint());
                    g2.fill(bg);
                }
                if (this.getLabelOutlinePaint() != null
                        && this.getLabelOutlineStroke() != null) {
                    g2.setPaint(this.getLabelOutlinePaint());
                    g2.setStroke(this.getLabelOutlineStroke());
                    g2.draw(bg);
                }

                g2.setPaint(this.getLabelPaint());
                g2.setFont(this.getLabelFont());
                TextUtilities.drawAlignedString(getLabelGenerator()
                        .generateSectionLabel(getDataset(), key), g2, x, y,
                        TextAnchor.CENTER);

            }
        }
        g2.setComposite(originalComposite);
    }
    /**
     * Returns a short string describing the type of plot.
     *
     * @return The plot type.
     */
    public String getPlotType() {
        return localizationResources.getString("TPartPie_Plot");
    }
}
