package com.pbl.stringprocessing.service;

import com.pbl.stringprocessing.model.StringResult;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Service class that performs all StringBuffer and StringTokenizer operations.
 *
 * KEY CONCEPTS:
 * ============
 *
 * StringBuffer:
 * - A mutable sequence of characters (unlike String which is immutable).
 * - Thread-safe (synchronized methods).
 * - Key methods: append(), insert(), delete(), reverse(), replace(), indexOf()
 * - More efficient than String concatenation in loops.
 *
 * StringTokenizer:
 * - Breaks a string into tokens using delimiters (default: whitespace).
 * - Methods: hasMoreTokens(), nextToken(), countTokens()
 * - Simpler and faster than String.split() for basic tokenization.
 * - Does NOT support regular expressions (unlike split()).
 */
@Service
public class StringProcessingService {

    private final SimpMessagingTemplate messagingTemplate;

    // History log of operations
    private final List<String> operationLogs = new ArrayList<>();
    private static final int MAX_LOGS = 100;

    public StringProcessingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // ==================== StringBuffer Operations ====================

    /**
     * APPEND: Appends text to the end of the StringBuffer.
     *
     * StringBuffer sb = new StringBuffer("Hello");
     * sb.append(" World"); // "Hello World"
     */
    public StringResult appendOperation(String original, String toAppend) {
        StringBuffer sb = new StringBuffer(original);
        sb.append(toAppend);

        String result = sb.toString();
        String msg = "append(\"" + toAppend + "\") → \"" + result + "\"";
        addLog("StringBuffer.append | " + msg);

        StringResult r = new StringResult(true, msg, result, null, "STRINGBUFFER");
        broadcast(r);
        return r;
    }

    /**
     * INSERT: Inserts text at the given index.
     *
     * StringBuffer sb = new StringBuffer("Hello World");
     * sb.insert(5, " Beautiful"); // "Hello Beautiful World"
     */
    public StringResult insertOperation(String original, int index, String toInsert) {
        try {
            StringBuffer sb = new StringBuffer(original);
            sb.insert(index, toInsert);

            String result = sb.toString();
            String msg = "insert(" + index + ", \"" + toInsert + "\") → \"" + result + "\"";
            addLog("StringBuffer.insert | " + msg);

            StringResult r = new StringResult(true, msg, result, null, "STRINGBUFFER");
            broadcast(r);
            return r;
        } catch (StringIndexOutOfBoundsException e) {
            String msg = "Error: Index " + index + " is out of bounds for length " + original.length();
            return new StringResult(false, msg, original, null, "STRINGBUFFER");
        }
    }

    /**
     * DELETE: Deletes characters from start (inclusive) to end (exclusive).
     *
     * StringBuffer sb = new StringBuffer("Hello World");
     * sb.delete(5, 11); // "Hello"
     */
    public StringResult deleteOperation(String original, int start, int end) {
        try {
            StringBuffer sb = new StringBuffer(original);
            sb.delete(start, end);

            String result = sb.toString();
            String msg = "delete(" + start + ", " + end + ") → \"" + result + "\"";
            addLog("StringBuffer.delete | " + msg);

            StringResult r = new StringResult(true, msg, result, null, "STRINGBUFFER");
            broadcast(r);
            return r;
        } catch (StringIndexOutOfBoundsException e) {
            String msg = "Error: Invalid range [" + start + ", " + end + "] for length " + original.length();
            return new StringResult(false, msg, original, null, "STRINGBUFFER");
        }
    }

    /**
     * REVERSE: Reverses the entire StringBuffer.
     *
     * StringBuffer sb = new StringBuffer("Hello");
     * sb.reverse(); // "olleH"
     */
    public StringResult reverseOperation(String original) {
        StringBuffer sb = new StringBuffer(original);
        sb.reverse();

        String result = sb.toString();
        String msg = "reverse() → \"" + result + "\"";
        addLog("StringBuffer.reverse | " + msg);

        StringResult r = new StringResult(true, msg, result, null, "STRINGBUFFER");
        broadcast(r);
        return r;
    }

