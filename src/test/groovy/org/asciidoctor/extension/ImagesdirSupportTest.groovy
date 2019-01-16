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
 * Integration test for making sure the imagesdir attribute is respected
 */
class ImagesdirSupportTest extends Specification {

    private static final String url = ImagesdirSupportTest.classLoader.getResource("sample.html").toString()
    private static final String document1 = """= Test substitutions

:imagesdir: ./images
:screenshot-dir-name: ./screenie

== Process

screenshot::${url}[name="screenImage"]

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

    def "test imagesdir in document"() {
        when:
          String html = asciidoctor.convert(document1, options)
          html = html.replaceAll('\n', ' ')

        then:
          html.contains('<img src="./images/screenie/screenImage.png"')

          def imageFile1 = new File(outputDir, 'images/screenie/screenImage.png')
          imageFile1.exists()
    }

    def "test imagesdir in options"() {
        when:
          options.setAttributes(["imagesdir" : "./bilder"])
          String html = asciidoctor.convert(document1, options)
          html = html.replaceAll('\n', ' ')

        then:
          html.contains('<img src="./bilder/screenie/screenImage.png"')

          def imageFile1 = new File(outputDir, 'bilder/screenie/screenImage.png')
          imageFile1.exists()
    }

    def "test imagesdir and screenshotdir in options"() {
        when:
          options.setAttributes([
                  'imagesdir' : './bilder',
                  'screenshot-dir-name' : 'bildschirm'
          ])
          String html = asciidoctor.convert(document1, options)
          html = html.replaceAll('\n', ' ')

        then:
          html.contains('<img src="./bilder/bildschirm/screenImage.png"')

          def imageFile1 = new File(outputDir, 'bilder/bildschirm/screenImage.png')
          imageFile1.exists()
    }

    def "test imagesdir without leading dot slash"() {
        when:
          options.setAttributes(['imagesdir' : 'bilder'])
          String html = asciidoctor.convert(document1, options)
          html = html.replaceAll('\n', ' ')

        then:
          html.contains('<img src="bilder/screenie/screenImage.png"')

          def imageFile1 = new File(outputDir, 'bilder/screenie/screenImage.png')
          imageFile1.exists()
    }

    def "test imagesdir with absolut path"() {
        when:
          options.setAttributes(['imagesdir' : outputDir.absolutePath + '/bilder'])
          String html = asciidoctor.convert(document1, options)
          html = html.replaceAll('\n', ' ')

        then:
          html.contains("<img src=\"${outputDir.absolutePath}/bilder/screenie/screenImage.png\"")

          def imageFile1 = new File(outputDir, 'bilder/screenie/screenImage.png')
          imageFile1.exists()
    }

    def "test empty imagesdir"() {
        when:
          options.setAttributes(['imagesdir' : ''])
          String html = asciidoctor.convert(document1, options)
          html = html.replaceAll('\n', ' ')

        then:
          html.contains('<img src="./screenie/screenImage.png"')

          def imageFile1 = new File(outputDir, 'screenie/screenImage.png')
          imageFile1.exists()
    }
}
