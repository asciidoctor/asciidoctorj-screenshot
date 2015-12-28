package org.asciidoctor.extension

import spock.lang.Specification
import spock.lang.Unroll

/**
 * ...
 */
class ScreenshotDimensionTest extends Specification {

    @Unroll
    def "screenshotDimension #dim should have width #w and height #h"() {
        given:
          def d = new ScreenshotDimension(dim)

        expect:
          d.width == w
          d.height == h

        where:
          dim                 ||    w |   h
          '100x200'           ||  100 | 200
          '200x200'           ||  200 | 200
          '300x200'           ||  300 | 200
          '200x100'           ||  200 | 100
          '200x100'           ||  200 | 100
          'FRAME_IPHONE4'     ||  336 | 504
          'FRAME_IPHONE5'     ||  336 | 596
          'FRAME_IPHONE6'     ||  336 | 596
          'FRAME_IPHONE6PLUS' ||  360 | 640
          'FRAME_SAMSUNG_S4'  ||  370 | 657
          'FRAME_IMAC'        || 1200 | 675
          'FRAME_BROWSER'     ||  800 | 500
    }

    @Unroll
    def "should throw #ex for invalid input '#dim'"() {
        given:
          def e = null
          try {
              new ScreenshotDimension(dim)
          } catch (any) {
              e = any
          }

        expect:
          e.class == ex

        where:
          dim           | ex
          null          | NullPointerException
          ''            | IllegalArgumentException
          'blubb'       | IllegalArgumentException
          'faxen'       | IllegalArgumentException
          '100x'        | IllegalArgumentException
          '100xasdf'    | IllegalArgumentException
          '100x200x300' | IllegalArgumentException
    }
}
