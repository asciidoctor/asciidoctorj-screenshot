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
    private File expectedScreenshot

    @Rule
    TemporaryFolder tmpFolder = new TemporaryFolder()

    void setup() {
        url = getClass().classLoader.getResource("sample.html").toString()
        expectedScreenshot = getClass().classLoader.getResource('screenshot.png').file as File
        outputDir = tmpFolder.newFolder()
        outputDir = new File('/tmp')
    }

    def 'fullScreenShot'() {
        given:
          ScreenshotTaker sut = new ScreenshotTaker(outputDir, ['url' : url])

        when:
          File screenshot = sut.takeScreenshot()

        then:
          screenshot.exists()
          screenshot.parentFile.absolutePath == outputDir.absolutePath
          getChecksum(expectedScreenshot) == getChecksum(screenshot)
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
