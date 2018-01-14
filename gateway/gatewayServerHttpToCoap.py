#!/usr/bin/env python3
"""
Very simple HTTP server in python.
Usage::
    ./dummy-web-server.py [<port>]
Send a GET request::
    curl http://localhost:3001?127.0.15.68!Hammer//01!0
Send a PUT request::
	curl -d "127.0.15.68!Hammer//01!0" -X PUT http://localhost:3001
"""

from http.server import BaseHTTPRequestHandler, HTTPServer
from aiocoap import *
import asyncio
import logging
import urllib
import codecs
import urllib.request
import urllib.parse

class S(BaseHTTPRequestHandler):
    def _set_headers(self,content):
        if (content and not content.isspace()):
            self.send_response(200)
            response = "200"
        else:
            response = "400" 	
            self.send_response(400)
        # Send response status code
        #self.send_response(200)
        # Send headers
        self.send_header('Content-type', 'text/plain')
        self.end_headers()
        return response

    def do_PUT(self):
        content_length = int(self.headers['Content-Length'])
        content = self.rfile.read(content_length)
        content.decode("utf-8")
		
		#example message
        #"127.0.15.68#Hammer//01#0"
		
		#split the message by the first '!'
        newContent = content.decode("utf-8").split('!',1)
		
        print (newContent)
        response = self._set_headers(content)
        print(response)
        self.wfile.write(bytes(response, "utf-8"))
		
        return response 
		
    def do_GET(self): # 'http://localhost?127.0.15.68!Hammer//01!0'
        o = urllib.parse.urlparse(self.path)
        print(o)
        print(o.query)
        content = o

        response = self._set_headers(content.query)
        print(response)
        self.wfile.write(bytes(response, "utf-8") )
		
        return response

def run(server_class=HTTPServer, handler_class=S, port=3001):
    # Server settings
    # Choose port 8080, for port 80, which is normally used for a http server, you need root access
    server_address = ('', port)
    #server_address = ('127.0.0.1', 8081)

    httpd = server_class(server_address, handler_class)
    print ('Starting http-server...')
    httpd.serve_forever()

if __name__ == "__main__":
    from sys import argv

    if len(argv) == 2:
        run(port=int(argv[1]))
run()