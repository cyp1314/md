package org.itheima.tools.markdown.impl;

import com.github.rjeschke.txtmark.Processor;
import org.itheima.tools.markdown.Markdown;

public class TxtmarkImpl
        implements Markdown {
    public String parse(String text) {
        return Processor.process(text);
    }
}
