var connect = require('connect'),
    url = require('url'),
    proxy = require('proxy-middleware'),
    modRewrite = require('connect-modrewrite'),
    serveStatic = require('serve-static'),
    http = require('http'),
    https = require('https'),
    fs = require('fs'),
    express = require('express'),
    nconf = require('nconf');

//get configuration from arguments
nconf.argv();

//Configure defaults for environment
nconf.defaults({
    env: 'dev'
});

//configure ports
//If dev uses 8000 as default redirect, 9000 as main
//If anything else(prod) uses 80 as default redirect, 443 as main (assuming HTTPS)
var env = nconf.get('env'),
    redirectPort = (env == 'dev') ? nconf.get('redirectPort') || '8000' : nconf.get('redirectPort') || '80',
    mainPort = (env == 'dev') ? nconf.get('mainPort') || '9000' : nconf.get('redirectPort') || '443';

console.log('Environment: ' + env);
console.log('Using port ' + redirectPort + ' as redirect and port ' + mainPort + ' as main.');

if (env == 'prod') {
//Create a server for redirecting unsecure requests
    var unsecureApp = express(),
        redirectServer = http.createServer(unsecureApp);
    unsecureApp.use(function requireHTTPS(req, res, next) {
        //redirect if not secure
        if (!req.secure) {
            return res.redirect('https://' + req.headers.host + req.url);
        }
        next();
    });
    redirectServer.listen(redirectPort);


//Configure tls options
    var secureOptions = {
        key: fs.readFileSync('key.pem'),
        cert: fs.readFileSync('cert.pem')
    };
}


//configure proxy to use back-end api server
var proxyOptions = url.parse('http://localhost/api');
proxyOptions.route = '/api';
proxyOptions.port = 8080;

var app = connect()
    .use(proxy(proxyOptions))
    /* Rewrite requests to ensure initial routes go through index(app), let angular take care of routing.
     Regex filter ensures static resources do not get rewritten
     */
    .use(modRewrite(['!\\.html|\\.js|\\.svg|\\.css|\\.png|\\.gif\\.jpg$ /index.html [L]']))
    //Make node act as a static server
    .use(serveStatic(require('path').resolve('src/main/webapp/')));

//create main server

if (env == 'prod') {
    https.createServer(secureOptions, app).listen(mainPort);
}
else {
    http.createServer(app).listen(mainPort)
}


