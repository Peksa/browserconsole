var bconsole = {
  req: null,
  context: {
    lastId: null
  },
  term: null,
  token: null,
  url: "/api",
  timeout: 20000,
  wait: 50,

  terminalSettings: {
    greetings: "            Welcome to browserconsole",
    prompt: "            » "
  },

  refresh: function() {
    bconsole.req = $.ajax({
      url: bconsole.url + "/subscribe/" + bconsole.token,
      timeout: bconsole.timeout,
      data: bconsole.context
    }).done(function(data) {
      bconsole.parse(data);
      setTimeout(function() {
        bconsole.refresh();
      },
      bconsole.wait);
    }).fail(function(data) {
      if (data.statusText != "timeout") return;
      setTimeout(function() {
        bconsole.refresh();
      },
      bconsole.wait);
    });
  },

  parse: function(res) {
    if (!res) return;
    bconsole.parseAll(res.messages);
    bconsole.context.lastId = res.lastId;
  },

  parseAll: function(messages) {
    for (var i = 0; i < messages.length; i++) {
      bconsole.parseOne(messages[i]);
    }
  },

  parseOne: function(message) {
    if (message.response) {
      bconsole.echo(message);
    } else if (message.command) {
      bconsole.run(message);
    }
  },

  echo: function(message) {
    var color;
    if (/firefox/i.test(message.browser)) {
      color = "#f96";
    } else if (/ie/i.test(message.browser)) {
      color = "#8cf";
    } else if (/chrome/i.test(message.browser)) {
      color = "#7d5";
    } else if (/opera/i.test(message.browser)) {
      color = "#f66";
    } else if (/safari/i.test(message.browser)) {
      color = "#bcd";
    } else {
      color = "#fff";
    }

    bconsole.term.echo("[[;" + color + ";#333]" + bconsole.pad(" ", 12-message.browser.length) + message.browser + ": " + message.response + "]");
  },

  pad: function(char, length) {
    if (length < 0) return;
    return Array(length+1).join(char);
  },

  run: function(message) {
    try {
      var result = window.eval(message.command) + "";
      bconsole.postResponse(result, message.id);
    } catch (e) {
      bconsole.postResponse(e + "", message.id);
    }
  },

  postResponse: function(response, forId) {
    $.ajax({
      url: bconsole.url + "/response/" + bconsole.token,
      data: JSON.stringify({
        response: response + "",
        forId: forId
      }),
      type: 'POST',
      contentType: 'application/json'
    });
  },

  parseCommand: function(data) {
    if (!data) return;
    $.ajax({
      url: bconsole.url + "/request/" + bconsole.token,
      data: JSON.stringify({
        command: data
      }),
      type: 'POST',
      contentType: 'application/json'
    });
  },

  abort: function() {
    if (bconsole.req) {
      bconsole.req.abort();
    }
  },

  init: function() {
    bconsole.initHash();
    bconsole.initTerminal();
    bconsole.abort();
    bconsole.context.lastId = null;
    bconsole.refresh();
  },

  initTerminal: function() {
    bconsole.term = $('#terminal').terminal(bconsole.parseCommand, bconsole.terminalSettings);
  },

  initHash: function() {
    if (location.hash && /^#[a-z0-9]{12}$/.test(location.hash)) {
      bconsole.token = location.hash.substring(1);

    } else {
      bconsole.token = bconsole.generateRandomToken();
      location.hash = "#" + bconsole.token;
    }
  },

  generateRandomToken: function() {
    return 'xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  },

  stop: function() {
    bconsole.abort();
  }
};