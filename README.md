Browserconsole
================

Browserconsole is a [Play! framework 1.x](http://www.playframework.org/) application that allows you to test
pieces of JavaScript snippets in multiple browsers and return the result to one of the browsers in real-time.

This can allow you to easily test some of those weird browser quirks in different browsers simultaneously.

![image](https://cloud.githubusercontent.com/assets/903976/4484548/09d9d60e-49bd-11e4-929c-2c4561456b6f.png)

Demo
----
Try it yourself at [browserconsole.com](http://browserconsole.com). Open the same URL (notice the hash fragment
that will be generated) in multiple different browsers (the ones you want to test in) and try typing some
JavaScript.

The statements will be sent to the different browsers, where they will be evaluated and the results will be
returned to the browser that send the statement.

Run it yourself
---------------
If you'd like to host or run the entire application yourself, you're welcome to do so.

1. Install JDK 1.7
2. Install Play framework 1.2.7. Instructions [here](http://www.playframework.org/documentation/1.2.7/install).
3. Clone this repo, `git clone git://github.com/Peksa/browserconsole.git`
4. Install all dependencies, `play deps --sync`
5. Start the application, `play run`
6. Visit [http://localhost:9005](http://localhost:9005)
