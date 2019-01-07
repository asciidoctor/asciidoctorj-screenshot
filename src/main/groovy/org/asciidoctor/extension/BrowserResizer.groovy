/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Stephan Classen, Markus Schlichting
 * Copyright (c) 2014 François-Xavier Thoorens
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.asciidoctor.extension

import geb.Browser
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriver.Window

/**
 * Trait which allows to resize the browser window. This is handy to trigger css media queries.
 */
trait BrowserResizer {

    ScreenshotDimension resizeBrowserWindow(String dimension) {
        ScreenshotDimension dim = new ScreenshotDimension(dimension)

        WebDriver driver = Browser.drive({}).driver
        Window window = driver.manage().window()
        Dimension size = window.size

        int viewportWidth = driver.executeScript("return document.documentElement.clientWidth")
        int viewportHeight = driver.executeScript("return document.documentElement.clientHeight")

        int viewportDeltaX = (size.width - viewportWidth) + 25
        int viewportDeltaY = (size.height - viewportHeight) + 25

        int newWidth = dim.width + viewportDeltaX
        int newHeight = dim.height + viewportDeltaY

        window.size = new Dimension(newWidth, newHeight)

        return dim
    }

}
