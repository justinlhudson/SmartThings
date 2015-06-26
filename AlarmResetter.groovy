definition(
    name: "AlarmResetter",
    namespace: "Operations",
    author: "justinlhudson",
    description: "Resets Alarming (incase someone comes invited without unlocking)",
    category:  "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("Alarming") {
        input "alarms", "capability.alarm", title:"Reset alarms", multiple:true, required:true
        input "strobe", "bool", title:"Strobe?", description: "Stobe still?", defaultValue: true
//        input "siren", "bool", title:"Siren?", description: "Stobe still?", defaultValue: true
        input "delay", "number", title:"Active (seconds)", defaultValue:60
        input "reset", "number", title:"Reset (minutes)", defaultValue:5
        input "setMode", "mode", title:"Mode Revert", multiple:false, required:false
    }
}

def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    unschedule()
    
    initialize()
}

def clear() {
    if(settings.strobe != true) {
        settings.alarms*.off()
    }

    state.alarmActive = false

    sendNotificationEvent "Alarm(s) Reset..."
}

def set() {
    if(settings.strobe == true) {
        settings.alarms*.strobe()
        sendNotificationEvent "Alarm(s) Silented!"
    }
    else {
        settings.alarms*.off()
    }

    runIn(settings.reset*60, clear, [overwrite: true])
    //sendPushMessage
}

def alarmHandler(evt)
{
    log.debug "${evt.value}"
/*
    if(settings.siren == true && settings.strobe == true){
        settings.alarms*.both()
    }
    else if(settings.siren == true) {
        settings.alarms*.siren()
    }
    else if(settings.strobe == true){
        settings.alarms*.strobe()
    }
*/
    if( evt.value != "off" && state.alarmActive == false) {
        sendNotificationEvent "Alarm(s) Active!"

		if(settings.setMode != null && settings.setMode != "" ) {
        	log.debug "changed mode: ${settings.setMode}"
			setLocationMode(settings.setMode)
        }

        state.alarmActive = true
        runIn(settings.delay, set, [overwrite: true])
    }
}

private def initialize() {
      state.alarmActive = false
      subscribe(alarms, "alarm", alarmHandler)
}
