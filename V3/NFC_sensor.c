#include "NFC_sensor.h"

int ret;
int check;
static nfc_iso14443a_t card;
static pn532_t pn532;

// Initialisierung des NFC Boards pn532

int nfc_init_pn532(void){

	#if defined(PN532_SUPPORT_I2C)
		ret = pn532_init_i2c(&pn532, &pn532_conf[0]);
	#elif defined(PN532_SUPPORT_SPI)
		ret = pn532_init_spi(&pn532, &pn532_conf[0]);
	#else
	#error None of PN532_SUPPORT_I2C and PN532_SUPPORT_SPI set!
	#endif
		if (ret != 0) {
			LOG_INFO("init error %d\n", ret);
			return check;		//Kontrolle
		}
		//Timer definition
    xtimer_usleep(200000);
    LOG_INFO("awake\n");
		
	uint32_t fwver;
    pn532_fw_version(&pn532, &fwver);
    LOG_INFO("ver %d.%d\n", (unsigned)PN532_FW_VERSION(fwver), (unsigned)PN532_FW_REVISION(fwver));


    ret = pn532_sam_configuration(&pn532, PN532_SAM_NORMAL, 1000);
    LOG_INFO("set sam %d\n", ret);	
	return check;			
}

// NFC Tag Info

int nfc_tag_info(void)
{
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
	
}

//Lesen des NFC Tags

int read_nfc_tag(char *nfcdata)

int ret;
{
	
	    //Prüfung ob Karte vorhanden ist
		ret = pn532_get_passive_iso14443a(&pn532, &card, 0x50);
		if (ret < 0) {
            LOG_DEBUG("no card\n");
            continue;
        }

		//Kartenschlüssel
        else if (card.type == ISO14443A_MIFARE) {
            char key[] = { 0xff, 0xff, 0xff, 0xff, 0xff, 0xff };
            char data[32];
			int var = 0;

			// Ausage der ersten 32 Blockspeicher zur Kontrolle
            for (int i = 0; i < 32; i++) {
                LOG_INFO("sector %02d, block %02d | ", i / 4, i);
				
                
				//autoidentifizierung des Lesematerials
				if ((i & 0x03) == 0) {
                    ret = pn532_mifareclassic_authenticate(&pn532, &card,
                                                           PN532_MIFARE_KEY_A, key, i);
                    if (ret != 0) {
                        LOG_ERROR("auth\n");
                        break;
                    }
                }
				
				//Einlesen eines 32bit Datenvektors

                ret = pn532_mifareclassic_read(&pn532, data, &card, i);
                if (ret == 0) {
                    printbuff(data, 32);
                }
                else {
                    LOG_ERROR("read\n");
                    break;
                }
	
}

 // Zwischenspeicher als buffer NFC lesen
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

//Ausgabe des Zwischenspeicher in der Konsole
 static void printbuff(char *buff, unsigned len)
{
    while (len) {
        len--;
        printf("%02x ", *buff++);
    }
    puts("");
}