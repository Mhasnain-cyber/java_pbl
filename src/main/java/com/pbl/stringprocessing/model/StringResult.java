package com.pbl.stringprocessing.model;

import java.util.List;

/**
 * Model class representing the result of a String operation.
 *
 * Used to return structured data to the frontend/WebSocket.
 */
public class StringResult {

    /** Whether the operation succeeded */
    private boolean success;

    /** Human-readable result message */
    private String message;

    /** The resulting string after the operation */
    private String resultString;

    /** List of tokens (used for StringTokenizer results) */
    private List<String> tokens;

    /** Type of operation performed: STRINGBUFFER or TOKENIZER */
    private String operationType;

    // ==================== Constructors ====================

    public StringResult() {}

    public StringResult(boolean success, String message, String resultString,
                        List<String> tokens, String operationType) {
        this.success = success;
        this.message = message;
        this.resultString = resultString;
        this.tokens = tokens;
        this.operationType = operationType;
    }

    // ==================== Getters & Setters ====================

    public boolean isSuccess()                  { return success; }
    public void setSuccess(boolean success)     { this.success = success; }

    public String getMessage()                  { return message; }
    public void setMessage(String message)      { this.message = message; }

    public String getResultString()             { return resultString; }
    public void setResultString(String r)       { this.resultString = r; }

    public List<String> getTokens()             { return tokens; }
    public void setTokens(List<String> tokens)  { this.tokens = tokens; }

    public String getOperationType()            { return operationType; }
    public void setOperationType(String t)      { this.operationType = t; }
}
