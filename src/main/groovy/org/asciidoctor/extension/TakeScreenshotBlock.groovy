/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Stephan Classen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.asciidoctor.extension

import org.asciidoctor.ast.AbstractBlock
import org.jruby.RubyHash
import org.jruby.RubySymbol

import static org.jruby.RubySymbol.newSymbol

/**
 * Block to take a screenshot using GEB.
 */
class TakeScreenshotBlock extends BlockProcessor implements BrowserResizer {

    TakeScreenshotBlock(String name, RubyHash config) {
        super(name, [contexts: [':paragraph'], content_model: ':simple'])
    }

    def process(AbstractBlock block, Reader reader, Map<String, Object> attributes) {
        final File buildDir = buildDir(block)
        final String screenshotDirName = screenshotDirName(block)
        final File screenshotDir = new File(buildDir, screenshotDirName)

        final File screenshotsFile = new ScreenshotTaker(screenshotDir, attributes).takeScreenshot()

        createBlock(block, "image", "", [
                target: screenshotDirName + '/' + screenshotsFile.name,
                title : reader.lines().join(" // "),
        ], [:])
    }

    private File buildDir(AbstractBlock block) {
        Map<RubySymbol, Object> globalOptions = block.document.options

        String toDir = globalOptions[newSymbol(rubyRuntime, 'to_dir')]
        String destDir = globalOptions[newSymbol(rubyRuntime, 'destination_dir')]
        String buildDir = toDir ? toDir : destDir
        new File(buildDir)
    }

    private String screenshotDirName(AbstractBlock block) {
        Map<String, Object> globalAttributes = block.document.attributes

        String screenshotDirName = globalAttributes['screenshot-dir-name']

        if (!screenshotDirName || screenshotDirName.isAllWhitespace()) {
            screenshotDirName = 'screenshots'
        }
        screenshotDirName
    }
}