    /**
     * REPLACE: Replaces characters from start to end with a new string.
     *
     * StringBuffer sb = new StringBuffer("Hello World");
     * sb.replace(6, 11, "Java"); // "Hello Java"
     */
    public StringResult replaceOperation(String original, int start, int end, String replacement) {
        try {
            StringBuffer sb = new StringBuffer(original);
            sb.replace(start, end, replacement);

            String result = sb.toString();
            String msg = "replace(" + start + ", " + end + ", \"" + replacement + "\") → \"" + result + "\"";
            addLog("StringBuffer.replace | " + msg);

            StringResult r = new StringResult(true, msg, result, null, "STRINGBUFFER");
            broadcast(r);
            return r;
        } catch (StringIndexOutOfBoundsException e) {
            String msg = "Error: Invalid range [" + start + ", " + end + "] for length " + original.length();
            return new StringResult(false, msg, original, null, "STRINGBUFFER");
        }
    }

    /**
     * LENGTH: Returns the length of the StringBuffer.
     *
     * StringBuffer sb = new StringBuffer("Hello");
     * sb.length(); // 5
     */
    public StringResult lengthOperation(String original) {
        StringBuffer sb = new StringBuffer(original);
        int len = sb.length();

        String msg = "length() → " + len;
        addLog("StringBuffer.length | " + msg);

        StringResult r = new StringResult(true, msg, String.valueOf(len), null, "STRINGBUFFER");
        broadcast(r);
        return r;
    }

    /**
     * CHARAT: Returns the character at the given index.
     */
    public StringResult charAtOperation(String original, int index) {
        try {
            StringBuffer sb = new StringBuffer(original);
            char c = sb.charAt(index);

            String msg = "charAt(" + index + ") → '" + c + "'";
            addLog("StringBuffer.charAt | " + msg);

            StringResult r = new StringResult(true, msg, String.valueOf(c), null, "STRINGBUFFER");
            broadcast(r);
            return r;
        } catch (StringIndexOutOfBoundsException e) {
            String msg = "Error: Index " + index + " is out of bounds for length " + original.length();
            return new StringResult(false, msg, original, null, "STRINGBUFFER");
        }
    }

    // ==================== StringTokenizer Operations ====================

    /**
     * TOKENIZE: Splits input string using the given delimiter.
     *
     * StringTokenizer st = new StringTokenizer("Hello World Java", " ");
     * while (st.hasMoreTokens()) {
     *     System.out.println(st.nextToken()); // "Hello", "World", "Java"
     * }
     */
    public StringResult tokenizeOperation(String input, String delimiter) {
        if (delimiter == null || delimiter.isEmpty()) delimiter = " ";

        // Use StringTokenizer to split the string
        StringTokenizer st = new StringTokenizer(input, delimiter);

        List<String> tokens = new ArrayList<>();
        int count = st.countTokens();

        // Iterate through all tokens using hasMoreTokens() and nextToken()
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }

        String msg = "StringTokenizer(\"" + input + "\", \"" + delimiter + "\") → "
                + count + " token(s): " + tokens;
        addLog("StringTokenizer.tokenize | " + msg);

        StringResult r = new StringResult(true, msg, String.join(", ", tokens), tokens, "TOKENIZER");
        broadcast(r);
        return r;
    }

    /**
     * COUNT TOKENS: Count tokens without consuming them.
     */
    public StringResult countTokensOperation(String input, String delimiter) {
        if (delimiter == null || delimiter.isEmpty()) delimiter = " ";

        StringTokenizer st = new StringTokenizer(input, delimiter);
        int count = st.countTokens();

        String msg = "countTokens() with delimiter \"" + delimiter + "\" → " + count + " token(s)";
        addLog("StringTokenizer.countTokens | " + msg);

        StringResult r = new StringResult(true, msg, String.valueOf(count), null, "TOKENIZER");
        broadcast(r);
        return r;
    }

    // ==================== Logs ====================

    public List<String> getLogs() {
        synchronized (operationLogs) {
            return new ArrayList<>(operationLogs);
        }
    }

    public void clearLogs() {
        synchronized (operationLogs) {
            operationLogs.clear();
        }
        broadcastLogs();
    }

    private void addLog(String message) {
        synchronized (operationLogs) {
            operationLogs.add(0, message); // newest first
            while (operationLogs.size() > MAX_LOGS) {
                operationLogs.remove(operationLogs.size() - 1);
            }
        }
    }

    // ==================== WebSocket Broadcast ====================

    private void broadcast(StringResult result) {
        messagingTemplate.convertAndSend("/topic/result", result);
        broadcastLogs();
    }

    private void broadcastLogs() {
        messagingTemplate.convertAndSend("/topic/logs", getLogs());
    }
}
