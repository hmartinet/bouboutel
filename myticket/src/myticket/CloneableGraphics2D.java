package myticket;

import java.awt.*;
import java.awt.font.*;
/*
 * CloneableGraphics2D.java
 *
 * Created on November 2, 2005, 9:40 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/**
 *
 * @author sdperret
 */
/*public abstract class CloneableGraphics2D extends Graphics2D implements Cloneable{
    
    public CloneableGraphics2D() {
        super();
    }
    
    public CloneableGraphics2D clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public abstract FontRenderContext getFontRenderContext();
    
}*/






import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * This extension of the <tt>java.awt.Graphics2D</tt> abstract class
 * is still abstract, but it implements a lot of the <tt>Graphics2D</tt>
 * method in a way that concrete implementations can reuse.
 *
 * This class uses a <tt>GraphicContext</tt> to store the state of
 * its various attributes that control the rendering, such as the
 * current <tt>Font</tt>, <tt>Paint</tt> or clip.
 *
 * Concrete implementations can focus on implementing the rendering
 * methods, such as <tt>drawShape</tt>. As a convenience, rendering
 * methods that can be expressed with other rendering methods (e.g.,
 * <tt>drawRect</tt> can be expressed as <tt>draw(new Rectangle(..))</tt>),
 * are implemented by <tt>AbstractGraphics2D</tt>
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id: AbstractGraphics2D.java,v 1.10 2005/04/02 12:58:17 deweese Exp $
 * @see org.apache.batik.ext.awt.g2d.GraphicContext
 */
public abstract class CloneableGraphics2D extends Graphics2D implements Cloneable {
    /**
     * Current state of the Graphic Context. The GraphicsContext
     * class manages the state of this <tt>Graphics2D</tt> graphic context
     * attributes.
     */
    //protected GraphicContext gc;


    public CloneableGraphics2D clone() throws CloneNotSupportedException{
         return (CloneableGraphics2D)super.clone();
    }
    

    
}
