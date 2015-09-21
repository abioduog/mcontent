//add timestamps in front of log messages
require('console-stamp')(console, 'HH:MM:ss.l');

const PORT=13013; 
const PATH="/cgi-bin/sendsms";

var http = require('http');
var url = require('url');
var dispatcher = require('httpdispatcher');

dispatcher.onGet(PATH, function(req, res) {
    res.writeHead(200, {'Content-Type': 'text/plain'});
	url_parts = url.parse(req.url, true);
    res.end('');
	console.log(url_parts.query);
});    

function handleRequest(request, response){
    try {
        console.log(request.url);
        dispatcher.dispatch(request, response);
    } catch(err) {
        console.log(err);
    }}

//create and start our server
var server = http.createServer(handleRequest);
server.listen(PORT, function(){
    console.log("Server listening on: http://localhost:%s", PORT);
});