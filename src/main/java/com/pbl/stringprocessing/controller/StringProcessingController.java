package com.pbl.stringprocessing.controller;

import com.pbl.stringprocessing.model.StringResult;
import com.pbl.stringprocessing.service.StringProcessingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for String Processing operations.
 *
 * StringBuffer Endpoints:
 * - POST /api/stringbuffer/append
 * - POST /api/stringbuffer/insert
 * - POST /api/stringbuffer/delete
 * - POST /api/stringbuffer/reverse
 * - POST /api/stringbuffer/replace
 * - POST /api/stringbuffer/length
 * - POST /api/stringbuffer/charat
 *
 * StringTokenizer Endpoints:
 * - POST /api/tokenizer/tokenize
 * - POST /api/tokenizer/count
 *
 * Utility:
 * - GET  /api/logs
 * - POST /api/logs/clear
 * - GET  /         → Dashboard page
 */
@Controller
@CrossOrigin(origins = "*")
public class StringProcessingController {

    private final StringProcessingService service;

    public StringProcessingController(StringProcessingService service) {
        this.service = service;
    }

    // ==================== StringBuffer Endpoints ====================

    @PostMapping("/api/stringbuffer/append")
    @ResponseBody
    public StringResult append(@RequestBody Map<String, String> body) {
        String original  = body.getOrDefault("original", "");
        String toAppend  = body.getOrDefault("value", "");
        return service.appendOperation(original, toAppend);
    }

    @PostMapping("/api/stringbuffer/insert")
    @ResponseBody
    public StringResult insert(@RequestBody Map<String, String> body) {
        String original  = body.getOrDefault("original", "");
        String toInsert  = body.getOrDefault("value", "");
        int index        = Integer.parseInt(body.getOrDefault("index", "0"));
        return service.insertOperation(original, index, toInsert);
    }

    @PostMapping("/api/stringbuffer/delete")
    @ResponseBody
    public StringResult delete(@RequestBody Map<String, String> body) {
        String original = body.getOrDefault("original", "");
        int start       = Integer.parseInt(body.getOrDefault("start", "0"));
        int end         = Integer.parseInt(body.getOrDefault("end", "0"));
        return service.deleteOperation(original, start, end);
    }

    @PostMapping("/api/stringbuffer/reverse")
    @ResponseBody
    public StringResult reverse(@RequestBody Map<String, String> body) {
        String original = body.getOrDefault("original", "");
        return service.reverseOperation(original);
    }

    @PostMapping("/api/stringbuffer/replace")
    @ResponseBody
    public StringResult replace(@RequestBody Map<String, String> body) {
        String original     = body.getOrDefault("original", "");
        String replacement  = body.getOrDefault("value", "");
        int start           = Integer.parseInt(body.getOrDefault("start", "0"));
        int end             = Integer.parseInt(body.getOrDefault("end", "0"));
        return service.replaceOperation(original, start, end, replacement);
    }

    @PostMapping("/api/stringbuffer/length")
    @ResponseBody
    public StringResult length(@RequestBody Map<String, String> body) {
        String original = body.getOrDefault("original", "");
        return service.lengthOperation(original);
    }

    @PostMapping("/api/stringbuffer/charat")
    @ResponseBody
    public StringResult charAt(@RequestBody Map<String, String> body) {
        String original = body.getOrDefault("original", "");
        int index       = Integer.parseInt(body.getOrDefault("index", "0"));
        return service.charAtOperation(original, index);
    }

    // ==================== StringTokenizer Endpoints ====================

    @PostMapping("/api/tokenizer/tokenize")
    @ResponseBody
    public StringResult tokenize(@RequestBody Map<String, String> body) {
        String input     = body.getOrDefault("input", "");
        String delimiter = body.getOrDefault("delimiter", " ");
        return service.tokenizeOperation(input, delimiter);
    }

    @PostMapping("/api/tokenizer/count")
    @ResponseBody
    public StringResult countTokens(@RequestBody Map<String, String> body) {
        String input     = body.getOrDefault("input", "");
        String delimiter = body.getOrDefault("delimiter", " ");
        return service.countTokensOperation(input, delimiter);
    }

    // ==================== Logs Endpoints ====================

    @GetMapping("/api/logs")
    @ResponseBody
    public List<String> getLogs() {
        return service.getLogs();
    }

    @PostMapping("/api/logs/clear")
    @ResponseBody
    public Map<String, String> clearLogs() {
        service.clearLogs();
        return Map.of("message", "Logs cleared");
    }

    // ==================== Dashboard Page ====================

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
