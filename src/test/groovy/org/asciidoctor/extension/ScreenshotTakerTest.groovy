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

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.security.MessageDigest

/**
 * ...
 */
class ScreenshotTakerTest extends Specification {

    public static final String NAME = 'screeeny'

    @Rule
    TemporaryFolder tmpFolder = new TemporaryFolder()

    private String url = getClass().classLoader.getResource("sample.html").toString()
    private File outputDir

    void setup() {
        outputDir = tmpFolder.newFolder()
    }

    def 'screenshot is placed into output directory'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url': url])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          screenshot.exists()
          screenshot.parentFile.absolutePath == outputDir.absolutePath
    }

    def 'screenshot is has given name'() {
        given:

          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url': url, 'name': NAME])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          screenshot.exists()
          screenshot.name == NAME + '.png'
    }

    def 'screenshot without dimension is equal to the expected 800x600'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url': url])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          getChecksum(screenshot) == getChecksum('screenshot_800x600.png')
    }

    def 'screenshot with dimension 800x600 is equal to the expected 800x600'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url': url, 'dimension': '800x600'])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          getChecksum(screenshot) == getChecksum('screenshot_800x600.png')
    }

    def 'screenshot with dimension Samsung S4 is equal to the expected 370x657'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url': url, 'dimension': 'SAMSUNG_S4'])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          getChecksum(screenshot) == getChecksum('screenshot_samsungS4.png')
    }


    def 'screenshot of circle is equal to the expected'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, [
                  'url'      : url,
                  'dimension': '800x600',
                  'selector' : '.circle'
          ])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          getChecksum(screenshot) == getChecksum('screenshot_circle.png')
    }

    def 'screenshot of circle with dimension Smasung S4 is equal to the expected'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, [
                  'url'      : url,
                  'dimension': 'SAMSUNG_S4',
                  'selector' : '.circle'
          ])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          getChecksum(screenshot) == getChecksum('screenshot_circle_samsungS4.png')
    }

    def 'screenshot of with frame Smasung S4 is equal to the expected'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, [
                  'url'      : url,
                  'frame': 'SAMSUNG_S4'
          ])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          screenshot.exists()
          // TODO: getChecksum(screenshot) == getChecksum('screenshot_frame_samsungS4.png')
    }


    private byte[] getChecksum(String expected) {
        File f = getClass().classLoader.getResource(expected).file as File
        getChecksum(f)
    }

    private static byte[] getChecksum(file) {
        MessageDigest digest = MessageDigest.getInstance('MD5')
        InputStream fileStream = file.newInputStream()
        byte[] buffer = new byte[16384]
        int len

        while ((len = fileStream.read(buffer)) > 0) {
            digest.update(buffer, 0, len)
        }
        digest.digest()
    }
}
