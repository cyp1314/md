package org.itheima.tools.markdown.impl;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.IParse;
import com.vladsch.flexmark.IRender;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.escaped.character.EscapedCharacterExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.jekyll.front.matter.JekyllFrontMatterExtension;
import com.vladsch.flexmark.ext.spec.example.SpecExampleExtension;
import com.vladsch.flexmark.ext.spec.example.internal.RenderAs;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.ext.toc.internal.TocOptions;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.KeepType;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.util.ArrayList;

import org.itheima.tools.markdown.Markdown;

public class FlexmarkImpl
        implements Markdown {
    private IParse parser;
    private IRender render;

    public enum ForUseBy {
        PARSER, JAVAFX, SWING, HTML;
    }

    static class Options {
        public boolean abbreviations;
        public boolean autoLinks;
        public boolean anchorLinks;
        public boolean definitions;
        public boolean fencedCode;
        public boolean hardWraps;
        public boolean atxHeadingSpace;
        public boolean typographicQuotes;
        public boolean typographicSmarts;
        public boolean relaxedThematicBreak;

        Options() {
            this.abbreviations = false;
            this.autoLinks = true;
            this.anchorLinks = true;
            this.definitions = false;
            this.fencedCode = true;
            this.hardWraps = false;
            this.atxHeadingSpace = true;
            this.typographicQuotes = false;
            this.typographicSmarts = false;
            this.relaxedThematicBreak = true;
            this.strikeThrough = true;
            this.tables = true;
            this.renderTablesGFM = true;
            this.taskListItems = true;
            this.wikiLinks = false;
            this.wikiLinkGfmSyntax = true;
            this.footnotes = false;
            this.tableOfContents = true;
            this.jekyllFrontMatter = false;
            this.emojiShortcuts = false;
            this.emojiImageDirectory = "";
        }

        public boolean strikeThrough;
        public boolean tables;
        public boolean renderTablesGFM;
        public boolean taskListItems;
        public boolean wikiLinks;
        public boolean wikiLinkGfmSyntax;
        public boolean footnotes;
        public boolean tableOfContents;
        public boolean jekyllFrontMatter;
        public boolean emojiShortcuts;
        public String emojiImageDirectory;
    }

    private static MutableDataHolder options(ForUseBy purpose, Options options) {
        MutableDataSet dataSet = new MutableDataSet();
        ArrayList<Extension> extensions = new ArrayList<>();

        dataSet.set(Parser.PARSE_INNER_HTML_COMMENTS, Boolean.valueOf(true));
        dataSet.set(Parser.INDENTED_CODE_NO_TRAILING_BLANK_LINES, Boolean.valueOf(true));
        dataSet.set(HtmlRenderer.SUPPRESS_HTML_BLOCKS, Boolean.valueOf(false));
        dataSet.set(HtmlRenderer.SUPPRESS_INLINE_HTML, Boolean.valueOf(false));


        extensions.add(EscapedCharacterExtension.create());


        dataSet.set(Parser.BLOCK_QUOTE_TO_BLANK_LINE, Boolean.valueOf(true));


        dataSet.set(Parser.LISTS_AUTO_LOOSE, Boolean.valueOf(false));
        dataSet.set(Parser.LISTS_AUTO_LOOSE, Boolean.valueOf(false));


        dataSet.set(Parser.LISTS_END_ON_DOUBLE_BLANK, Boolean.valueOf(false));

        dataSet.set(Parser.LISTS_BULLET_ITEM_INTERRUPTS_PARAGRAPH, Boolean.valueOf(false));
        dataSet.set(Parser.LISTS_BULLET_ITEM_INTERRUPTS_ITEM_PARAGRAPH, Boolean.valueOf(true));
        dataSet.set(Parser.LISTS_ORDERED_ITEM_DOT_ONLY, Boolean.valueOf(true));
        dataSet.set(Parser.LISTS_ORDERED_ITEM_INTERRUPTS_PARAGRAPH, Boolean.valueOf(false));
        dataSet.set(Parser.LISTS_ORDERED_ITEM_INTERRUPTS_ITEM_PARAGRAPH, Boolean.valueOf(true));
        dataSet.set(Parser.LISTS_ORDERED_NON_ONE_ITEM_INTERRUPTS_PARAGRAPH, Boolean.valueOf(false));


        dataSet.set(Parser.LISTS_ORDERED_LIST_MANUAL_START, Boolean.valueOf(false));

        if (options.abbreviations) {
            extensions.add(AbbreviationExtension.create());
            dataSet.set(AbbreviationExtension.ABBREVIATIONS_KEEP, KeepType.LAST);
        }

        if (options.anchorLinks) {
            extensions.add(AnchorLinkExtension.create());
            dataSet.set(AnchorLinkExtension.ANCHORLINKS_WRAP_TEXT, Boolean.valueOf(true));
        }

        if (options.autoLinks) {
            extensions.add(AutolinkExtension.create());
        }

        if (options.definitions) {
            extensions.add(DefinitionExtension.create());
        }

        if (options.fencedCode) {

            dataSet.set(Parser.MATCH_CLOSING_FENCE_CHARACTERS, Boolean.valueOf(false));
        } else {
            dataSet.set(Parser.FENCED_CODE_BLOCK_PARSER, Boolean.valueOf(false));
        }

        if (options.hardWraps) {
            dataSet.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
            dataSet.set(HtmlRenderer.HARD_BREAK, "<br />\n<br />\n");
        }

        if (!options.atxHeadingSpace) {
            dataSet.set(Parser.HEADING_NO_ATX_SPACE, Boolean.valueOf(true));
        }
        dataSet.set(Parser.HEADING_NO_LEAD_SPACE, Boolean.valueOf(true));

        if (purpose == ForUseBy.PARSER) {


            dataSet.set(Parser.HEADING_SETEXT_MARKER_LENGTH, Integer.valueOf(3));
        }

        if (options.typographicQuotes || options.typographicSmarts) {

            extensions.add(TypographicExtension.create());
            dataSet.set(TypographicExtension.TYPOGRAPHIC_SMARTS, Boolean.valueOf(options.typographicSmarts));
            dataSet.set(TypographicExtension.TYPOGRAPHIC_QUOTES, Boolean.valueOf(options.typographicQuotes));
        }

        dataSet.set(Parser.THEMATIC_BREAK_RELAXED_START, Boolean.valueOf(options.relaxedThematicBreak));

        if (options.strikeThrough) {
            extensions.add(StrikethroughExtension.create());
        }

        if (options.tables) {
            extensions.add(TablesExtension.create());
            dataSet.set(TablesExtension.TRIM_CELL_WHITESPACE, Boolean.valueOf(false));
            dataSet.set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, Boolean.valueOf(false));
        }

        if (options.taskListItems) {
            extensions.add(TaskListExtension.create());
        }

        if (options.wikiLinks) {
            extensions.add(WikiLinkExtension.create());
            dataSet.set(WikiLinkExtension.LINK_FIRST_SYNTAX, Boolean.valueOf(!options.wikiLinkGfmSyntax));
        }

        if (options.footnotes) {
            extensions.add(FootnoteExtension.create());
            dataSet.set(FootnoteExtension.FOOTNOTES_KEEP, KeepType.LAST);
        }


        dataSet.set(Parser.REFERENCES_KEEP, KeepType.LAST);

        if (options.tableOfContents) {
            extensions.add(SimTocExtension.create());
            dataSet.set(SimTocExtension.BLANK_LINE_SPACER, Boolean.valueOf(true));

            extensions.add(TocExtension.create());
            dataSet.set(TocExtension.LEVELS, Integer.valueOf(TocOptions.getLevels(new int[]{2, 3})));
        }

        if (options.jekyllFrontMatter) {
            extensions.add(JekyllFrontMatterExtension.create());
        }

        if (options.emojiShortcuts) {


            extensions.add(EmojiExtension.create());
            if (options.emojiImageDirectory.isEmpty()) {
                dataSet.set(EmojiExtension.USE_IMAGE_URLS, Boolean.valueOf(true));
            } else {
                dataSet.set(EmojiExtension.ROOT_IMAGE_PATH, options.emojiImageDirectory);
            }
        }

        if (purpose == ForUseBy.JAVAFX) {


            dataSet.set(HtmlRenderer.INDENT_SIZE, Integer.valueOf(2));


            if (options.tables && options.renderTablesGFM) {
                dataSet.set(TablesExtension.COLUMN_SPANS, Boolean.valueOf(false)).set(TablesExtension.MIN_HEADER_ROWS, Integer.valueOf(1))
                        .set(TablesExtension.MAX_HEADER_ROWS, Integer.valueOf(1)).set(TablesExtension.APPEND_MISSING_COLUMNS, Boolean.valueOf(true))
                        .set(TablesExtension.DISCARD_EXTRA_COLUMNS, Boolean.valueOf(true))
                        .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, Boolean.valueOf(true));
            }

            if (options.fencedCode) {
                dataSet.set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "");
            }

            if (options.anchorLinks) {
                dataSet.set(AnchorLinkExtension.ANCHORLINKS_SET_ID, Boolean.valueOf(true));
                dataSet.set(AnchorLinkExtension.ANCHORLINKS_ANCHOR_CLASS, "anchor");
                dataSet.set(AnchorLinkExtension.ANCHORLINKS_SET_NAME, Boolean.valueOf(true));
                dataSet.set(AnchorLinkExtension.ANCHORLINKS_TEXT_PREFIX,
                        "<span class=\"octicon octicon-link\"></span>");
            }

            if (options.taskListItems) {
                dataSet.set(TaskListExtension.ITEM_DONE_MARKER, "<span class=\"taskitem\">X</span>");
                dataSet.set(TaskListExtension.ITEM_NOT_DONE_MARKER, "<span class=\"taskitem\">O</span>");
            }

            dataSet.set(HtmlRenderer.RENDER_HEADER_ID, Boolean.valueOf(true));

            if (!options.wikiLinks) {
                dataSet.set(WikiLinkExtension.DISABLE_RENDERING, Boolean.valueOf(true));
            }
        } else if (purpose == ForUseBy.SWING) {

            dataSet.set(HtmlRenderer.INDENT_SIZE, Integer.valueOf(2));


            if (options.tables && options.renderTablesGFM) {
                dataSet.set(TablesExtension.COLUMN_SPANS, Boolean.valueOf(false)).set(TablesExtension.MIN_HEADER_ROWS, Integer.valueOf(1))
                        .set(TablesExtension.MAX_HEADER_ROWS, Integer.valueOf(1)).set(TablesExtension.APPEND_MISSING_COLUMNS, Boolean.valueOf(true))
                        .set(TablesExtension.DISCARD_EXTRA_COLUMNS, Boolean.valueOf(true))
                        .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, Boolean.valueOf(true));
            }

            if (options.fencedCode) {
                dataSet.set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "");
            }

            if (options.anchorLinks) {
                dataSet.set(AnchorLinkExtension.ANCHORLINKS_SET_ID, Boolean.valueOf(false));
                dataSet.set(AnchorLinkExtension.ANCHORLINKS_ANCHOR_CLASS, "");
                dataSet.set(AnchorLinkExtension.ANCHORLINKS_SET_NAME, Boolean.valueOf(true));
                dataSet.set(AnchorLinkExtension.ANCHORLINKS_TEXT_PREFIX, "");
            }

            if (options.taskListItems) {
                dataSet.set(TaskListExtension.ITEM_DONE_MARKER, "");
                dataSet.set(TaskListExtension.ITEM_NOT_DONE_MARKER, "");
            }

            dataSet.set(HtmlRenderer.RENDER_HEADER_ID, Boolean.valueOf(true));

            if (!options.wikiLinks) {
                dataSet.set(WikiLinkExtension.DISABLE_RENDERING, Boolean.valueOf(true));
            }
        } else if (purpose == ForUseBy.HTML) {

            dataSet.set(HtmlRenderer.INDENT_SIZE, Integer.valueOf(2));

            dataSet.set(Parser.LISTS_LOOSE_WHEN_PREV_HAS_TRAILING_BLANK_LINE, Boolean.valueOf(false));

            if (options.fencedCode) {
                dataSet.set(HtmlRenderer.FENCED_CODE_LANGUAGE_CLASS_PREFIX, "");
            }

            if (options.tables && options.renderTablesGFM) {
                dataSet.set(TablesExtension.COLUMN_SPANS, Boolean.valueOf(false)).set(TablesExtension.MIN_HEADER_ROWS, Integer.valueOf(1))
                        .set(TablesExtension.MAX_HEADER_ROWS, Integer.valueOf(1)).set(TablesExtension.APPEND_MISSING_COLUMNS, Boolean.valueOf(true))
                        .set(TablesExtension.DISCARD_EXTRA_COLUMNS, Boolean.valueOf(true))
                        .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, Boolean.valueOf(true));
            }

            dataSet.set(HtmlRenderer.RENDER_HEADER_ID, Boolean.valueOf(false));
            dataSet.set(HtmlRenderer.GENERATE_HEADER_ID, Boolean.valueOf(true));


            dataSet.set(SpecExampleExtension.SPEC_EXAMPLE_RENDER_AS, RenderAs.FENCED_CODE);
            dataSet.set(SpecExampleExtension.SPEC_EXAMPLE_RENDER_RAW_HTML, Boolean.valueOf(false));
        }

        dataSet.set(Parser.EXTENSIONS, extensions);

        return (MutableDataHolder) dataSet;
    }


    public FlexmarkImpl() {
        DataHolder holder = buildDataHolder();
        this.parser = (IParse) Parser.builder(holder).build();
        this.render = (IRender) HtmlRenderer.builder(holder).escapeHtml(true).indentSize(4).build();
    }


    private DataHolder buildDataHolder() {
        MutableDataSet dataSet = new MutableDataSet();


        dataSet.set(HtmlRenderer.CODE_STYLE_HTML_OPEN, "```");
        dataSet.set(HtmlRenderer.CODE_STYLE_HTML_CLOSE, "```");

        return (DataHolder) dataSet;
    }


    public String parse(String text) {
        Node document = this.parser.parse(text);
        return this.render.render(document);
    }
}
