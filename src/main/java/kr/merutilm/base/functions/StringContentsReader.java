package kr.merutilm.base.functions;

import javax.annotation.Nonnull;

public class StringContentsReader {
    private String next;
    private int start;
    private int end;

    public void skip() {
        s = next;
    }

    public String get() {
        return s;
    }

    private String s;

    public StringContentsReader(String s) {
        this.s = s;
    }

    public String getContentOfQuotes() {

        start = s.indexOf("\"");
        end = start;
        do {
            end = s.indexOf("\"", end + 1);
            if (end == -1) {
                break;
            }
        } while (s.charAt(end - 1) == '\\');
        return checkContents();
    }

    public String getContentOfQuotesIgnoredBackSlash() {

        start = s.indexOf("\"");
        end = start;
        end = s.indexOf("\"", end + 1);

        return checkContents();
    }

    @Nonnull
    private String checkContents() {
        String contents;
        if (start + 1 == end || start == -1 || end == -1) {
            contents = "";
        } else {
            contents = s.substring(start + 1, end);
        }
        next = s.substring(end + 1);
        return contents;
    }

    public String getContentOfBrackets() {
        String contents;
        start = s.indexOf("[");
        end = s.indexOf("]", start + 1);

        if (start + 1 == end || start == -1 || end == -1) {
            return "";
        } else {
            contents = s.substring(start + 1, end);
        }

        next = s.substring(end + 1);
        return contents;
    }

    public String[] getContentOfBracketsToArray() {
        String contents;
        contents = getContentOfBrackets();

        String[] contentsArray = contents.split(",");
        for (int i = 0; i < contentsArray.length; i++) {
            contentsArray[i] = contentsArray[i].replace(" ", "");
        }
        return contentsArray;
    }

    public String getContentOfComma() {
        String contents;
        end = s.indexOf(",");

        if (end == -1) {
            return s;
        } else {
            contents = s.substring(0, end);
        }
        next = s.substring(end + 1);
        return contents;
    }
}
