
package dk.aau.cs.idq.ptree;

import org.khelekore.prtree.NodeFilter;

import dk.aau.cs.idq.indoorentities.Rect;


/**
 * AcceptAllFilter Filter for R-Tree Queries Processing
 * 
 * 
 * @author lihuan
 * @version 0.1 / 2014.10.06
 * @see org.khelekore.prtree.NodeFilter
 *
 */
public class AcceptAllFilter implements NodeFilter<Rect> {

	/* (non-Javadoc)
	 * @see org.khelekore.prtree.NodeFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(Rect rect) {
		// TODO Auto-generated method stub
		return true;
	}

}
