var connect = require('connect');
var url = require('url');
var proxy = require('proxy-middleware');
var modRewrite = require('connect-modrewrite');
var serveStatic = require('serve-static');
var http = require('http');


var proxyOptions = url.parse('http://localhost/api');
proxyOptions.route='/api';
proxyOptions.port = 8080;

var app = connect()
    .use(proxy(proxyOptions))
    .use(modRewrite(['!\\.html|\\.js|\\.svg|\\.css|\\.png|\\.gif\\.jpg$ /index.html [L]']))
    .use(serveStatic(require('path').resolve('src/main/webapp/')));

http.createServer(app).listen(9000);

