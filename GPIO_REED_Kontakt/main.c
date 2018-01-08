/*
	Beispiel zur GPIO Belegung mit Interrupt am Board Amtel SAMR21x mit RIOT OS
	Autor: Johannes Roling
	Datum: 18.12.2017
	Verson: 1.00
*/
#include <stdio.h>
#include <stdlib.h>

#include "shell.h"
#include "periph/gpio.h"
#include "msg.h"

//Struktur für GPIO Parameter
typedef struct {
	int pin;
	int port;
} param_t;

//Vergeben der Parameter für die GPIOs (Port und Pin)
param_t PA13 = {.port = 0, .pin = 13};
param_t PA28 = {.port = 0, .pin = 28};

//Vordefinierung der GPIO_Pins
#define Pin_13 GPIO_PIN(PA13.port,PA13.pin)
#define Pin_28 GPIO_PIN(PA28.port,PA28.pin)

//Zum Aufrufen der Shell
static const shell_command_t shell_commands[] = {	{ NULL, NULL, NULL }	};


static void cb(void *arg){
	//Callback mit Ausgabe des Ports, des Pins und des Status
	param_t * p = (param_t*)arg;
    printf("Port:\t%i\nPin:\t%i\nStatus:%d\n\n", p->port, p->pin, gpio_read(GPIO_PIN(p->port,p->pin) ) );
}

int main (void){
	// Aktivierung PIN13, Definiert als in_Pulldown, reagiert auf beide Flanken, Übergabe des Pointers auf PA13
	gpio_init_int(Pin_13, GPIO_IN_PD, GPIO_BOTH, cb, &PA13 );
	gpio_init_int(Pin_28, GPIO_IN_PD, GPIO_BOTH, cb, &PA28 );
	// Initierung der Shell (Damit die main nicht endet. Alternative: while(1){xtimer.sleep;})
    char line_buf[SHELL_DEFAULT_BUFSIZE];
    shell_run(shell_commands, line_buf, SHELL_DEFAULT_BUFSIZE);
	
	return 0;
}