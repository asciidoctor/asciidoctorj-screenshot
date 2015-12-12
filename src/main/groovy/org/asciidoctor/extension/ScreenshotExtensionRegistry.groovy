package org.asciidoctor.extension

import org.asciidoctor.Asciidoctor
import org.asciidoctor.extension.spi.ExtensionRegistry

class ScreenshotExtensionRegistry implements ExtensionRegistry {
    void register(Asciidoctor asciidoctor) {
        asciidoctor.javaExtensionRegistry().block 'driveBrowser', DriveBrowserBlock
        asciidoctor.javaExtensionRegistry().block 'takeScreenshot', TakeScreenshotBlock
    }
}
