package org.asciidoctor.extension

/**
 * ...
 */
class ScreenshotDimension {

    private static final Map<String, String> dimensions = [
            "FRAME_IPHONE4"    : "336x504",
            "FRAME_IPHONE5"    : "336x596",
            "FRAME_IPHONE6"    : "336x596",
            "FRAME_IPHONE6PLUS": "360x640",
            "FRAME_SAMSUNG_S4" : "370x657",
            "FRAME_IMAC"       : "1200x675",
            "FRAME_BROWSER"    : "800x500"
    ].withDefault { key -> key }

    int width
    int height

    ScreenshotDimension(int width, int height) {
        this.width = width
        this.height = height
    }

    ScreenshotDimension(String dim) {
        String[] d = dimensions[dim].split('x')

        if (d.size() != 2) {
            throw new IllegalArgumentException('Dimension must be in the format {width}x{height}')
        }

        try {
            width = d[0] as int
            height = d[1] as int
        } catch (Exception ignored) {
            throw new IllegalArgumentException('Dimension must be in the format {width}x{height}')
        }
    }
}
