#ifndef COAP_SEND_H
#define COAP_SEND_H


#include "msg.h"
#include "net/gcoap.h"
#include "od.h"

#define CROSSCOAP_PORT ("5683")

 void _resp_handler(unsigned req_state, coap_pkt_t* pdu,
                          sock_udp_ep_t *remote);
					
 size_t _send(uint8_t *buf, size_t len, char *addr_str, char *port_str);
 
 int put(char *adr, char *pth, char *data);
 
 
 #endif /* COAP_SEND_H */