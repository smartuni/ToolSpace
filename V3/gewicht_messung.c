
#include "gewicht_messung.h"



int gewicht_init_adc(void){
  for (int i = 0; i < 3; i++) {
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

int gewicht_sensor_ausgabe (void)
{
	int output = gewicht_read_adc(0);
		if(output > 900)
		{
			LOG_INFO("Schrauben nachbestellen put Befehl wird gesendet\n \n");
			xtimer_usleep(5000000UL);
        } else {
			LOG_INFO("Aktueller Wert: %i \n", output);
			
		}  
	return 0;	
	
}




