#!/usr/bin/env python3
"""
Very simple HTTP to Coap Server in Python.
CURL Testing::
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
    def _set_headers(self,content): # setting header for http
        if (content and not content.isspace()): #non empty message
		    # Send response status code
            self.send_response(200)
            response = "200"
        else:									#empty message
		    # Send response status code
            response = "400" 	
            self.send_response(400)
			
        # Send headers
        self.send_header('Content-type', 'text/plain')
        self.end_headers()
		
        return response

    def do_PUT(self):
	    #read request content
        content_length = int(self.headers['Content-Length'])
        content = self.rfile.read(content_length)
        content.decode("utf-8")
		
		#example message
        #"127.0.15.68!Hammer//01!0"
		
		#split the message by the first '!'
        newContent = content.decode("utf-8").split('!',1)
		
        print (newContent)
        #response = self._set_headers(content)
        response = self.coap_put(newContent)
        #self.send_header('Content-type', 'text/plain')
        #self.end_headers()
        print(response)
        self.wfile.write(bytes(response, "utf-8"))
		
        return response 
	
    async def coap_put(self,content):
        response = asyncio.get_event_loop().run_until_complete(self.coap_putTransform(content))
        return response

    async def coap_putTransform(self,content):
        """Perform a single PUT request to localhost on the default port, URI
        "/other/block". The request is sent 2 seconds after initialization.

        The payload is bigger than 1kB, and thus sent as several blocks."""

        context = await Context.create_client_context()

        await asyncio.sleep(2)

        payload = content[1]
        # 3 = PUT
        request = Message(code=3, payload=payload)
        # These direct assignments are an alternative to setting the URI like in
        #request.opt.uri_host = 'fe80::7b65:364c:7034:34a6%lowpan0'
		
        request.opt.uri_host = content[0] + '%lowpan0'
		#optional
        #request.opt.uri_path = ("other", "block")

        response = await context.request(request).response

        print('Result: %s\n%r' % (response.code, response.payload))

        return response
		
    def do_GET(self):
		#parse url query
        content = urllib.parse.urlparse(self.path)
        print(content)
        print(content.query)

        
		
		#example message
        #"127.0.15.68!Hammer//01!0"
		
		#split the message by the first '!'
        newContent = content.query.split('!',1)
        print (newContent)
        #response = self._set_headers(content.query)
        
        response = self.coap_get(newContent)
        #self.send_header('Content-type', 'text/plain')
        #self.end_headers()
        print(response)
        self.wfile.write(bytes(response, "utf-8") )
		
        return response

    async def coap_get(self,content):
        response = asyncio.get_event_loop().run_until_complete(self.coap_getTransform(content))
        return response

    async def coap_getTransform(self,content):
        """Perform a single PUT request to localhost on the default port, URI
        "/other/block". The request is sent 2 seconds after initialization.

        The payload is bigger than 1kB, and thus sent as several blocks."""

        context = await Context.create_client_context()

        await asyncio.sleep(2)

        payload = content[1]
        # 1 = GET
        request = Message(code=1, payload=payload)
        # These direct assignments are an alternative to setting the URI like in
        #request.opt.uri_host = 'fe80::7b65:364c:7034:34a6%lowpan0'
		
        request.opt.uri_host = content[0] + '%lowpan0'
		#optional
        #request.opt.uri_path = ("other", "block")

        response = await context.request(request).response

        print('Result: %s\n%r' % (response.code, response.payload))

        return response
		
		
def run(server_class=HTTPServer, handler_class=S, port=3001):
    # Server settings
    # Choose port 8080, for port 80, which is normally used for a http server, you need root access
    server_address = ('', port) # set adress and port for the server
    #server_address = ('127.0.0.1', 8081)

    httpd = server_class(server_address, handler_class)
    print ('Starting http-server...')
    httpd.serve_forever()

if __name__ == "__main__":
    from sys import argv

    if len(argv) == 2:
        run(port=int(argv[1]))
run()