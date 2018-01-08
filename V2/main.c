

/**
 * @ingroup tests
 * @{
 *
 * @file
 * @brief       This is the main function for the toolstation with a nfc bord
 *
 * @author      Sabrina Sendel <sabrina.sendel@haw-hamburg.de> and Bila Ouedraogo <bila.ouedraogo@haw-hamburg.de
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


/* Voltage_Control */
//#include "thread_flags.h"
#include "periph/gpio.h"
#include "periph/adc.h"


#define LOG_LEVEL LOG_INFO
#include "log.h"

#define CROSSCOAP_PORT ("5683")

#define MIN(a, b) ((a) < (b) ? (a) : (b))

#define RES             ADC_RES_10BIT

//#define ADC_NUMOF 16 /*Sonst Error*/

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
  len = gcoap_finish(&pdu, strlen(data), COAP_FORMAT_TEXT);
  printf("gcoap_cli: sending msg ID %u, %u bytes\n", coap_get_id(&pdu),
         (unsigned) len);
  if (!_send(&buf[0], len, adr, CROSSCOAP_PORT)) {
      puts("gcoap_cli: msg send failed");
  }
  return 1;

}

#define  LEDPIN  GPIO_PIN(0, 19) // PIN wird mit der Variable LED festgelegt

int init_adc(void){
  for (int i = 0; i < 3; i++) {
      if (adc_init(ADC_LINE(i)) < 0) {
          return 1;
      }
  }
  return 0;
}



int main(void)
{
    static char data[16];
    static nfc_iso14443a_t card;
    static pn532_t pn532;
    unsigned len;
    int ret;
	char testdaten[64];
	
	//int test;
	//char temp_wert[10]; 
	//int test = 0;
	gpio_init (LEDPIN, GPIO_OUT);
	adc_init(0);
	
	
/* 	const adc_conf_chan_t adc_channels[]
	{	
    
    {GPIO_PIN(PA, 18), ADC_INPUTCTRL_MUXPOS_PIN6},      
    {GPIO_PIN(PA, 19), ADC_INPUTCTRL_MUXPOS_PIN7},      
	} */

	
	

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
            char data[64];
			int var = 0;
			//char zeichen[64] = "T1:";
			//char zeichenz[64] = "\"";
			//char ziel [64];

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
                    printbuff(data, 64);
                }
                else {
                    LOG_ERROR("read\n");
                    break;
                }
				if (var == 0 && i == 0) {
				    strcpy(testdaten, data);
					var = 1;
					printf("TEST\n");
					printbuff(testdaten, 64);
					//strcat(zeichen, data);
					//strcpy(ziel, zeichen);
					//strcat(ziel, zeichenz);
					//printbuff(ziel, 64);
					put("fe80::1ac0:ffee:1ac0:ffee","/sensor", testdaten);
					gpio_toggle(LEDPIN);
					//gpio_set(LEDPIN);
					
					 //test = adc_sample(ADC_LINE(0), RES);
					//sprintf("ADC_WERT: %d", test );
					
				int sample = adc_sample(ADC_LINE(0), RES);
                sprintf(temp_wert, "%d", sample);
					
					}
            }
			var = 0;

        }
        else {
            LOG_ERROR("unknown\n");
        }
    }

    return 0;
}
