/**
 * String Processing Demo — app.js
 * Handles API calls, WebSocket, UI updates
 */

// ==================== WebSocket ====================
let stompClient = null;

document.addEventListener('DOMContentLoaded', () => {
    connectWebSocket();
    // Open first op-card in each panel by default
    document.querySelector('#panelSB .op-card')?.classList.add('open');
    document.querySelector('#panelST .op-card')?.classList.add('open');
});

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; // suppress console noise

    stompClient.connect({}, () => {
        setConnectionBadge('connected');

        stompClient.subscribe('/topic/result', msg => {
            const result = JSON.parse(msg.body);
            renderResult(result);
        });
        stompClient.subscribe('/topic/logs', msg => {
            const logs = JSON.parse(msg.body);
            renderLogs(logs);
        });
    }, () => {
        setConnectionBadge('error');
        setTimeout(connectWebSocket, 5000);
    });
}

function setConnectionBadge(state) {
    const badge = document.getElementById('connectionBadge');
    if (state === 'connected') {
        badge.innerHTML = '<span class="dot dot-connected"></span> Connected';
    } else if (state === 'error') {
        badge.innerHTML = '<span class="dot dot-error"></span> Reconnecting…';
    } else {
        badge.innerHTML = '<span class="dot dot-connecting"></span> Connecting…';
    }
}

// ==================== Tab Switch ====================
function switchTab(tab) {
    document.getElementById('panelSB').classList.toggle('hidden', tab !== 'sb');
    document.getElementById('panelST').classList.toggle('hidden', tab !== 'st');
    document.getElementById('tabSB').classList.toggle('active', tab === 'sb');
    document.getElementById('tabST').classList.toggle('active', tab === 'st');
}

// ==================== Op-Card toggle ====================
function toggleOp(id) {
    const card = document.getElementById(id);
    card.classList.toggle('open');
}

// ==================== StringBuffer Operations ====================

async function doAppend() {
    const original = document.getElementById('sbInput').value;
    const value    = document.getElementById('appendValue').value;
    const res = await post('/api/stringbuffer/append', { original, value });
    if (res) {
        renderResult(res);
        showCode('append', original, { value });
    }
}

async function doInsert() {
    const original = document.getElementById('sbInput').value;
    const index    = parseInt(document.getElementById('insertIndex').value);
    const value    = document.getElementById('insertValue').value;
    const res = await post('/api/stringbuffer/insert', { original, index: String(index), value });
    if (res) {
        renderResult(res);
        showCode('insert', original, { index, value });
    }
}

async function doDelete() {
    const original = document.getElementById('sbInput').value;
    const start    = parseInt(document.getElementById('deleteStart').value);
    const end      = parseInt(document.getElementById('deleteEnd').value);
    const res = await post('/api/stringbuffer/delete', { original, start: String(start), end: String(end) });
    if (res) {
        renderResult(res);
        showCode('delete', original, { start, end });
    }
}

async function doReverse() {
    const original = document.getElementById('sbInput').value;
    const res = await post('/api/stringbuffer/reverse', { original });
    if (res) {
        renderResult(res);
        showCode('reverse', original, {});
    }
}

async function doReplace() {
    const original    = document.getElementById('sbInput').value;
    const start       = parseInt(document.getElementById('replaceStart').value);
    const end         = parseInt(document.getElementById('replaceEnd').value);
    const value       = document.getElementById('replaceValue').value;
    const res = await post('/api/stringbuffer/replace', { original, start: String(start), end: String(end), value });
    if (res) {
        renderResult(res);
        showCode('replace', original, { start, end, value });
    }
}

async function doLength() {
    const original = document.getElementById('sbInput').value;
    const res = await post('/api/stringbuffer/length', { original });
    if (res) {
        renderResult(res);
        showCode('length', original, {});
    }
}

async function doCharAt() {
    const original = document.getElementById('sbInput').value;
    const index    = parseInt(document.getElementById('charAtIndex').value);
    const res = await post('/api/stringbuffer/charat', { original, index: String(index) });
    if (res) {
        renderResult(res);
        showCode('charAt', original, { index });
    }
}

