package io.bacta.swg.template.definition;

/**
 * Created by crush on 4/18/2016.
 * <p>
 * TemplateLineBuffer helps to simulate operations that would be done with a pointer on a string. It also encapsulates
 * some helper functions for parsing the template strings.
 */
public class TemplateLineBuffer {
    private final String line;
    private int currentPosition;

    public TemplateLineBuffer(final String line) {
        this.line = line;
    }

    public TemplateLineBuffer(final TemplateLineBuffer buffer) {
        this.line = buffer.line;
        this.currentPosition = buffer.currentPosition;
    }

    /**
     * Returns the next whitespace-delimited token from a string.
     *
     * @return The next non-whitespace character in the string, or null if the end of line has been reached.
     */
    public String getNextWhitespaceToken() {
        //Skip leading whitespace.
        skipWhitespace();

        if (isAtEnd())
            return null;

        final StringBuilder sb = new StringBuilder(line.length() - currentPosition);

        //Get the token.
        while (!isAtEnd() && !Character.isWhitespace(line.charAt(currentPosition)))
            sb.append(readChar());

        //Skip trailing whitespace.
        skipWhitespace();

        return sb.toString();
    }

    /**
     * Gets the next token from a string.
     *
     * @return the next token, defined by the 1st non-whitespace character in buffer:
     * if it is '/' and the next character is '/', null
     * if it is a double-quote, the text until the next double quote (not including \")
     * if it is a symbol, the symbol
     * if it is a number, the next characters that make a valid integer or float
     * if it is a character, the text until the next whitespace or symbol, not including _
     * if it is NULL, null
     */
    public String getNextToken() {
        //Skip any leading whitespace.
        skipWhitespace();

        if (isAtEnd())
            return null;

        final StringBuilder sb = new StringBuilder(line.length() - currentPosition);

        if (isComment()) {
            return null;
        } else if (isNumber()) {
            appendDigits(sb);
        } else if (isAlpha()) {
            appendAlphaNumericUnderscore(sb);
        } else if (charAt(0) == '"') {
            appendQuotation(sb);
        } else {
            sb.append(readChar()); //Just append the character.
        }

        //Skip any trailing whitespace.
        skipWhitespace();

        return sb.toString();
    }

    /**
     * Gets the remainder of the line and returns it as a string.
     *
     * @return The remainder of the line.
     */
    public String getRemainingString() {
        return line.substring(currentPosition);
    }

    public int position() {
        return currentPosition;
    }

    public void position(int position) {
        currentPosition = position;
    }

    public boolean isAtEnd() {
        return currentPosition >= line.length();
    }

    public char charAt(int offset) {
        return line.charAt(currentPosition + offset);
    }

    public int remaining() {
        return line.length() - currentPosition;
    }

    public void skip(int offset) {
        currentPosition += offset;
    }

    private boolean isComment() {
        return remaining() >= 2 && charAt(0) == '/' && charAt(1) == '/';
    }

    private boolean isNumber() {
        return remaining() >= 2
                && Character.isDigit(charAt(0))
                || (charAt(0) == '+' || charAt(0) == '-') && Character.isDigit(charAt(1));
    }

    private boolean isAlpha() {
        return Character.isAlphabetic(charAt(0));
    }

    private void skipWhitespace() {
        while (!isAtEnd() && Character.isWhitespace(charAt(0)))
            ++currentPosition;
    }

    private char readChar() {
        return line.charAt(currentPosition++);
    }

    private void appendDigits(final StringBuilder sb) {
        while (!isAtEnd() && Character.isDigit(charAt(0)))
            sb.append(readChar());

        if (!isAtEnd() && charAt(0) == '.' && Character.isDigit(charAt(1))) {
            sb.append(readChar());
            appendDigits(sb);
        }
    }

    private void appendAlphaNumericUnderscore(final StringBuilder sb) {
        while (!isAtEnd()
                && (Character.isAlphabetic(charAt(0))
                || Character.isDigit(charAt(0))
                || charAt(0) == '_'))
            sb.append(readChar());
    }

    private void appendQuotation(final StringBuilder sb) {
        while (!isAtEnd() && charAt(0) != '"') {
            if (charAt(0) != '\\') {
                sb.append(charAt(0));
            } else {
                //Copy the \ and the next character blindly.
                sb.append(readChar());

                if (!isAtEnd())
                    sb.append(readChar());
            }
        }

        if (charAt(0) == '"')
            sb.append(readChar());
    }
}
