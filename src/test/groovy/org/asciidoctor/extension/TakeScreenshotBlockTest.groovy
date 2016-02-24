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

import org.asciidoctor.Asciidoctor
import org.asciidoctor.Options
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Integration test for {@link TakeScreenshotBlock}.
 */
class TakeScreenshotBlockTest extends Specification {

    private static final String url = TakeScreenshotBlockTest.classLoader.getResource("sample.html").toString()
    private static final String document1 = """ = How to use Google properly

== Process

[takeScreenshot, name=image1, url=${url}]
caption

"""

    @Rule
    TemporaryFolder tmpFolder = new TemporaryFolder()
    private File outputDir
    private Options options
    private Asciidoctor asciidoctor

    void setup() {
        outputDir = tmpFolder.newFolder()
        options = new Options()
        options.setDestinationDir(outputDir.absolutePath)

        asciidoctor = Asciidoctor.Factory.create()
    }


    def "default dir for images should be 'screenshots' below 'destination_dir'"() {
        when:
          String html = asciidoctor.convert(document1, options)

        then:
          new File(outputDir, 'screenshots/image1.png').exists()
          html.contains('<img src="screenshots/image1.png"')
    }

    def "take configured dir name for images"() {
        when:
          options.setAttributes(['screenshot-dir-name': 'img'])
          String html = asciidoctor.convert(document1, options)

        then:
          new File(outputDir, 'img/image1.png').exists()
          html.contains('<img src="img/image1.png"')
    }

    def "take configured dir name for images in parent"() {
        when:
          options.setAttributes(['screenshot-dir-name': '../img'])
          String html = asciidoctor.convert(document1, options)

        then:
          new File(tmpFolder.root, 'img/image1.png').exists()
          html.contains('<img src="../img/image1.png"')
    }
}