// ==================== StringTokenizer Operations ====================

async function doTokenize() {
    const input     = document.getElementById('stInput').value;
    const delimiter = document.getElementById('stDelimiter').value || ' ';
    const res = await post('/api/tokenizer/tokenize', { input, delimiter });
    if (res) {
        renderResult(res);
        showCode('tokenize', input, { delimiter });
    }
}

async function doCountTokens() {
    const input     = document.getElementById('stInput').value;
    const delimiter = document.getElementById('stDelimiter').value || ' ';
    const res = await post('/api/tokenizer/count', { input, delimiter });
    if (res) {
        renderResult(res);
        showCode('countTokens', input, { delimiter });
    }
}

// ==================== Logs ====================

async function clearLogs() {
    await fetch('/api/logs/clear', { method: 'POST' });
    document.getElementById('logContainer').innerHTML =
        '<div class="log-entry system">Logs cleared</div>';
}

// ==================== Render Result ====================

function renderResult(result) {
    const card   = document.getElementById('resultCard');
    const badge  = document.getElementById('resultTypeBadge');
    const isSB   = result.operationType === 'STRINGBUFFER';
    const isST   = result.operationType === 'TOKENIZER';

    badge.textContent  = isSB ? 'StringBuffer' : 'StringTokenizer';
    badge.className    = 'result-type-badge ' + (isSB ? 'sb' : 'st');
    card.classList.add('has-result');

    if (result.success) {
        card.innerHTML = `
            <div class="result-inner">
                <div class="result-message">${escHtml(result.message)}</div>
                <div class="result-value ${isST ? 'st-val' : ''}">${escHtml(result.resultString ?? '')}</div>
            </div>`;
    } else {
        card.innerHTML = `
            <div class="result-inner">
                <div class="result-message" style="color:#ef4444">❌ ${escHtml(result.message)}</div>
            </div>`;
    }

    // Token visualization
    const tokenViz = document.getElementById('tokenViz');
    const chips    = document.getElementById('tokenChips');
    if (isST && result.tokens && result.tokens.length > 0) {
        tokenViz.classList.remove('hidden');
        chips.innerHTML = result.tokens.map((t, i) =>
            `<span class="token-chip"><span class="token-index">[${i}]</span>${escHtml(t)}</span>`
        ).join('');
    } else {
        tokenViz.classList.add('hidden');
    }
}

function renderLogs(logs) {
    const container = document.getElementById('logContainer');
    if (!logs || logs.length === 0) {
        container.innerHTML = '<div class="log-entry system">No operations yet</div>';
        return;
    }
    container.innerHTML = logs.map(log => {
        let cls = 'system';
        if (log.includes('StringBuffer')) cls = 'sb';
        else if (log.includes('StringTokenizer')) cls = 'st';
        else if (log.includes('Error')) cls = 'error';
        return `<div class="log-entry ${cls}">${escHtml(log)}</div>`;
    }).join('');
}

// ==================== Code Snippets ====================

