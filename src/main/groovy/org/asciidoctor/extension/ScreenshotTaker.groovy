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
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static java.lang.Math.min

class ScreenshotTaker implements BrowserResizer {
    private static final Collection<Object> ALPHABET = ['A'..'Z', '0'..'9'].flatten()
    private static final int ALPHABET_SIZE = ALPHABET.size()

    private final File screenshotDir
    private final String dimension
    private final String frame
    private final String url
    private final String selector
    private final String fileName
    private final File imageFile

    ScreenshotTaker(File screenshotDir, Map<String, Object> attributes) {
        this.screenshotDir = screenshotDir
        this.dimension = attributes['dimension']
        this.frame = attributes['frame']
        this.url = attributes['url']
        this.selector = attributes['selector']

        String name = attributes['name']
        this.fileName = name ? name : uniqueName()
        this.imageFile = new File(screenshotDir, fileName + ".png")
    }

    private static String uniqueName() {
        Random random = new Random()
        Collection<Object> randomChars = (1..32).collect { ALPHABET[random.nextInt(ALPHABET_SIZE)] }
        randomChars.join('')
    }

    File takeScreenshot() {
        ScreenshotDimension dim = resizeBrowserIfNecessary()

        BufferedImage rawScreenshot = rawScreenshot()
        BufferedImage croppedImage = crop(rawScreenshot, dim)
        BufferedImage framedImage = frame(croppedImage)

        ImageIO.write(framedImage, "png", imageFile)
        imageFile
    }

    private ScreenshotDimension resizeBrowserIfNecessary() {
        if (frame) {
            resizeBrowserWindow(frame)
        } else if (dimension) {
            resizeBrowserWindow(dimension)
        } else {
            new ScreenshotDimension(800, 600)
        }
    }

    private BufferedImage rawScreenshot() {
        Browser.drive {
            Browser browser = delegate as Browser
            browser.config.reporter = new ScreenshotReporter()
            browser.config.reportsDir = screenshotDir

            if (url) {
                go url
                waitFor(1) { true }
            }

            report fileName
        }

        ImageIO.read(imageFile)
    }

    private BufferedImage crop(BufferedImage img, ScreenshotDimension dim) {
        int x, y, w, h

        if (selector) {
            Navigator element = null
            Browser.drive {
                element = $(selector)
            }

            if (element == null) {
                throw new IllegalArgumentException("Selector '$selector' did not match any content in the page")
            }

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

        img.getSubimage(x, y, w, h)
    }

    private BufferedImage frame(BufferedImage img) {
        if (frame) {
            Frame f = Frame.valueOf(frame)

            BufferedImage result = f.frameImage

            // drawing is asynchronous - wait until the drawing is completed
            CountDownLatch latch = new CountDownLatch(1)
            boolean imageComplete = result.graphics.drawImage(img, f.xOffset, f.yOffset, new ImageCompleteWaiter(latch))
            if (! imageComplete) {
                latch.await(10, TimeUnit.SECONDS)
            }

            result
        } else {
            img
        }
    }

    private static class ImageCompleteWaiter implements ImageObserver {

        private final CountDownLatch latch

        ImageCompleteWaiter(CountDownLatch latch) {
            this.latch = latch
        }

        @Override
        boolean imageUpdate(Image observedImg, int flags, int x, int y, int width, int height) {
            if (flags & ALLBITS) {
                latch.countDown()
                false
            } else {
                true
            }
        }
    }
}
