package org.asciidoctor.extension

import geb.Browser
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriver.Window

/**
 * ...
 */
trait BrowserResizer {

    ScreenshotDimension resizeBrowserWindow(String dimension) {
        ScreenshotDimension dim = new ScreenshotDimension(dimension)

        WebDriver driver = Browser.drive({}).driver
        Window window = driver.manage().window()
        Dimension size = window.size

        int viewportWidth = driver.executeScript("return document.documentElement.clientWidth")
        int viewportHeight = driver.executeScript("return document.documentElement.clientHeight")

        int viewportDeltaX = size.width - viewportWidth
        int viewportDeltaY = size.height - viewportHeight

        window.size = new Dimension(dim.width + viewportDeltaX, dim.height + viewportDeltaY)

        return dim
    }

}
