#!/usr/bin/env python3
"""
Very simple HTTP server in python.
Usage::
    ./dummy-web-server.py [<port>]
Send a GET request::
    curl http://localhost
Send a HEAD request::
    curl -I http://localhost
Send a POST request::
    curl -d "foo=bar&bin=baz" http://localhost
Send a PUT request::
	curl -d "foo" -X PUT http://localhost
"""

# turn python data structure into json data structure
#json_str = json.dumps(data)

# turn json data structure into python data structure
#data = json.loads(json_str)

from http.server import BaseHTTPRequestHandler, HTTPServer
import urllib
import codecs
#import jsonEncoder

class S(BaseHTTPRequestHandler):     
    def _set_headers(self,content):
        if (content and not content.isspace()):
            self.send_response(200)
            response = "200"
        else:
            response = "400" #richtige ausgabe implementieren	
            self.send_response(400)
		# Send response status code
        #self.send_response(200)
		# Send headers
        self.send_header('Content-type', 'text/plain')
        self.end_headers()	
        return response
		
    def do_POST(self):
        # Doesn't do anything with posted data
        content_length = int(self.headers['Content-Length']) # <--- Gets the size of data
        content = self.rfile.read(content_length) # <--- Gets the data itself
        content.decode("utf-8")
        print (content)
        response = self._set_headers(content)
        self.wfile.write(str.encode(response))		
		
    def do_PUT(self):
	    # Doesn't do anything with put data
        content_length = int(self.headers['Content-Length'])
        content = self.rfile.read(content_length)
        content.decode("utf-8")
        print (content)
        response = self._set_headers(content)
        self.wfile.write(str.encode(response))
		
def run(server_class=HTTPServer, handler_class=S, port=80):
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