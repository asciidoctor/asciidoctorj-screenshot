package org.asciidoctor.extension

import geb.Browser
import org.asciidoctor.ast.AbstractBlock
import org.jruby.RubySymbol
import org.openqa.selenium.Dimension

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class ScreenshotBlock extends BlockProcessor {

    ScreenshotBlock(String name, Map<String, Object> config) {
        super(name, [contexts: [':paragraph'], content_model: ':simple'])
    }

    String generateId() {
        def alphabet = (('A'..'Z') + ('0'..'9')).join()
        def stringId
        new Random().with {
            stringId = (1..32).collect { alphabet[nextInt(36)] }.join()
        }
        stringId
    }

    def process(AbstractBlock block, Reader reader, Map<String, Object> attributes) {
        final Map<String, Object> globalAttributes = block.document.attributes
        final Map<String, Object> globalOptions = block.document.options

        def maxHeight = 600
        def maxWidth = 800
        if (attributes.dimension) {
            def dimension = attributes.dimension
            def driver = Browser.drive({}).driver
            def window = driver.manage().window()
            if (dimension == "FRAME_IPHONE4") dimension = "336x504"
            else if (dimension == "FRAME_IPHONE5") dimension = "336x596"
            else if (dimension == "FRAME_IPHONE6") dimension = "336x596"
            else if (dimension == "FRAME_IPHONE6PLUS") dimension = "360x640"
            else if (dimension == "FRAME_SAMSUNG_S4") dimension = "370x657"
            else if (dimension == "FRAME_IMAC") dimension = "1200x675"
            else if (dimension == "FRAME_BROWSER") dimension = "800x500"
            dimension = dimension.split("x")
            def width = dimension[0] as int
            def height = dimension[1] as int
            maxHeight = height
            maxWidth = width
            def size = window.size
            int viewportdeltax = driver.executeScript("return document.documentElement.clientWidth") - size.width
            int viewportdeltay = 80//driver.executeScript("return document.documentElement.clientHeight")-size.height
            window.size = new Dimension(width - viewportdeltax, height + viewportdeltay)
        }
        if (attributes.action == "browse") {
            def binding = new Binding()
            binding.setVariable("Browser", Browser)
            binding.setVariable("Dimension", Dimension)
            def shell = new GroovyShell(binding)
            shell.evaluate("Browser.drive{" + reader.lines().join("\n") + "}")
            createBlock(block, "paragraph", "", [:], [:])
        } else {
            def cutElement
            if (!attributes.name) attributes.name = generateId()
            Browser.drive {
                if (attributes.url) {
                    go attributes.url
                    waitFor(1) { true }
                }
                report attributes.name
                if (attributes.selector) {
                    cutElement = $(attributes.selector)
                }
            }

            def buildDir = getFromOptions(globalOptions, 'to_dir')
            String screenshotsDir = "${buildDir}/${globalAttributes['screenshot-dir-name']}/"
            if (cutElement) cropScreenshot(new File(screenshotsDir + attributes.name + ".png"), cutElement, maxWidth, maxHeight)
            else cropScreenshot(new File(screenshotsDir + attributes.name + ".png"), maxWidth, maxHeight)
            def alt = attributes.dimension
            createBlock(block, "image", "", [
                    target: "${screenshotsDir}/${attributes.name}.png" as String,
                    title : reader.lines().join(" // "),
                    alt   : alt
            ], [:])
        }
    }

    // required, since keys in options are RubySymbol and not String...
    def getFromOptions(Map options, String key) {
        RubySymbol keySym = RubySymbol.newSymbol(rubyRuntime, key)
        return options[keySym]
    }

    def cropScreenshot(screen, element, int maxWidth = 800, int maxHeight = 1200) {
        int width = element.width
        int height = element.height
        if (height > maxHeight) height = maxHeight
        if (width > maxWidth) width = maxWidth
        BufferedImage img = ImageIO.read(screen)
        BufferedImage dest = img.getSubimage(element.x, element.y, width, height)
        ImageIO.write(dest, "png", screen)
    }

    def cropScreenshot(File screen, int maxWidth = 800, int maxHeight = 600) {
        BufferedImage img = ImageIO.read(screen)
        int width = img.width
        int height = img.height
        if (height > maxHeight) height = maxHeight
        if (width > maxWidth) width = maxWidth
        BufferedImage dest = img.getSubimage(0, 0, width, height)
        ImageIO.write(dest, "png", screen)
    }
}
