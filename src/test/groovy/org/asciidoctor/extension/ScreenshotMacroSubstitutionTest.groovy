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

/**
 * Integration test for substituting attributes in teh screenshot macro.
 */
class ScreenshotMacroSubstitutionTest extends Specification {

    private static final String url = ScreenshotMacroSubstitutionTest.classLoader.getResource("sample.html").toString()
    private static final String document1 = """= Test substitutions

:url: ${url}
:title: With Substitution

:name1: imgName1
:name2: imgName2
:name3: imgName3
:dimension1: 201x301
:dimension2: 202x302
:dimension3: 203x303

== Process

.{title}
screenshot::{url}[name="{name1}", dimension="{dimension1}"]

screenshot::{url}[name='{name2}', dimension='{dimension2}']

screenshot::{url}[name={name3}, dimension={dimension3}]

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
          html.contains('<div class="title">Figure 1. With Substitution</div>')

          html =~ /<img src=".*imgName1\.png"/
          html.contains('alt="imgName1"')

          def imageFile1 = new File(outputDir, 'screenshots/imgName1.png')
          imageFile1.exists()
          def image1 = ImageIO.read(imageFile1)
          image1.height == 301
          image1.width == 201

          html =~ /<img src=".*imgName2\.png"/
          html.contains('alt="imgName2"')

          def imageFile2 = new File(outputDir, 'screenshots/imgName2.png')
          imageFile2.exists()
          def image2 = ImageIO.read(imageFile2)
          image2.height == 302
          image2.width == 202

          html =~ /<img src=".*imgName3\.png"/
          html.contains('alt="imgName3"')

          def imageFile3 = new File(outputDir, 'screenshots/imgName3.png')
          imageFile3.exists()
          def image3 = ImageIO.read(imageFile3)
          image3.height == 303
          image3.width == 203
    }
}
