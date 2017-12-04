/*
 * Copyright (C) 2016 TriaGnoSys GmbH
 *
 * This file is subject to the terms and conditions of the GNU Lesser
 * General Public License v2.1. See the file LICENSE in the top level
 * directory for more details.
 */

/**
 * @ingroup tests
 * @{
 *
 * @file
 * @brief       Test application for the PN532 NFC reader
 *
 * @author      Víctor Ariño <victor.arino@triagnosys.com>
 *
 * @}
 */

#include "board.h"
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include "string.h"

#include "pn532.h"
#include "pn532_params.h"
#include "xtimer.h"
#include "net/gcoap.h"

#include "od.h"
#include "fmt.h"
#include "msg.h"
#include "kernel_types.h"

#define LOG_LEVEL LOG_INFO
#include "log.h"

/*COAP Server
#include "nanocoap.h"
#include "nanocoap_sock.h"
#define COAP_INBUF_SIZE (256U)

#define MAIN_QUEUE_SIZE     (8)
COAP Server*/


#define CROSSCOAP_PORT ("5683")

#define MIN(a, b) ((a) < (b) ? (a) : (b))

/*COAP Server
static msg_t _main_msg_queue[MAIN_QUEUE_SIZE];

// import "ifconfig" shell command, used for printing addresses 
extern int _netif_config(int argc, char **argv);
COAP */

static void _resp_handler(unsigned req_state, coap_pkt_t* pdu,
                          sock_udp_ep_t *remote);
						  

/* Counts requests sent by CLI. */
static uint16_t req_count = 0;						  

static void printbuff(char *buff, unsigned len)
{
    while (len) {
        len--;
        printf("%02x ", *buff++);
    }
    puts("");
}

static void _resp_handler(unsigned req_state, coap_pkt_t* pdu,
                          sock_udp_ep_t *remote)
{
    (void)remote;       /* not interested in the source currently */

    if (req_state == GCOAP_MEMO_TIMEOUT) {
        printf("gcoap: timeout for msg ID %02u\n", coap_get_id(pdu));
        return;
    }
    else if (req_state == GCOAP_MEMO_ERR) {
        printf("gcoap: error in response\n");
        return;
    }

    char *class_str = (coap_get_code_class(pdu) == COAP_CLASS_SUCCESS)
                            ? "Success" : "Error";
    printf("gcoap: response %s, code %1u.%02u", class_str,
                                                coap_get_code_class(pdu),
                                                coap_get_code_detail(pdu));
	LOG_DEBUG("Payloadlength:%i", pdu->payload_len);
    if (pdu->payload_len) {
        if (pdu->content_type == COAP_FORMAT_TEXT
                || pdu->content_type == COAP_FORMAT_LINK
                || coap_get_code_class(pdu) == COAP_CLASS_CLIENT_FAILURE
                || coap_get_code_class(pdu) == COAP_CLASS_SERVER_FAILURE) {
            /* Expecting diagnostic payload in failure cases */
            printf(", %u bytes\n%.*s\n", pdu->payload_len, pdu->payload_len,
                                                          (char *)pdu->payload);
        }
        else {
            printf(", %u bytes\n", pdu->payload_len);
            od_hex_dump(pdu->payload, pdu->payload_len, OD_WIDTH_DEFAULT);
        }
    }
    else {
        printf(", empty payload\n");
    }
}

/*COAP Server
static ssize_t _riot_value_handler(coap_pkt_t *pkt, uint8_t *buf, size_t len)
{
    ssize_t p = 0;
    char rsp[16];
    unsigned code = COAP_CODE_EMPTY;

    // read coap method type in packet 
    unsigned method_flag = coap_method2flag(coap_get_code_detail(pkt));

    switch(method_flag) {
    case COAP_GET:
        // write the response buffer with the internal value 
        p += fmt_u32_dec(rsp, internal_value);
        code = COAP_CODE_205;
        break;
    case COAP_PUT:
    case COAP_POST:
    {
        // convert the payload to an integer and update the internal value 
        char payload[16] = { 0 };
        memcpy(payload, (char*)pkt->payload, pkt->payload_len);
        internal_value = strtol(payload, NULL, 10);
        code = COAP_CODE_CHANGED;
    }
    }

    return coap_reply_simple(pkt, code, buf, len,
            COAP_FORMAT_TEXT, (uint8_t*)rsp, p);
}

// must be sorted by path (alphabetically) 
const coap_resource_t coap_resources[] = {
    COAP_WELL_KNOWN_CORE_DEFAULT_HANDLER,
    { "/riot/board", COAP_GET, _riot_board_handler },
    { "/riot/value", COAP_GET | COAP_PUT | COAP_POST, _riot_value_handler },
};

const unsigned coap_resources_numof = sizeof(coap_resources) / sizeof(coap_resources[0]);
COAP Server*/

void storebuff(char *buff, unsigned len, char *store)
{
//	char temp[1];
	char temp[0];	
	while (len){
		len--;
		sprintf(temp, "%02x", *buff++);
		strcat(store, temp);
	}
}

static size_t _send(uint8_t *buf, size_t len, char *addr_str, char *port_str)
{
    ipv6_addr_t addr;
    size_t bytes_sent;
    sock_udp_ep_t remote;

    remote.family = AF_INET6;
    remote.netif  = SOCK_ADDR_ANY_NETIF;

    /* parse destination address */
    if (ipv6_addr_from_str(&addr, addr_str) == NULL) {
        puts("gcoap_cli: unable to parse destination address");
        return 0;
    }
    memcpy(&remote.addr.ipv6[0], &addr.u8[0], sizeof(addr.u8));

    /* parse port */
    remote.port = atoi(port_str);
    if (remote.port == 0) {
        puts("gcoap_cli: unable to parse destination port");
        return 0;
    }

    bytes_sent = gcoap_req_send2(buf, len, &remote, _resp_handler);
    if (bytes_sent > 0) {
        req_count++;
    }
    return bytes_sent;
}


