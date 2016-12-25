package com.smotricz.pinger;

import com.jdotsoft.jarloader.JarClassLoader;

public class Launcher {

	public static void main(String[] args) {
        JarClassLoader jcl = new JarClassLoader();
        try {
            jcl.invokeMain("com.smotricz.pinger.Main", args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    } // main()	}

}
