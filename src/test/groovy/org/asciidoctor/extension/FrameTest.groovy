package org.asciidoctor.extension

import spock.lang.Specification

/**
 * ...
 */
class FrameTest extends Specification {

    def "all images should be available"() {
      expect:
        frame.getFrameImage()

      where:
        frame << Frame.values()
    }
}
