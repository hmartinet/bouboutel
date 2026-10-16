package ch.poudriere.bouboutel.ui.components;

import java.awt.Graphics2D;

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


    @Override
    public CloneableGraphics2D clone() throws CloneNotSupportedException{
         return (CloneableGraphics2D)super.clone();
    }
}
