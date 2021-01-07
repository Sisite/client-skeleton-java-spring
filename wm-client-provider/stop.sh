#!/bin/bash
#More sleep time between these commands might be needed on slower devices like a Raspberry Pi (because of the database accesses)
echo Shutting down WM client
pkill -f wm-client-provider
echo WM client shutdown
