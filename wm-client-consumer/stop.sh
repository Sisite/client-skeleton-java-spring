#!/bin/bash
#More sleep time between these commands might be needed on slower devices like a Raspberry Pi (because of the database accesses)
echo Shutting down WM client consumer
pkill -f wm-client-consumer
echo WM client consumer shutdown
