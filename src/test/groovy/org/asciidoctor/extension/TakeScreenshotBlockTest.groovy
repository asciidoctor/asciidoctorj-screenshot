package org.asciidoctor.extension

import org.asciidoctor.Asciidoctor
import org.asciidoctor.Options
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * ...
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
          asciidoctor.convert(document1, options)

        then:
          new File(outputDir, 'screenshots/image1.png').exists()
    }

    def "take configured dir name for images"() {
        when:
          options.setAttributes(['screenshot-dir-name': 'img'])
          asciidoctor.convert(document1, options)

        then:
          new File(outputDir, 'img/image1.png').exists()
    }
}
