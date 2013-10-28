package ui;

import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class InputAccessor implements TweenAccessor<Text> {

	public static final TweenManager manager = new TweenManager();
	public static final int UPDATE_PERIOD = 1000/60;
	
	static {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(UPDATE_PERIOD);
					} catch (Exception e) {
						System.out.println("something broke");
					}
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							manager.update(1);
						}
					});
				}
			}
		}).start();
	}

	@Override
	public int getValues(Text target, int tweenType, float[] returnValues) {
		Point p = target.getLocation();
		returnValues[0] = p.y;
//		System.out.println("current height " + returnValues[0]);
		return 1;
	}

	@Override
	public void setValues(Text target, int tweenType, float[] newValues) {
		Point point = target.getLocation();
		target.setLocation(point.x, (int) newValues[0]);
//		System.out.println("height set to " + newValues[0]);
	}

}
