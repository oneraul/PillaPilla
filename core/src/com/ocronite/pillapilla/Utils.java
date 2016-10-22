package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.math.Vector2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

class Utils {
	
	static boolean isPointInCircle(float pX, float pY, float centerX, float centerY, float radius) {
		return Vector2.dst2(pX, pY, centerX, centerY) <= radius*radius;
	}
	
	static boolean intersectionCircleCircle(float AcenterX, float AcenterY, float Aradius, float BcenterX, float BcenterY, float Bradius) {
		return Vector2.dst2(AcenterX, AcenterY, BcenterX, BcenterY) <= (Aradius*Aradius)+(Bradius*Bradius);
	}

    /**
     * https://www.reddit.com/r/gamedev/comments/4xkx71/sigmoidlike_interpolation/
     * https://www.desmos.com/calculator/3zhzwbfrxd
     *      p and s control the curve.
     *      returns f(x) for the given curve, where both x and f(x) are [0, 1].
     *      f(x) can be used to interpolate following the curve.
     */
	static float sigmoidInterpolation(float x, float p, float s) {
		float c = (2 / (1-s)) - 1;
		if (x <= p) {
			return (float)(Math.pow(x, c) / Math.pow(p, c-1));
		} else {
			return (float)(1 - (Math.pow(1-x, c) / Math.pow(1-p, c-1)));
		}
	}

    /** http://stackoverflow.com/a/22940952 */
    static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    /** http://stackoverflow.com/a/14541376 */
    static String getPublicIp() {

        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
                String ip = in.readLine();
                return ip;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** http://stackoverflow.com/a/21299229 */
    static boolean isValidIP4Address(String ipAddress) {
        if (ipAddress.matches("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$")) {
            String[] groups = ipAddress.split("\\.");

            for (int i = 0; i <= 3; i++) {
                String segment = groups[i];
                if (segment == null || segment.length() <= 0) {
                    return false;
                }

                int value = 0;
                try {
                    value = Integer.parseInt(segment);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (value > 255) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    enum Digits10 {
        uno, dos, tres, cuatro, cinco, seis, siete, ocho, nueve, diez;

        public int value() {
            return ordinal() + 1;
        }

        @Override
        public String toString() {
            return "" + value();
        }
    }
}
