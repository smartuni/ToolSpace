#ifndef NFC_SENSOR_H
#define NFC_SENSOR_H

#include "pn532.h"
#include "pn532_params.h"
#include <string.h>

//Aufruf der NFC Funktionen

int nfc_init_pn532(void);

int nfc_tag_info(void);

int read_nfc_tag(char *nfcdata);

void storebuff(char *buff, unsigned len, char *store);

 static void printbuff(char *buff, unsigned len);
 
 #endif /*NFC_SENSOR_H */