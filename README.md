# SmartThings
SmartThings Development

## SevereWeatherAlert

Alarm, text, and push notification on changes in weather alert(s). Allows filtering for ignore list of alert(s) as well as alarm auto shut off.

## DeviceEndpoints

Web Service application endpoint for switches as starting point.

Note: Was being developed for Amazon Echo but now is supported.

### smartThings.js

Example code for reaching endpoint.

### token.js

Example code for retrieving token for access into endpoint.

## AlarmResetter

Resets alarm if active in reset period with delay period before disarm.

## ModeLocker

Monitors switches for on/active to determine if mode does not get changed during normal cycle.  Can use simulated switch for phone app or actual switch to lock "home" location for example.

## Device Logger

Does "put" request to send device name and value to local server for logging [WebApplication-Server](https://github.com/justinlhudson/WebApplication-Server)

## ModeWatcher

Watches for zone 1 entry into zone 2 and if no entry into zone 2 will revert back to location mode before zone 1 was entered.

### Story

If Night mode changes to Home mode with zone 1 sensing presence (making the transition). Yet zone 2 in Night mode is sensing intruder.  If zone 1 goes active changing location mode, but zone 2 is not passed within window peroid, will revert to location mode prior to zone 1 presence.  

This solves my problem where bedroom hallway will change location mode to Home (so can take dog out).  However, if just going to bathroom and back want location mode to go back quickly if never actually went downstairs (to take dog out).

## Virtual Devices

Lets turn on/off devices without actually having any for controlling other application operations states.
