/**
 *
 * @file
 * @brief       This is the main function for the toolstation with a nfc bord
 *
 * @author      Sabrina Sendel <sabrina.sendel@haw-hamburg.de> and Bila Ouedraogo <bila.ouedraogo@haw-hamburg.de
 *
 * @}
 */

//bibliotheken
#include "board.h"
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>
#include "string.h"

#include "NFC_sensor.h"
#include "coap_send.h"
#include "gewicht_messung.h" //fehlt noch
#include "reed_sensor.h" //fehlt noch

#include "kernel_types.h"
#include "thread.h"
#include "xtimer.h"
/* Unklare Bibliotheken
*/
#include "od.h" 
#include "fmt.h" 
#include "log.h"
/* Ende
*/


#define RCV_QUEUE_SIZE  (8)
#define LOG_LEVEL LOG_INFO


int main(void) {
	int nfc_init_pn532(void);
	
	/* Message read Thread Parameter*/
 msg_t msg;
 
      msg.content.value = 0;
      rcv_pid = thread_create(rcv_stack, sizeof(rcv_stack),
                              THREAD_PRIORITY_MAIN - 1, 0, rcv, NULL, "rcv");
 /* Message read Thread Parameter*/
	
	while (1) {
        /* Delay not to be always polling the interface */
        xtimer_usleep(250000UL);
		int nfc_tag_info(void);
		
		char daten[7];
		if(read_nfc_tag(char *nfcdata)){
			
			LOG_INFO("Gesendete Daten: %s\n", daten);
			put("fe80::1ac0:ffee:1ac0:ffee","/login", daten);
			
		 //	int sample = adc_sample(ADC_LINE(0), RES);
		//  sprintf(temp_wert, "%d", sample);
			
	        if (msg_try_send(&msg, rcv_pid) == 0) {
             printf("Receiver queue full.\n");
         }
         msg.content.value++;
            }
		if (mesg.content. == 202){
			
		}
			
/* Message receive*/			
        
        else {
            LOG_ERROR("unknown\n");
        }


    return 0;

		

}