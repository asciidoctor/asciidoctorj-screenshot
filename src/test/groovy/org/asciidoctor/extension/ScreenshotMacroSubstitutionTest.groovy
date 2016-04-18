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

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * Integration test for substituting attributes in teh screenshot macro.
 */
class ScreenshotMacroSubstitutionTest extends Specification {

    private static final String url = ScreenshotMacroSubstitutionTest.classLoader.getResource("sample.html").toString()
    private static final String document1 = """ = How to use Google properly

:url: ${url}
:name: imgName
:dimension: 200x300
:title: With Substitution

== Process

.{title}
screenshot::{url}[name="{name}", dimension='{dimension}']

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

    def "test keys in geb block"() {
        when:
          String html = asciidoctor.convert(document1, options)
          html = html.replaceAll('\n', ' ')

        then:
          html =~ /<img src=".*imgName\.png"/
          html.contains('alt="imgName"')
          html.contains('<div class="title">Figure 1. With Substitution</div>')

          def imageFile = new File(outputDir, 'screenshots/imgName.png')
          imageFile.exists()
          def image = ImageIO.read(imageFile)
          image.height == 300
          image.width == 200
    }
}
