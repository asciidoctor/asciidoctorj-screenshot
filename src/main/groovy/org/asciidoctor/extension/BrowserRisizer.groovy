package org.asciidoctor.extension

import geb.Browser
import org.openqa.selenium.Dimension

/**
 * ...
 */
trait BrowserRisizer {

    ScreenshotDimension resizBrowserWindow(String dimension) {
        ScreenshotDimension dim = new ScreenshotDimension(dimension)

        def driver = Browser.drive({}).driver
        def window = driver.manage().window()
        def size = window.size

        int viewportdeltax = driver.executeScript("return document.documentElement.clientWidth") - size.width
        int viewportdeltay = 80//driver.executeScript("return document.documentElement.clientHeight")-size.height
        window.size = new Dimension(dim.width - viewportdeltax, dim.height + viewportdeltay)

        return dim
    }

}
