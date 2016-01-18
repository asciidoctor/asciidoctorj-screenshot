/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Stephan Classen
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
import geb.navigator.Navigator
import geb.report.ScreenshotReporter

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static java.lang.Math.min

class ScreenshotTaker implements BrowserResizer {
    private static final Collection<Object> ALPHABET = ['A'..'Z', '0'..'9'].flatten()
    private static final int ALPHABET_SIZE = ALPHABET.size()

    private final File screenshotDir
    private final String dimension
    private final String url
    private final String selector
    private final String fileName

    ScreenshotTaker(File screenshotDir, Map<String, Object> attributes) {
        this.screenshotDir = screenshotDir
        this.dimension = attributes['dimension']
        this.url = attributes['url']
        this.selector = attributes['selector']

        String name = attributes['name']
        this.fileName = name ? name : uniqueName()
    }

    private static String uniqueName() {
        Random random = new Random()
        Collection<Object> randomChars = (1..32).collect { ALPHABET[random.nextInt(ALPHABET_SIZE)] }
        randomChars.join('')
    }

    File takeScreenshot() {
        ScreenshotDimension dim = resizeBrowserIfNecessary()
        crop(rawScreenshot(), dim)
    }

    private ScreenshotDimension resizeBrowserIfNecessary() {
        if (dimension) {
            resizeBrowserWindow(dimension)
        } else {
            new ScreenshotDimension(800, 600)
        }
    }

    private Navigator rawScreenshot() {
        Navigator cutElement = null
        Browser.drive {
            Browser browser = delegate as Browser
            browser.config.reporter = new ScreenshotReporter()
            browser.config.reportsDir = screenshotDir

            if (url) {
                go url
                waitFor(1) { true }
            }

            if (selector) {
                cutElement = $(selector)
            }

            report fileName
        }
        cutElement
    }

    private File crop(Navigator element, ScreenshotDimension dim) {
        final File imageFile = new File(screenshotDir, fileName + ".png")
        final BufferedImage img = ImageIO.read(imageFile)

        int x, y, w, h
        if (element) {
            x = element.x
            y = element.y
            w = element.width
            h = element.height

        } else {
            x = 0
            y = 0
            w = dim.width
            h = dim.height
        }

        w = min(w, img.width - x)
        h = min(h, img.height - y)
        ImageIO.write(img.getSubimage(x, y, w, h), "png", imageFile)

        imageFile
    }
}
