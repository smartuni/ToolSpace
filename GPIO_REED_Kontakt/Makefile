# name of your application
APPLICATION = GPIO_TEST

# If no BOARD is found in the environment, use this default:
BOARD ?= native
FEATURES_REQUIRED = periph_i2c periph_gpio

# This has to be the absolute path to the RIOT base directory:
RIOTBASE ?= $(CURDIR)/../RIOT

# Comment this out to disable code in RIOT that does safety checking
# which is not needed in a production environment but helps in the
# development process:
CFLAGS += -DDEVELHELP

# Change this to 0 show compiler invocation lines by default:
QUIET ?= 1

# Modules to include:
USEMODULE += shell
USEMODULE += shell_commands
USEMODULE += ps


include $(RIOTBASE)/Makefile.include
