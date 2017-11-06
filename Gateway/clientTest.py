import logging
import asyncio
import json

from aiocoap import *

logging.basicConfig(level=logging.INFO)

async def main():
	protocol = await Context.create_client_context()
	
	msg = Message(code=GET, uri='coap://[fe80::7b65:364c:7034:34a6%lowpan0]/fence/info')
		#Message(code=GET, uri='coap://[' + hostname + '%lowpan0]/temperature', mtype=NON)
	
	try:
		response = await protocol.request(msg).response
	except Exception as e:
		print('Failed to fetch resource:')
		print(e)
	else:
		print('Result: %s\n%r'%(response.code, response.payload))

if __name__ == "__main__":
    asyncio.get_event_loop().run_until_complete(main())

	
#To serve more than one resource on a site, use the Site class to dispatch requests based on the Uri-Path header. 
#aiocoap.resource.hashing_etag(request, response) Helper function for render_get handlers that allows them to use 
#ETags based on the payload’s hash value Run this on your request and response before returning from render_get; it is safe to use this function with 
#all kinds of responses, 
#it will only act on 2.05 Content. 
#The hash used are the ﬁrst 8 bytes of the sha1 sum of the payload. Note that this method is not ideal from a server performance point of view 
#(a ﬁle server, for example, might wanttohashonlythestat()resultofaﬁleinsteadofreadingitinfull),butitsavesbandwithforthesimplecases.

#>>> from aiocoap import * 
#>>> req = Message(code=GET) 
#>>> hash_of_hello = b'\xaa\xf4\xc6\x1d\xdc\xc5\xe8\xa2' 
#>>> req.opt.etags = [hash_of_hello] 
#>>> resp = Message(code=CONTENT) >>> resp.payload = b'hello' 
#>>> hashing_etag(req, resp) 
#>>> resp <aiocoap.Message at ... 2.03 Valid ... 1 option(s)>
