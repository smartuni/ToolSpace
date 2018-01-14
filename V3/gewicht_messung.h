#ifndef GEWICHT_MESSUNG_H
#define GEWICHT_MESSUNG_H

#include "periph/gpio.h"
#include "periph/adc.h"


#define RES             ADC_RES_10BIT

int gewicht_init_adc(void);

int gewicht_read_adc(int pin);

int gewicht_umrechnung(int pin);

int gewicht_sensor_ausgabe(void);

#endif /* GEWICHT_MESSUNG_H */