# SmartThings
SmartThings Development

## <s>SevereWeatherAlert</s>

<s>Alarm, text, and push notification on changes in weather alert(s). Allows filtering for ignore list of alert(s) as well as alarm auto shut off.  

Note: This could have been setup to alert when message changed rather then type of alert, but sometimes same alert they seem to change often and not want alarm all night long (I like to sleep).</s>

## <s>DeviceEndpoints</s>

<s>Web Service application endpoint for switches as starting point.

Note: Was being developed for Amazon Echo but now is supported.</s>

### <s>smartThings.js</s>

<s>Example code for reaching endpoint.</s>

### <s>token.js</s>

<s>Example code for retrieving token for access into endpoint.</s>

## <s>AlarmResetter</s>

<s>Resets alarm if active in reset period with delay period before disarm.</s>

## ModeSwitchRoutine

When mode changes, will also activate routine. Since routines already activate mode, this allows the other direction.

## ModeSwitchActivator

When selected mode change occurs (e.g. home, away, night) then switch(es) are activated and in any other mode are deactivated.

## ModeLocker

Monitors switches for on/active to determine if mode does not get changed during normal location cycles. Can use simulated switch for phone app or actual switch to lock "home" location for example.

## Device Logger

Does "put" request to send device name and value to local server for logging [WebApplication-Server](https://github.com/justinlhudson/Web-Application)

`http://<hostname>:<port>/api/st/log`

## ModeWatcher

Watches for zone 1 entry into zone 2 and if no entry into zone 2 will revert back to location mode before zone 1 was entered.

### Story

If Night mode changes to Home mode with zone 1 sensing presence (making the transition). Yet zone 2 in Night mode is sensing intruder.  If zone 1 goes active changing location mode, but zone 2 is not passed within window peroid, will revert to location mode prior to zone 1 presence.  

This solves my problem where bedroom hallway will change location mode to Home (so can take dog out).  However, if just going to bathroom and back want location mode to go back quickly if never actually went downstairs (to take dog out).

## Virtual Devices

Lets turn on/off devices without actually having any for controlling other application operations states.
