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
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url': url, 'dimension': 'FRAME_SAMSUNG_S4'])

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
                  'dimension': 'FRAME_SAMSUNG_S4',
                  'selector' : '.circle'
          ])

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
        InputStream fileStream = file.newInputStream()
        byte[] buffer = new byte[16384]
        int len

        while ((len = fileStream.read(buffer)) > 0) {
            digest.update(buffer, 0, len)
        }
        digest.digest()
    }
}
