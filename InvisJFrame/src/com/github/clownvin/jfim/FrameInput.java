package com.github.clownvin.jfim;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import com.clown.math.Point2D;

public final class FrameInput implements NativeMouseInputListener {

	public static BufferedImage captureScreen(int x, int y, int width, int height) {
		Robot robot;
		try {
			robot = new Robot();
			return robot.createScreenCapture(new Rectangle(x, y, width, height));
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Point2D[] getVideoBounds(BufferedImage image) {
		Point2D[] points = new Point2D[4];
		int ptr = 0;
		// G will always be the first one found.
		int startX = 0, width = 0, startY = 0, height = 0;
		for (int x = 0; x < image.getWidth() && ptr < 4; x++) {
			y: for (int y = 0; y < image.getHeight() && ptr < 4; y++) {
				int color = image.getRGB(x, y);
				int red = color >> 16 & 0xFF;
				int green = color >> 8 & 0xFF;
				int blue = color & 0xFF;
				if (green > 0xC8 && red < 50 && blue < 50) {
					// We've found green.
					startX = x;
					startY = y;
					int x2 = 1;
					while ((image.getRGB(x + (x2++), y) >> 8 & 0xFF) > 0xC8) {
						// Getting x2 to a location that's not green. Next color
						// on X should be red.
					}
					for (int i = 1; i < 3; i++) {
						color = image.getRGB(x + (x2 + i), y);
						red = color >> 16 & 0xFF;
						green = color >> 8 & 0xFF;
						blue = color & 0xFF;
						if (red > 0xC8 && green < 0x32 && blue < 0x32) {
							// We've found red.
							width = x2;
							break;
						} else if (i == 2)
							break y;
					}
					int y2 = 1;
					while ((image.getRGB(x, y + (y2++)) >> 8 & 0xFF) > 0xC8) {
						// Getting y2 to a locations that's not green. Next
						// color on Y should be blue;
					}
					for (int i = 1; i < 3; i++) {
						color = image.getRGB(x, y + (y2 + i));
						red = color >> 16 & 0xFF;
						green = color >> 8 & 0xFF;
						blue = color & 0xFF;
						if (blue > 0xC8 && green < 0x32 && red < 0x32) {
							height = y2;
							break;
						} else if (i == 2)
							break y;
					}
					y = startY + (height * 4);
					points[ptr] = new Point2D((ptr == 0 || ptr == 1) ? startX : (startX + (width * 2)),
							(ptr == 0 || ptr == 2) ? startY : (startY + (height * 2)));
					ptr++;
				}
			}
			x += 20;
		}
		return points;
	}

	public static void main(String[] args) {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}
		System.err.close();
		FrameInput input = new FrameInput();
		GlobalScreen.addNativeMouseListener(input);
		Thread lolthread = new Thread() {
			@Override
			public void run() {
				while (true) {
					boolean notnull = true;
					int smallestX = Integer.MAX_VALUE;
					int smallestY = Integer.MAX_VALUE;
					int largestX = Integer.MIN_VALUE;
					int largestY = Integer.MIN_VALUE;
					for (Point2D point : getVideoBounds(captureScreen(0, 0, 1600, 900))) {
						if (point == null) {
							notnull = false;
							break;
						}
						if (point.getX() < smallestX) {
							smallestX = point.getX();
						} else if (point.getX() > largestX) {
							largestX = point.getX();
						}
						if (point.getY() < smallestY) {
							smallestY = point.getY();
						} else if (point.getY() > largestY) {
							largestY = point.getY();
						}
					}
					if (notnull) {
						System.out.println("Got em, boss: " + smallestX + ", " + smallestY + ", " + (largestX) + ", "
								+ (largestY));
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		lolthread.start();

	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nativeMousePressed(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent arg0) {
		System.out.println(arg0.getX() + ", " + arg0.getY());

	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