int put(char *adr, char *pth, char *data)
{
  uint8_t buf[GCOAP_PDU_BUF_SIZE];
  coap_pkt_t pdu;
  size_t len;

  gcoap_req_init(&pdu, &buf[0], GCOAP_PDU_BUF_SIZE, COAP_METHOD_PUT, pth);
  memcpy(pdu.payload, data, strlen(data));

  printf("Payload: %s\n", pdu.payload);
  len = gcoap_finish(&pdu, strlen(data), COAP_FORMAT_TEXT);
  printf("gcoap_cli: sending msg ID %u, %u bytes\n", coap_get_id(&pdu),
         (unsigned) len);
  if (!_send(&buf[0], len, adr, CROSSCOAP_PORT)) {
      puts("gcoap_cli: msg send failed");
  }
  return 1;

}


int main(void)
{
    static char data[16];
    static nfc_iso14443a_t card;
    static pn532_t pn532;
    unsigned len;
    int ret;
	char testdaten[7]; //vorher 32
//	char testdatenNeu[7]; // vorher 32
	

#if defined(PN532_SUPPORT_I2C)
    ret = pn532_init_i2c(&pn532, &pn532_conf[0]);
#elif defined(PN532_SUPPORT_SPI)
    ret = pn532_init_spi(&pn532, &pn532_conf[0]);
#else
#error None of PN532_SUPPORT_I2C and PN532_SUPPORT_SPI set!
#endif
    if (ret != 0) {
        LOG_INFO("init error %d\n", ret);
    }

    xtimer_usleep(200000);
    LOG_INFO("awake\n");

    uint32_t fwver;
    pn532_fw_version(&pn532, &fwver);
    LOG_INFO("ver %d.%d\n", (unsigned)PN532_FW_VERSION(fwver), (unsigned)PN532_FW_REVISION(fwver));


    ret = pn532_sam_configuration(&pn532, PN532_SAM_NORMAL, 1000);
    LOG_INFO("set sam %d\n", ret);
	
	/* COAP Server
	
	// nanocoap_server uses gnrc sock which uses gnrc which needs a msg queue 
    msg_init_queue(_main_msg_queue, MAIN_QUEUE_SIZE);

    puts("Waiting for address autoconfiguration...");
    xtimer_sleep(3);

    // print network addresses 
    puts("Configured network interfaces:");
    _netif_config(0, NULL);
	uint8_t buf[COAP_INBUF_SIZE];
    sock_udp_ep_t local = { .port=COAP_PORT, .family=AF_INET6 };
    nanocoap_server(&local, buf, sizeof(buf));
	 COAP Server*/
	
	

    while (1) {
        /* Delay not to be always polling the interface */
        xtimer_usleep(250000UL);

        ret = pn532_get_passive_iso14443a(&pn532, &card, 0x50);
        if (ret < 0) {
            LOG_DEBUG("no card\n");
            continue;
        }

        if (card.type == ISO14443A_TYPE4) {
            if (pn532_iso14443a_4_activate(&pn532, &card) != 0) {
                LOG_ERROR("act\n");
                continue;

            }
            else if (pn532_iso14443a_4_read(&pn532, data, &card, 0x00, 2) != 0) {
                LOG_ERROR("len\n");
                continue;
            }

            len = PN532_ISO14443A_4_LEN_FROM_BUFFER(data);
            len = MIN(len, sizeof(data));

            if (pn532_iso14443a_4_read(&pn532, data, &card, 0x02, len) != 0) {
                LOG_ERROR("read\n");
                continue;
            }

            LOG_INFO("dumping card contents (%d bytes)\n", len);
            printbuff(data, len);
            pn532_release_passive(&pn532, card.target);

        }
        else if (card.type == ISO14443A_MIFARE) {
            char key[] = { 0xff, 0xff, 0xff, 0xff, 0xff, 0xff };
            char data[32];
//			char *tuedelchen[1] = '"';
//			char *tuedelchen2[1] = '"';
			int var = 0;

            for (int i = 0; i < 64; i++) {
                LOG_INFO("sector %02d, block %02d | ", i / 4, i);
				
                if ((i & 0x03) == 0) {
                    ret = pn532_mifareclassic_authenticate(&pn532, &card,
                                                           PN532_MIFARE_KEY_A, key, i);
                    if (ret != 0) {
                        LOG_ERROR("auth\n");
                        break;
                    }
                }

                ret = pn532_mifareclassic_read(&pn532, data, &card, i);
                if (ret == 0) {
                    printbuff(data, 32);
                }
                else {
                    LOG_ERROR("read\n");
                    break;
                }
				if (var == 0 && i == 0) {
					char testdatenNeu[7];
					memset(&testdatenNeu[0], 0, sizeof(testdatenNeu)); 
				    strcpy(testdaten, data);
					var = 1;
					printf("TEST\n");
					printbuff(testdaten, 7);
					storebuff(testdaten, 7, testdatenNeu);
					put("fe80::1ac0:ffee:1ac0:ffee","/login", testdatenNeu);
					
            }
			var = 0;
		}}
        
        else {
            LOG_ERROR("unknown\n");
        }
    }

    return 0;
}
