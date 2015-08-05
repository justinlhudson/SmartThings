definition(
    name: "AlarmResetter",
    namespace: "Operations",
    author: "justinlhudson",
    description: "Resets Alarming (incase someone comes invited without unlocking)",
    category:  "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("Alarming...") {
        input "alarms", "capability.alarm", title:"Reset alarms", multiple:true, required:true
        input "strobe", "bool", title:"Strobe?", description: "Stobe still?", defaultValue: true
        input "delay", "number", title:"Active (seconds)", defaultValue:60
        input "reset", "number", title:"Reset (seconds)", defaultValue:60
    }
}

def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def alarms_both() {
    log.debug "alarms_both"
    settings.alarms?.both()
    pause(1000)
    settings.alarms.each {
    while ( it != null && it.latestValue("alarm") != "both") {
      it.both()
        pause(1000)
      }
    }
}

def alarms_strobe() {
    log.debug "alarms_strobe"
    settings.alarms?.strobe()
    pause(1000)
    settings.alarms.each {
    while ( it != null && it.latestValue("alarm") != "strobe") {
        it.strobe()
        pause(1000)
      }
    }
}

def alarms_off() {
    log.debug "alarms_off"
    settings.alarms?.off()
    pause(1000)
    settings.alarms.each {
    while ( it != null && it.latestValue("alarm") != "off") {
      it.off()
        pause(1000)
      }
    }
}

def clear() {
    log.debug "clear"
    alarms_off()
    state.alarmActive = false
    sendNotificationEvent "Alarm(s) Reset..."
}

def set() {
  log.debug "set"
  if(settings.strobe == true) {
        alarms_strobe()
  }
  sendNotificationEvent "Alarm(s) Silented!"
  runIn(settings.reset, clear, [overwrite: true])
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
      state.alarmValue = evt.value
      state.alarmActive = true

        sendNotificationEvent "Alarm(s) Active!"
        runIn(settings.delay, set, [overwrite: true])
    }
}

private def initialize() {
      state.alarmActive = false
      subscribe(alarms, "alarm", alarmHandler)
}