const codeTemplates = {
    append: (orig, p) => `<span class="code-comment">// StringBuffer — append()</span>
<span class="code-class">StringBuffer</span> sb = <span class="code-keyword">new</span> <span class="code-class">StringBuffer</span>(<span class="code-string">"${orig}"</span>);
sb.<span class="code-method">append</span>(<span class="code-string">"${p.value}"</span>);
<span class="code-class">System</span>.out.<span class="code-method">println</span>(sb.<span class="code-method">toString</span>()); <span class="code-comment">// → "${orig}${p.value}"</span>`,

    insert: (orig, p) => `<span class="code-comment">// StringBuffer — insert()</span>
<span class="code-class">StringBuffer</span> sb = <span class="code-keyword">new</span> <span class="code-class">StringBuffer</span>(<span class="code-string">"${orig}"</span>);
sb.<span class="code-method">insert</span>(<span class="code-number">${p.index}</span>, <span class="code-string">"${p.value}"</span>);
<span class="code-class">System</span>.out.<span class="code-method">println</span>(sb.<span class="code-method">toString</span>());`,

    delete: (orig, p) => `<span class="code-comment">// StringBuffer — delete()</span>
<span class="code-class">StringBuffer</span> sb = <span class="code-keyword">new</span> <span class="code-class">StringBuffer</span>(<span class="code-string">"${orig}"</span>);
sb.<span class="code-method">delete</span>(<span class="code-number">${p.start}</span>, <span class="code-number">${p.end}</span>);
<span class="code-class">System</span>.out.<span class="code-method">println</span>(sb.<span class="code-method">toString</span>());`,

    reverse: (orig, p) => `<span class="code-comment">// StringBuffer — reverse()</span>
<span class="code-class">StringBuffer</span> sb = <span class="code-keyword">new</span> <span class="code-class">StringBuffer</span>(<span class="code-string">"${orig}"</span>);
sb.<span class="code-method">reverse</span>();
<span class="code-class">System</span>.out.<span class="code-method">println</span>(sb.<span class="code-method">toString</span>());`,

    replace: (orig, p) => `<span class="code-comment">// StringBuffer — replace()</span>
<span class="code-class">StringBuffer</span> sb = <span class="code-keyword">new</span> <span class="code-class">StringBuffer</span>(<span class="code-string">"${orig}"</span>);
sb.<span class="code-method">replace</span>(<span class="code-number">${p.start}</span>, <span class="code-number">${p.end}</span>, <span class="code-string">"${p.value}"</span>);
<span class="code-class">System</span>.out.<span class="code-method">println</span>(sb.<span class="code-method">toString</span>());`,

    length: (orig, p) => `<span class="code-comment">// StringBuffer — length()</span>
<span class="code-class">StringBuffer</span> sb = <span class="code-keyword">new</span> <span class="code-class">StringBuffer</span>(<span class="code-string">"${orig}"</span>);
<span class="code-keyword">int</span> len = sb.<span class="code-method">length</span>();
<span class="code-class">System</span>.out.<span class="code-method">println</span>(len); <span class="code-comment">// → ${orig.length}</span>`,

    charAt: (orig, p) => `<span class="code-comment">// StringBuffer — charAt()</span>
<span class="code-class">StringBuffer</span> sb = <span class="code-keyword">new</span> <span class="code-class">StringBuffer</span>(<span class="code-string">"${orig}"</span>);
<span class="code-keyword">char</span> c = sb.<span class="code-method">charAt</span>(<span class="code-number">${p.index}</span>);
<span class="code-class">System</span>.out.<span class="code-method">println</span>(c); <span class="code-comment">// → '${orig[p.index] ?? '?'}'</span>`,

    tokenize: (orig, p) => `<span class="code-comment">// StringTokenizer — tokenize with hasMoreTokens() + nextToken()</span>
<span class="code-class">StringTokenizer</span> st = <span class="code-keyword">new</span> <span class="code-class">StringTokenizer</span>(<span class="code-string">"${orig}"</span>, <span class="code-string">"${p.delimiter}"</span>);
<span class="code-keyword">while</span> (st.<span class="code-method">hasMoreTokens</span>()) {
    <span class="code-class">System</span>.out.<span class="code-method">println</span>(st.<span class="code-method">nextToken</span>());
}`,

    countTokens: (orig, p) => `<span class="code-comment">// StringTokenizer — countTokens()</span>
<span class="code-class">StringTokenizer</span> st = <span class="code-keyword">new</span> <span class="code-class">StringTokenizer</span>(<span class="code-string">"${orig}"</span>, <span class="code-string">"${p.delimiter}"</span>);
<span class="code-keyword">int</span> count = st.<span class="code-method">countTokens</span>();
<span class="code-class">System</span>.out.<span class="code-method">println</span>(count);`,
};

function showCode(op, original, params) {
    const tpl = codeTemplates[op];
    if (!tpl) return;
    document.getElementById('codeSnippet').innerHTML = tpl(original, params);
}

// ==================== Helpers ====================

async function post(url, body) {
    try {
        const resp = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        return await resp.json();
    } catch (err) {
        console.error('API error:', err);
        return null;
    }
}

function escHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}
