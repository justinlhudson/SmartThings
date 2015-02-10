# SmartThings
SmartThings Development

## AlarmResetter

Resets alarm if active on next cycle of interval, incase was thrown just before interval makes sure full cycle has alarm.  Can setup to still strobe...

## ModeWatcher

Watches for zone 1 entry into zone 2 and if no entry into zone 2 will revert back to location mode before zone 1 was entered.  

### Story

If Night mode changes to Home mode with zone 1 sensing presence (making the transition). Yet zone 2 in Night mode is sensing intruder.  If zone 1 goes active changing location mode, but zone 2 is not passed within window peroid, will revert to location mode prior to zone 1 presence.  

This solves my problem where bedroom hallway will change location mode to Home (so can take dog out).  However, if just going to bathroom and back want location mode to go back quickly if never actually went downstairs (to take dog out).

## Virtual Devices

Lets turn on/off devices without actually having any for controlling other application operations states.
