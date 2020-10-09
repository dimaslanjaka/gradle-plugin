package com.dimaslanjaka.gradle.Utils;

import org.junit.Test;

public class AnsiTest {
    @Test
    public void run() {
        String msg = Ansi.Yellow.and(Ansi.BgBlue).format("Hello %s", "name");
        String bli = Ansi.Blink.colorize("BOOM!");
        System.out.println(bli);
    }

    void warn(Object msg) {

    }
}
