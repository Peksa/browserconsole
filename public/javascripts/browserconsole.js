if (!Function.prototype.bind) {
  Function.prototype.bind = function (oThis) {
    if (typeof this !== "function") {
      // closest thing possible to the ECMAScript 5 internal IsCallable function
      throw new TypeError("Function.prototype.bind - what is trying to be bound is not callable");
    }
 
    var aArgs = Array.prototype.slice.call(arguments, 1), 
        fToBind = this, 
        fNOP = function () {},
        fBound = function () {
          return fToBind.apply(this instanceof fNOP && oThis
                                 ? this
                                 : oThis,
                               aArgs.concat(Array.prototype.slice.call(arguments)));
        };
 
    fNOP.prototype = this.prototype;
    fBound.prototype = new fNOP();
 
    return fBound;
  };
}

var Browserconsole = {
	req: null,
	context: {
		lastId: null
	},
	term: null,
	token: null,
	url: "/",
	timeout: 20000,
	wait: 50,
	
	terminalSettings: {
		greetings: "Welcome to browserconsole",
		prompt: "» "
	},
	
	refresh: function() {
		_this = this;
		this.req = $.ajax({
			url: _this.url + _this.token,
			timeout: this.timeout,
			data: _this.context
		}).done(function(data) {
			_this = _this;
			_this.parse(data);
			setTimeout(function() {
				_this = _this;
				_this.refresh();
			},
			_this.wait);
		}).fail(function(data) {
			if (data.statusText != "timeout")
				return;
			_this = _this;
			setTimeout(function() {
				_this = _this;
				_this.refresh();
			},
			_this.wait);
		});
	},
	
	parse: function(res) {
		if (!res) return;
		this.parseAll(res.messages);
		this.context.lastId = res.lastId;
	},
	
	parseAll: function(messages) {
		for (key in messages) {
			this.parseOne(messages[key]);
		}
	},
	
	parseOne: function(message) {
		if (message.response) {
			this.echo(message);
		} else if (message.command) {
			this.run(message);
		}
	},
	
	echo: function(message) {
		if (/firefox/i.test(message.browser)) {
			color = "#f96";
		} else if (/ie/i.test(message.browser)) {
			color = "#8cf";
		} else if (/chrome/i.test(message.browser)) {
			color = "#7d5";
		} else {
			color = "#fff";
		}
			
		this.term.echo("[[;" + color + ";#333]" + message.browser + ": " + message.response + "]");
	},
	
	run: function(message) {
		try {
        	var result = window.eval(message.command);
        	this.postResponse(new String(result), message.id);
		} catch (e) {
			this.postResponse(new String(e), message.id);
		}
	},
	
	postResponse: function(response, forId) {
		$.ajax({
			url: this.url + this.token + "/responses",
			data: JSON.stringify({ 
				response: new String(response),
				forId: forId
			}),
			type: 'POST',
			contentType: 'application/json'
		});
	},
	
	parseCommand: function(data) {
		if (!data) return;
		var _this = this;
		$.ajax({
			url: _this.url + _this.token + "/commands",
			data: JSON.stringify({ 
				command: data
			}),
			type: 'POST',
			contentType: 'application/json'
		});
	},
	
	abort: function() {
		if (this.req) {
			this.req.abort();
		}
	},
	
	init: function() {
		this.initHash();
		this.initTerminal();
		this.abort();
		this.context.lastId = null;
		this.refresh();
	},
	
	initTerminal: function() {
		var boundParseCommand = this.parseCommand.bind(this);
		this.term = $('#terminal').terminal(boundParseCommand, this.terminalSettings);
	},
	
	initHash: function() {
		if (!location.hash) {
			this.token = this.generateRandomToken();
			location.hash = "#" + this.token;
		} else {
			this.token = location.hash.substring(1);
		}
	},
	
	generateRandomToken: function() {
		return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
		    return v.toString(16);
		});
	},
	
	stop: function() {
		this.abort();
	}
}