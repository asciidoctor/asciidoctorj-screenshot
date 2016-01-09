package org.asciidoctor.extension

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.security.MessageDigest

/**
 * ...
 */
class ScreenshotTakerTest extends Specification {
    private String url
    private File outputDir

    @Rule
    TemporaryFolder tmpFolder = new TemporaryFolder()

    void setup() {
        url = getClass().classLoader.getResource("sample.html").toString()
        outputDir = tmpFolder.newFolder()
        outputDir = new File('/tmp')
    }

    def 'screenshot is placed into output directory'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url' : url])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          screenshot.exists()
          screenshot.parentFile.absolutePath == outputDir.absolutePath
    }

    def 'screenshot is has given name'() {
        given:
          String name = 'screeeny'
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url' : url, 'name': name])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          screenshot.exists()
          screenshot.name == name + '.png'
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
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url': url, 'dimension': 'FRAME_SAMSUNG_S4'])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          getChecksum(screenshot) == getChecksum('screenshot_samsungS4.png')
    }


    def 'screenshot of circle is equal to the expected'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url': url, 'dimension': '800x600', 'selector': '.circle'])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          getChecksum(screenshot) == getChecksum('screenshot_circle.png')
    }

    def 'screenshot of circle with dimension Smasung S4 is equal to the expected'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url': url, 'dimension': 'FRAME_SAMSUNG_S4', 'selector': '.circle', 'name': 'ccc'])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          getChecksum(screenshot) == getChecksum('screenshot_circle_samsungS4.png')
    }


    private byte[] getChecksum(String expected) {
        File f = getClass().classLoader.getResource(expected).file as File
        getChecksum(f)
    }

    private static byte[] getChecksum(file) {
        MessageDigest digest = MessageDigest.getInstance('MD5')
        InputStream inputstream = file.newInputStream()
        byte[] buffer = new byte[16384]
        int len

        while((len=inputstream.read(buffer)) > 0) {
            digest.update(buffer, 0, len)
        }
        digest.digest()
    }
}
