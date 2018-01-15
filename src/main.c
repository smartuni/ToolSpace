/**
 *
 * @file
 * @brief       This is the main function for the toolstation with a nfc bord, a gauge and a Reed Sensor at the workstation.
 * @author      Sabrina Sendel <sabrina.sendel@haw-hamburg.de>, Johannes Rohling <johannes.rohling@haw-hamburg.de> and Bila Ouedraogo <bila.ouedraogo@haw-hamburg.de
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

#include "periph/gpio.h"
#include "periph/adc.h"



#define RES             ADC_RES_10BIT
#define  LEDPIN  GPIO_PIN(0, 19) // PIN wird mit der Variable LED festgelegt


#define CROSSCOAP_PORT ("5683") // Angesprochener Coap Port

#define MIN(a, b) ((a) < (b) ? (a) : (b))

//Vordefinierung der GPIO_Pins
#define Pin_13 GPIO_PIN(PA13.port,PA13.pin)
#define Pin_28 GPIO_PIN(PA28.port,PA28.pin)


/* CoAP resources */
static const coap_resource_t _resources[] = {
    { "/riot/board", COAP_PUT, _riot_board_handler },
};	

/* Counts requests sent by CLI. */
static uint16_t req_count = 0;	

/* Verfuegbarkeit der Werkzeuge, Messung ueber Reed Kontakt
------------------------------------------------------------
*/

//Struktur für GPIO Parameter
typedef struct {
	int pin;
	int port;
} param_t;

//Vergeben der Parameter für die GPIOs (Port und Pin)
param_t PA13 = {.port = 0, .pin = 13};
param_t PA28 = {.port = 0, .pin = 28};
			  
	

static void cb(void *arg){
	//Callback mit Ausgabe des Ports, des Pins und des Status
	param_t * p = (param_t*)arg;
    printf("Port:\t%i\nPin:\t%i\nStatus:%d\n\n", p->port, p->pin, gpio_read(GPIO_PIN(p->port,p->pin) ) );
}

/*
-------------------------------------------------------------------
*/

/*Implementation des Servers
--------------------------------------------------------------------
*/

static ssize_t _riot_board_handler(coap_pkt_t *pdu, uint8_t *buf, size_t len)
{
    gcoap_resp_init(pdu, buf, len, COAP_CODE_CONTENT);
    /* write the RIOT board name in the response buffer */
    memcpy(pdu->payload, RIOT_BOARD, strlen(RIOT_BOARD));
	LED0_TOGGLE;
    return gcoap_finish(pdu, strlen(RIOT_BOARD), COAP_FORMAT_TEXT);
}
				  
/*
----------------------------------------------------------------------
*/

/*Senden ueber Coap als Client
---------------------------------------------------------------------
*/



// Der Response handler greift das das ack(Feedback vom Gateway auf)
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



 // Sende Befehl für das senden ueber Coap
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

//Coap put Befehl zum Übertagen der Daten
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



/* 				  
static void printbuff(char *buff, unsigned len)
{
    while (len) {
        len--;
        printf("%02x ", *buff++);
    }
    puts("");
}


 */
 
 /*
 Umformatierung des NFC Tags
 */
 
 
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

/*
------------------------------------------------------------------------------------------------
*/

/*
Messung des Dehnungsmessstreifen ueber den ADC und einen Widerstand
------------------------------------------------------------------------------------------------
*/


int gewicht_init_adc(void){
  for (int i = 0; i < ADC_NUMOF; i++) {
      if (adc_init(ADC_LINE(i)) < 0) {
          return 1;
      }
  }
  return 0;
}

int gewicht_read_adc(int pin){
  // Einlesen des ADC
  int sample = adc_sample(ADC_LINE(pin), RES);
  if (sample < 0) {
    // Fehler
      return -1;
  } else {
    return sample;
  }
}

/*
------------------------------------------------------------------------------------------------
*/


int main(void)
{
    static char data[16];
    static nfc_iso14443a_t card;
    static pn532_t pn532;
    unsigned len;
    int ret;
//	char werzeug_id[7]; // vorher 32


	
// Initialisierung des NFC Boards pn532
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

// Aufruf der verwendeten Funktionen in der while-Schleife
	
	gpio_init (LEDPIN, GPIO_OUT);
	adc_init(0);
	gewicht_init_adc();	
	int aktiv = 0;
	int output =0;
	

    while (1) {
        /* Delay not to be always polling the interface */
        xtimer_usleep(250000UL);
		
		
		gpio_init_int(Pin_13, GPIO_IN_PD, GPIO_BOTH, cb, &PA13 ); 
		gpio_init_int(Pin_28, GPIO_IN_PD, GPIO_BOTH, cb, &PA28 );
		// Hier sollte das senden der Werzeug pins an den Server implementiert werden.
		
		/*
		Messung des Dehnungsmessstreifen und senden per put Befehl an den Server
		----------------------------------------------------------------------------
		*/
		
		output = gewicht_read_adc(0);
		// LOG_INFO("Aktueller Wert: %i \n", output);
		char bestellung[16] = "Neue Schrauben!";
		if(output < 900 && aktiv!= 1)
		{
			//LOG_INFO("Schrauben nachbestellen put Befehl wird gesendet\n \n");
			put("fe80::1ac0:ffee:1ac0:ffee","/order", bestellung);
			aktiv = 1;
        } else {	
			aktiv = 0; 
			
		} 
	
		//Pruefung ob Karte vorhanden ist.
        ret = pn532_get_passive_iso14443a(&pn532, &card, 0x50);
        if (ret < 0) {
            LOG_DEBUG("no card\n");
            continue;
        }

		/*
		----------------------------------------------------------------------------
		*/				

		//Pruefung des verwendeten Kartentyps

        if (card.type == ISO14443A_MIFARE) {
            char key[] = { 0xff, 0xff, 0xff, 0xff, 0xff, 0xff };
            char data[32];
			int var = 0;

            //Ausgabe der Kartenbloecke
			for (int i = 0; i < 64; i++) {
                LOG_INFO("sector %02d, block %02d | ", i / 4, i);
				
                //Autoidentifizierung des Kartenmaterials
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
					char werzeug_id[7];
					memset(&werzeug_id[0], 0, sizeof(werzeug_id));
				  //  strcpy(testdaten, data);
					var = 1;
					//printf("TEST\n");
					//printbuff(data, 7);
					storebuff(data, 7, werzeug_id);
					put("fe80::1ac0:ffee:1ac0:ffee","/rent", werzeug_id); //Senden an den Server
					LOG_INFO("Gesendete Werzeug ID: %s\n", werzeug_id);
				}	
			var = 0;
			}
		}
        
        else {
            LOG_ERROR("unknown\n");
        }
    }

    return 0;
}
