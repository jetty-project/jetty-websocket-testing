if (!window.WebSocket && window.MozWebSocket) {
    window.WebSocket = window.MozWebSocket;
}

if (!window.WebSocket) {
    alert("WebSocket not supported by this browser");
}

if (!window.Node){
    var Node =
        {
          ELEMENT_NODE                :  1,
          ATTRIBUTE_NODE              :  2,
          TEXT_NODE                   :  3,
          CDATA_SECTION_NODE          :  4,
          ENTITY_REFERENCE_NODE       :  5,
          ENTITY_NODE                 :  6,
          PROCESSING_INSTRUCTION_NODE :  7,
          COMMENT_NODE                :  8,
          DOCUMENT_NODE               :  9,
          DOCUMENT_TYPE_NODE          : 10,
          DOCUMENT_FRAGMENT_NODE      : 11,
          NOTATION_NODE               : 12
        };
}

function $() {
    return document.getElementById(arguments[0]);
}
function $F() {
    return document.getElementById(arguments[0]).value;
}

function getKeyCode(ev) {
    if (window.event)
        return window.event.keyCode;
    return ev.keyCode;
}

var wstool = {
    connect : function() {
        var location = document.location.toString().replace('http://', 'ws://')
                .replace('https://', 'wss://') + "command";

        wstool.info("Document URI: " + document.location);
        wstool.info("WS URI: " + location);
        
        this._scount = 0;

        try {
            this._ws = new WebSocket(location);
            this._ws.onopen = this._onopen;
            this._ws.onmessage = this._onmessage;
            this._ws.onclose = this._onclose;
            this._ws.onerror = this._onerror;
        } catch (exception) {
            wstool.info("Connect Error: " + exception);
        }
    },

    close : function() {
        this._ws.close();
    },
    
    _out : function(css, message) {
        var console = $('console');
        var spanText = document.createElement('span');
        spanText.className = 'text ' + css;
        spanText.innerHTML = message;
        var lineBreak = document.createElement('br');
        console.appendChild(spanText);
        console.appendChild(lineBreak);
        console.scrollTop = console.scrollHeight - console.clientHeight;
        
        var children = console.childNodes;
        var extra = children.length - 1000;
        for(i=0; i<extra; i++) {
            children[i].parentNode.removeChild(children[i]);
        }
    },

    info : function(message) {
        wstool._out("info", message);
    },

    error : function(message) {
        wstool._out("error", message);
    },

    infoc : function(message) {
        wstool._out("client", "[c] " + message);
    },
    
    infos : function(message) {
        this._scount++;
        wstool._out("server", "[s" + this._scount + "] " + message);
    },

    setState : function(enabled) {
        $('connect').disabled = enabled;
        $('close').disabled = !enabled;
        if(!enabled) wstool._clearCommands();
    },
    
    _onopen : function() {
        wstool.setState(true);
        wstool.info("Websocket Connected");
        
        wstool._clearCommands();
        
        // Refresh new set of commands
        wstool._sendQuietly("categories");
    },
    
    _onerror : function(evt) {
        wstool.setState(false);
        wstool.error("Websocket Error: " + evt.data);
        wstool.error("See Javascript Console for possible detailed error message");
    },

    _send : function(message) {
        if (this._ws) {
            this._ws.send(message);
            wstool.infoc(message);
        }
    },
    
    _sendQuietly : function(message) {
        if (this._ws) {
            this._ws.send(message);
        }
    },
    
    _clearCommands : function() {
        // Clear out the existing commands
        var cmdDivs = document.querySelectorAll("#commands div");
        for(i=0; i<cmdDivs.length; i++) {
            cmdDivs[i].parentNode.removeChild(cmdDivs[i]);
        }
    },

    write : function(text) {
        wstool._send(text);
    },

    _onmessage : function(m) {
        if (m.data) {
            // TODO: figure out what is TEXT vs BINARY
            var txt = m.data;
            if(txt.startsWith("category:")) {
                var idx = txt.indexOf(':');
                wstool.addCommandCategory(txt.substring(idx+1));
            } else if(txt.startsWith("category-config:")) {
                var idx = txt.indexOf(':');
                wstool.addCommandCategoryConfig(txt.substring(idx+1));
            } else {
                wstool.infos(m.data);
            }
        }
    },

    _onclose : function(closeEvent) {
        this._ws = null;
        wstool.setState(false);
        wstool.info("Websocket Closed");
        wstool.info("  .wasClean = " + closeEvent.wasClean);
        
        var codeMap = {};
        codeMap[1000] = "(NORMAL)";
        codeMap[1001] = "(ENDPOINT_GOING_AWAY)";
        codeMap[1002] = "(PROTOCOL_ERROR)";
        codeMap[1003] = "(UNSUPPORTED_DATA)";
        codeMap[1004] = "(UNUSED/RESERVED)";
        codeMap[1005] = "(INTERNAL/NO_CODE_PRESENT)";
        codeMap[1006] = "(INTERNAL/ABNORMAL_CLOSE)";
        codeMap[1007] = "(BAD_DATA)";
        codeMap[1008] = "(POLICY_VIOLATION)";
        codeMap[1009] = "(MESSAGE_TOO_BIG)";
        codeMap[1010] = "(HANDSHAKE/EXT_FAILURE)";
        codeMap[1011] = "(SERVER/UNEXPECTED_CONDITION)";
        codeMap[1015] = "(INTERNAL/TLS_ERROR)";
        var codeStr = codeMap[closeEvent.code];
        wstool.info("  .code = " + closeEvent.code + "  " + codeStr);
        wstool.info("  .reason = " + closeEvent.reason);
    },
    
    addCommandCategory : function(categoryName) {
        // wstool.info("Add Category Name [" + categoryName + "]");

        var cmds = $('commands');
        var cmdDiv = document.createElement("div");
        cmdDiv.className = "command";
        cmds.appendChild(cmdDiv);
        
        var title = document.createElement("span");
        title.className = "title";
        title.innerHTML = categoryName;
        cmdDiv.appendChild(title);

        wstool._sendQuietly("category-config:" + categoryName);
    },

    addCommandCategoryConfig : function(configLine) {
        // wstool.info("Add Category Config [" + configLine + "]");
        var idx = configLine.indexOf(':');
        var categoryName = configLine.substring(0,idx);
        var config = configLine.substring(idx+1);

        var cmdDiv = wstool.findCommandDiv(categoryName);

        var btn = document.createElement("input");
        btn.className = "button";
        btn.type = "submit";
        btn.name = config;
        btn.value = config;
        btn.onclick = function() { wstool.write(config); }
        cmdDiv.appendChild(btn);
    },

    findCommandDiv : function(categoryName) {
        var cmdTitles = document.querySelectorAll("#commands div.command span.title");
        for(i=0; i<cmdTitles.length; i++) {
            if(cmdTitles[i].innerHTML == categoryName) {
                return cmdTitles[i].parentNode;
            }
        }
        return null;
    }
};
