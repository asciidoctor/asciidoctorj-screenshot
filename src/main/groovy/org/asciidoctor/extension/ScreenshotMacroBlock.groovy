/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Stephan Classen, Markus Schlichting
 * Copyright (c) 2014 François-Xavier Thoorens
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
import java.util.regex.Matcher
import java.util.regex.Pattern

import static org.jruby.RubySymbol.newSymbol

/**
 * Block macro to take a screenshot.
 */
class ScreenshotMacroBlock extends BlockMacroProcessor implements BrowserResizer {

    ScreenshotMacroBlock(String name, RubyHash config) {
        super(name, [contexts: [':paragraph'], content_model: ':attributes', pos_attrs: ['name', 'frame']])
    }

    def process(AbstractBlock parent, String target, Map<String, Object> attributes) {
        attributes.put('url', expandAttributesInTarget(target, parent))
        final File buildDir = buildDir(parent)
        final String screenshotDirName = screenshotDirName(parent)
        final File screenshotDir = new File(buildDir, screenshotDirName)

        final File screenshotFile = new ScreenshotTaker(screenshotDir, attributes).takeScreenshot()

        createBlock(parent, "image", "", [
                target: screenshotDirName + '/' + screenshotFile.name,
                title : attributes['title'],
        ], [:])
    }

    //TODO this method should be removed when AsciidoctorJ provides a resolve_subs method to extensions.
    private String expandAttributesInTarget(String target, AbstractBlock block) {
        Matcher m = checkInputAgainstPattern(target, ~/\{[A-Za-z0-9_][A-Za-z0-9_-]+\}/)

        String url = target;
        if (m) {
            for (int i = 0; i < m.size(); i++) {
                url = replaceAttribute(url, m[i].toString(), block.document.attributes)
            }
        }

        return url;
    }

    private String replaceAttribute(String url, String attribute, Map<String, Object> documentAttributes) {
        // attribute is on the form '{attribute}'
        String attributeName = attribute.substring(1, attribute.length() - 1)

        if (!documentAttributes.containsKey(attributeName)) {
            throw new IllegalArgumentException("${attributeName} is not set in the document so it cannot be expanded.")
        }

        return url.replace(attribute, documentAttributes.get(attributeName))
    }

    private Matcher checkInputAgainstPattern(String input, Pattern pattern) {
        if (!(input ==~ pattern)) {
            return null
        }

        input =~ pattern
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
