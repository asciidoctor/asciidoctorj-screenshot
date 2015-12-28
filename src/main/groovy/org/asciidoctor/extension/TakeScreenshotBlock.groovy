package org.asciidoctor.extension

import geb.Browser
import org.asciidoctor.ast.AbstractBlock
import org.jruby.RubyHash
import org.jruby.RubySymbol

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static org.jruby.RubySymbol.newSymbol

class TakeScreenshotBlock extends BlockProcessor implements BrowserResizer {

    TakeScreenshotBlock(String name, RubyHash config) {
        super(name, [contexts: [':paragraph'], content_model: ':simple'])
    }

    private String generateId() {
        def alphabet = (('A'..'Z') + ('0'..'9')).join()
        def stringId
        new Random().with {
            stringId = (1..32).collect { alphabet[nextInt(36)] }.join()
        }
        stringId
    }

    def process(AbstractBlock block, Reader reader, Map<String, Object> attributes) {

        final String dimension = attributes['dimension']
        final String name = attributes['name']
        final String url = attributes['url']
        final String selector = attributes['selector']
        // this is a hack to display frames around images if a special dimension is selected
        final String alt = attributes['dimension']

        final String fileName = name ? name : generateId()

        ScreenshotDimension dim = new ScreenshotDimension(800, 600)
        if (dimension) {
            dim = resizeBrowserWindow(dimension)
        }

        def cutElement = takeScreenshot(url, fileName, selector)
        File screenshotsFile = getScreenshotFile(block, fileName)

        crop(screenshotsFile, cutElement, dim)

        createBlock(block, "image", "", [
                target: screenshotsFile.absolutePath,
                title : reader.lines().join(" // "),
                alt   : alt
        ], [:])
    }

    private def takeScreenshot(String url, String fileName, String selector) {
        def cutElement = null
        Browser.drive {
            if (url) {
                go url
                waitFor(1) { true }
            }

            if (selector) {
                cutElement = $(selector)
            }

            report fileName
        }
        cutElement
    }

    private File getScreenshotFile(AbstractBlock block, String name) {
        Map<String, Object> globalAttributes = block.document.attributes
        Map<RubySymbol, Object> globalOptions = block.document.options

        String buildDir = globalOptions[newSymbol(rubyRuntime, 'to_dir')] as String
        String screenshotDirName = globalAttributes['screenshot-dir-name'] as String

        String screenshotsDir = "${buildDir}/${screenshotDirName}/"

        File imageFile = new File(screenshotsDir + name + ".png")
    }

    private void crop(imageFile, cutElement, ScreenshotDimension dim) {
        BufferedImage img = ImageIO.read(imageFile)

        int x, y, w, h
        if (cutElement) {
            x = cutElement.x
            y = cutElement.y
            w = cutElement.width
            h = cutElement.height

        } else {
            x = 0
            y = 0
            w = dim.width
            h = dim.height
        }

        w = Math.min(w, img.width - x)
        h = Math.min(h, img.height - y)
        ImageIO.write(img.getSubimage(x, y, w, h), "png", imageFile)
    }
}
